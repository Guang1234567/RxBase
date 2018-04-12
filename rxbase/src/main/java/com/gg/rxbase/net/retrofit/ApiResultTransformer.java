package com.gg.rxbase.net.retrofit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author Guang1234567
 * @date 2017/7/7 17:42
 */
public class ApiResultTransformer<DATA, ERROR> implements ObservableTransformer<ApiResult<DATA, ERROR>, ApiResult<DATA, ERROR>> {
    private final ApiThrowableHandler mThrowableHandler;

    public ApiResultTransformer(ApiThrowableHandler throwableHandler) {
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
                    public void onError(Throwable e) {
                        if (mThrowableHandler != null) {
                            boolean isConsume = false;
                            Throwable exp = null;
                            try {
                                isConsume = mThrowableHandler.accept(e);
                            } catch (Throwable tr) {
                                exp = tr;
                            } finally {
                                if (exp != null) {
                                    downstream.onError(exp);
                                } else if (!isConsume) {
                                    downstream.onError(e);
                                } else {
                                    onComplete();
                                }
                            }
                        } else {
                            downstream.onError(e);
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
}
