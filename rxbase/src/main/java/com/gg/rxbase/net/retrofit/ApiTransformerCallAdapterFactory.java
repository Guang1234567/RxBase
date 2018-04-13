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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Completable;
import io.reactivex.CompletableTransformer;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleTransformer;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class ApiTransformerCallAdapterFactory extends CallAdapter.Factory {

    private final ObservableTransformer mObservableTransformer;
    private final FlowableTransformer mFlowableTransformer;
    private final SingleTransformer mSingleTransformer;
    private final MaybeTransformer mMaybeTransformer;
    private final CompletableTransformer mCompletableTransformer;

    private ApiTransformerCallAdapterFactory(ObservableTransformer observableTransformer,
                                             FlowableTransformer flowableTransformer,
                                             SingleTransformer singleTransformer,
                                             MaybeTransformer maybeTransformer,
                                             CompletableTransformer completableTransformer) {

        mObservableTransformer = observableTransformer;
        mFlowableTransformer = flowableTransformer;
        mSingleTransformer = singleTransformer;
        mMaybeTransformer = maybeTransformer;
        mCompletableTransformer = completableTransformer;
    }

    public static ApiTransformerCallAdapterFactory create(ObservableTransformer observableTransformer,
                                                          FlowableTransformer flowableTransformer,
                                                          SingleTransformer singleTransformer,
                                                          MaybeTransformer maybeTransformer,
                                                          CompletableTransformer completableTransformer) {
        return new ApiTransformerCallAdapterFactory(observableTransformer,
                flowableTransformer,
                singleTransformer,
                maybeTransformer,
                completableTransformer);
    }


    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        Class<?> rawType = getRawType(returnType);

        final boolean isObservable = rawType == Observable.class;
        final boolean isFlowable = rawType == Flowable.class;
        final boolean isSingle = rawType == Single.class;
        final boolean isMaybe = rawType == Maybe.class;
        final boolean isCompletable = rawType == Completable.class;
        if (!isObservable
                && !isFlowable
                && !isSingle
                && !isMaybe
                && !isCompletable) {
            return null;
        }

        // Look up the next call adapter which would otherwise be used if this one was not present.
        //noinspection unchecked returnType checked above to be Observable.
        final CallAdapter<Object, ?> delegate =
                (CallAdapter<Object, ?>) retrofit.nextCallAdapter(this, returnType,
                        annotations);

        return new CallAdapter<Object, Object>() {
            @Override
            public Object adapt(Call<Object> call) {
                // Delegate to get the normal Observable...
                Object o = delegate.adapt(call);

                // ...and change it to send notifications to the compose the specified mXXXTransformer.
                if (isObservable) {
                    return ((Observable) o).compose(mObservableTransformer);
                } else if (isFlowable) {
                    return ((Flowable) o).compose(mFlowableTransformer);
                } else if (isSingle) {
                    return ((Single) o).compose(mSingleTransformer);
                } else if (isMaybe) {
                    return ((Maybe) o).compose(mMaybeTransformer);
                } else if (isCompletable) {
                    return ((Completable) o).compose(mCompletableTransformer);
                } else {
                    return o;
                }
            }

            @Override
            public Type responseType() {
                return delegate.responseType();
            }
        };
    }
}
