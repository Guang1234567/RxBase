/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gg.rxbase.net.retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A sample showing a custom {@link CallAdapter} which adapts the built-in {@link Call} to a custom
 * version whose callback has more granular methods.
 */
public final class ErrorHandlingAdapter {
    /**
     * A callback which offers granular callbacks for various conditions.
     */
    interface MyCallback<T> {
        /**
         * Called for [200, 300) responses.
         */
        void success(MyCall<T> call, Response<T> response);

        /**
         * Called for 401 responses.
         */
        void unauthenticated(MyCall<T> call, Response<?> response);

        /**
         * Called for [400, 500) responses, except 401.
         */
        void clientError(MyCall<T> call, Response<?> response);

        /**
         * Called for [500, 600) response.
         */
        void serverError(MyCall<T> call, Response<?> response);

        /**
         * Called for network errors while making the call.
         */
        void networkError(MyCall<T> call, IOException e);

        /**
         * Called for unexpected errors while making the call.
         */
        void unexpectedError(MyCall<T> call, Throwable t);
    }

    interface MyCall<T> {
        Call<T> getCall();

        void cancel();

        void enqueue(MyCallback<T> callback);

        MyCall<T> clone();

        void execute(MyCallback<T> callback);
    }

    public static class ErrorHandlingCallAdapterFactory extends CallAdapter.Factory {
        @Override
        public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations,
                                     Retrofit retrofit) {
            if (getRawType(returnType) != MyCall.class) {
                return null;
            }
            if (!(returnType instanceof ParameterizedType)) {
                throw new IllegalStateException(
                        "MyCall must have generic type (e.g., MyCall<ResponseBody>)");
            }
            Type responseType = getParameterUpperBound(0, (ParameterizedType) returnType);
            Executor callbackExecutor = retrofit.callbackExecutor();
            return new ErrorHandlingCallAdapter<>(responseType, callbackExecutor);
        }

        private static final class ErrorHandlingCallAdapter<R> implements CallAdapter<R, MyCall<R>> {
            private final Type responseType;
            private final Executor callbackExecutor;

            ErrorHandlingCallAdapter(Type responseType, Executor callbackExecutor) {
                this.responseType = responseType;
                this.callbackExecutor = callbackExecutor;
            }

            @Override
            public Type responseType() {
                return responseType;
            }

            @Override
            public MyCall<R> adapt(Call<R> call) {
                return new MyCallAdapter<>(call, callbackExecutor);
            }
        }
    }

    /**
     * Adapts a {@link Call} to {@link MyCall}.
     */
    static class MyCallAdapter<T> implements MyCall<T> {
        private final Call<T> call;
        private final Executor callbackExecutor;

        MyCallAdapter(Call<T> call, Executor callbackExecutor) {
            this.call = call;
            this.callbackExecutor = callbackExecutor;
        }

        @Override
        public Call<T> getCall() {
            return call;
        }

        @Override
        public void cancel() {
            call.cancel();
        }

        @Override
        public void enqueue(final MyCallback<T> callback) {
            call.enqueue(new Callback<T>() {
                @Override
                public void onResponse(Call<T> call, final Response<T> response) {
                    MyCallAdapter.this.onResponse(response, callback);
                }

                @Override
                public void onFailure(Call<T> call, final Throwable t) {
                    MyCallAdapter.this.onFailure(t, callback);
                }
            });
        }

        @Override
        public MyCall<T> clone() {
            return new MyCallAdapter<>(call.clone(), callbackExecutor);
        }

        @Override
        public void execute(final MyCallback<T> callback) {
            try {
                final Response<T> response = call.execute();
                onResponse(response, callback);
            } catch (final Throwable t) {
                onFailure(t, callback);
            }
        }

        private void onResponse(final Response<T> response, final MyCallback<T> callback) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    int code = response.code();
                    if (code >= 200 && code < 300) {
                        callback.success(MyCallAdapter.this, response);
                    } else if (code == 401) {
                        callback.unauthenticated(MyCallAdapter.this, response);
                    } else if (code >= 400 && code < 500) {
                        callback.clientError(MyCallAdapter.this, response);
                    } else if (code >= 500 && code < 600) {
                        callback.serverError(MyCallAdapter.this, response);
                    } else {
                        callback.unexpectedError(MyCallAdapter.this, new RuntimeException("Unexpected response " + response));
                    }
                }
            };
            if (callbackExecutor != null) {
                callbackExecutor.execute(r);
            } else {
                r.run();
            }
        }

        private void onFailure(final Throwable t, final MyCallback<T> callback) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    if (t instanceof IOException) {
                        callback.networkError(MyCallAdapter.this, (IOException) t);
                    } else {
                        callback.unexpectedError(MyCallAdapter.this, t);
                    }
                }
            };
            if (callbackExecutor != null) {
                callbackExecutor.execute(r);
            } else {
                r.run();
            }
        }
    }
}
