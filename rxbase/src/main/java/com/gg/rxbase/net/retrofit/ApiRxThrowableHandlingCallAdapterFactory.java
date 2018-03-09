/*
 * Copyright (C) 2016 Square, Inc.
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

import android.content.Context;

import com.gg.rxbase.net.NetWorkUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class ApiRxThrowableHandlingCallAdapterFactory extends CallAdapter.Factory {
    private final Context mContext;
    private final ApiThrowableHandler mThrowableHandler;
    private final Scheduler mScheduler;

    private ApiRxThrowableHandlingCallAdapterFactory(Context context, ApiThrowableHandler throwableHandler, Scheduler scheduler) {
        mContext = context.getApplicationContext();
        mThrowableHandler = throwableHandler;
        mScheduler = scheduler;
    }

    public static ApiRxThrowableHandlingCallAdapterFactory create(Context context, ApiThrowableHandler errorHandler, Scheduler scheduler) {
        return new ApiRxThrowableHandlingCallAdapterFactory(context, errorHandler, scheduler);
    }

    public static ApiRxThrowableHandlingCallAdapterFactory create(Context context, ApiThrowableHandler errorHandler) {
        return create(context, errorHandler, null);
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != Observable.class) {
            return null; // Ignore non-Observable types.
        }

        // Look up the next call adapter which would otherwise be used if this one was not present.
        //noinspection unchecked returnType checked above to be Observable.
        final CallAdapter<Object, Observable<?>> delegate =
                (CallAdapter<Object, Observable<?>>) retrofit.nextCallAdapter(this, returnType,
                        annotations);

        return new CallAdapter<Object, Object>() {
            @Override
            public Object adapt(Call<Object> call) {
                // Delegate to get the normal Observable...
                Observable<?> o = delegate.adapt(call);
                // ...and change it to send notifications to the observer on the specified scheduler.
                o = o.doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (!NetWorkUtils.isConnectedByState(mContext)) {
                            //订阅此Observable时, 如果网络不可用,则主动抛出下面的异常
                            throw new ApiException(ApiErrorCode.ERROR_NO_INTERNET, "network unavailable");
                        }
                    }
                });
                if (mScheduler != null) {
                    o = o.observeOn(mScheduler); //使 doOnError 在指定的线程中运行
                }
                return o.doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mThrowableHandler != null) {
                            mThrowableHandler.accept(throwable);
                        }
                    }
                });
            }

            @Override
            public Type responseType() {
                return delegate.responseType();
            }
        };
    }
}
