package com.gg.rxbase.net.retrofit;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.FlowableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.functions.Consumer;

/**
 * @author Guang1234567
 * @date 2017/7/7 17:42
 */
public class ApiResultTransformer<DATA, ERROR>
        implements
        ObservableTransformer<ApiResult<DATA, ERROR>, ApiResult<DATA, ERROR>>,
        FlowableTransformer<ApiResult<DATA, ERROR>, ApiResult<DATA, ERROR>>,
        SingleTransformer<ApiResult<DATA, ERROR>, ApiResult<DATA, ERROR>>,
        MaybeTransformer<ApiResult<DATA, ERROR>, ApiResult<DATA, ERROR>>,
        CompletableTransformer {
    private final Consumer<Throwable> mThrowableHandler;

    public ApiResultTransformer(Consumer<Throwable> throwableHandler) {
        mThrowableHandler = throwableHandler;
    }

    @Override
    public ObservableSource<ApiResult<DATA, ERROR>> apply(final Observable<ApiResult<DATA, ERROR>> upstream) {
        return new Observable<ApiResult<DATA, ERROR>>() {
            @Override
            protected void subscribeActual(final Observer<? super ApiResult<DATA, ERROR>> downstream) {
                upstream.subscribe(new Observer<ApiResult<DATA, ERROR>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        downstream.onSubscribe(d);
                    }

                    @Override
                    public void onNext(ApiResult<DATA, ERROR> apiResult) {
                        if (apiResult.isOk()) {
                            downstream.onNext(apiResult);
                        } else {
                            onError(new ApiException(apiResult.getApiCode(), apiResult.getMsg()));
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Exception extra = null;
                        if (mThrowableHandler != null) {
                            try {
                                mThrowableHandler.accept(t);
                            } catch (Exception e) {
                                extra = e;
                            }
                        }
                        if (extra == null) {
                            downstream.onError(t);
                        } else {
                            downstream.onError(new CompositeException(t, extra));
                        }
                    }

                    @Override
                    public void onComplete() {
                        downstream.onComplete();
                    }
                });
            }
        };
    }

    @Override
    public Publisher<ApiResult<DATA, ERROR>> apply(final Flowable<ApiResult<DATA, ERROR>> upstream) {
        return new Flowable<ApiResult<DATA, ERROR>>() {
            @Override
            protected void subscribeActual(final Subscriber<? super ApiResult<DATA, ERROR>> downstream) {
                upstream.subscribe(new FlowableSubscriber<ApiResult<DATA, ERROR>>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        downstream.onSubscribe(s);
                    }

                    @Override
                    public void onNext(ApiResult<DATA, ERROR> apiResult) {
                        if (apiResult.isOk()) {
                            downstream.onNext(apiResult);
                        } else {
                            onError(new ApiException(apiResult.getApiCode(), apiResult.getMsg()));
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Exception extra = null;
                        if (mThrowableHandler != null) {
                            try {
                                mThrowableHandler.accept(t);
                            } catch (Exception e) {
                                extra = e;
                            }
                        }
                        if (extra == null) {
                            downstream.onError(t);
                        } else {
                            downstream.onError(new CompositeException(t, extra));
                        }
                    }

                    @Override
                    public void onComplete() {
                        downstream.onComplete();
                    }
                });
            }
        };
    }

    @Override
    public MaybeSource<ApiResult<DATA, ERROR>> apply(final Maybe<ApiResult<DATA, ERROR>> upstream) {
        return new Maybe<ApiResult<DATA, ERROR>>() {
            @Override
            protected void subscribeActual(final MaybeObserver<? super ApiResult<DATA, ERROR>> downstream) {
                upstream.subscribe(new MaybeObserver<ApiResult<DATA, ERROR>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        downstream.onSubscribe(d);
                    }

                    @Override
                    public void onSuccess(ApiResult<DATA, ERROR> apiResult) {
                        if (apiResult.isOk()) {
                            downstream.onSuccess(apiResult);
                        } else {
                            onError(new ApiException(apiResult.getApiCode(), apiResult.getMsg()));
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Exception extra = null;
                        if (mThrowableHandler != null) {
                            try {
                                mThrowableHandler.accept(t);
                            } catch (Exception e) {
                                extra = e;
                            }
                        }
                        if (extra == null) {
                            downstream.onError(t);
                        } else {
                            downstream.onError(new CompositeException(t, extra));
                        }
                    }

                    @Override
                    public void onComplete() {
                        downstream.onComplete();
                    }
                });
            }
        };
    }

    @Override
    public SingleSource<ApiResult<DATA, ERROR>> apply(final Single<ApiResult<DATA, ERROR>> upstream) {
        return new Single<ApiResult<DATA, ERROR>>() {
            @Override
            protected void subscribeActual(final SingleObserver<? super ApiResult<DATA, ERROR>> downstream) {
                upstream.subscribe(new SingleObserver<ApiResult<DATA, ERROR>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        downstream.onSubscribe(d);
                    }

                    @Override
                    public void onSuccess(ApiResult<DATA, ERROR> apiResult) {
                        if (apiResult.isOk()) {
                            downstream.onSuccess(apiResult);
                        } else {
                            onError(new ApiException(apiResult.getApiCode(), apiResult.getMsg()));
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Exception extra = null;
                        if (mThrowableHandler != null) {
                            try {
                                mThrowableHandler.accept(t);
                            } catch (Exception e) {
                                extra = e;
                            }
                        }
                        if (extra == null) {
                            downstream.onError(t);
                        } else {
                            downstream.onError(new CompositeException(t, extra));
                        }
                    }
                });
            }
        };
    }

    @Override
    public CompletableSource apply(final Completable upstream) {
        return new Completable() {
            @Override
            protected void subscribeActual(final CompletableObserver downstream) {
                upstream.subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        downstream.onSubscribe(d);
                    }

                    @Override
                    public void onComplete() {
                        downstream.onComplete();
                    }

                    @Override
                    public void onError(Throwable t) {
                        Exception extra = null;
                        if (mThrowableHandler != null) {
                            try {
                                mThrowableHandler.accept(t);
                            } catch (Exception e) {
                                extra = e;
                            }
                        }
                        if (extra == null) {
                            downstream.onError(t);
                        } else {
                            downstream.onError(new CompositeException(t, extra));
                        }
                    }
                });
            }
        };
    }
}
