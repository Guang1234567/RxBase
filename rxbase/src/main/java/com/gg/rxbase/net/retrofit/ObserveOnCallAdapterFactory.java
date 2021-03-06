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
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class ObserveOnCallAdapterFactory extends CallAdapter.Factory {
    final Scheduler mScheduler;

    private ObserveOnCallAdapterFactory(Scheduler scheduler) {
        mScheduler = scheduler;
    }

    public static ObserveOnCallAdapterFactory create(Scheduler scheduler) {
        return new ObserveOnCallAdapterFactory(scheduler);
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

                // ...and change it to send notifications to the observer on the specified mScheduler.
                if (isObservable) {
                    return ((Observable) o).observeOn(mScheduler);
                } else if (isFlowable) {
                    return ((Flowable) o).observeOn(mScheduler);
                } else if (isSingle) {
                    return ((Single) o).observeOn(mScheduler);
                } else if (isMaybe) {
                    return ((Maybe) o).observeOn(mScheduler);
                } else if (isCompletable) {
                    return ((Completable) o).observeOn(mScheduler);
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
