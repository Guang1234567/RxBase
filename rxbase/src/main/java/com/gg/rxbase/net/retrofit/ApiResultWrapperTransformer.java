package com.gg.rxbase.net.retrofit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;

/**
 * @author Guang1234567
 * @date 2017/7/7 17:42
 */

public class ApiResultWrapperTransformer<DATA, ERROR> implements ObservableTransformer<ApiResult<DATA, ERROR>, ApiResultWrapper<DATA, ERROR>> {
    @Override
    public ObservableSource<ApiResultWrapper<DATA, ERROR>> apply(Observable<ApiResult<DATA, ERROR>> upstream) {
        return Observable.just(asWrapper(upstream));
    }

    public static <DATA, ERROR> ApiResultWrapper<DATA, ERROR> asWrapper(Observable<ApiResult<DATA, ERROR>> o) {

        // 分流
        final Observable<ApiResult<DATA, ERROR>> publish = o
                .publish()
                .autoConnect(3);

        // 处理Throwable结果的分支
        final Observable<Throwable> onThrowable = Observable.create(new ObservableOnSubscribe<Throwable>() {
            @Override
            public void subscribe(final ObservableEmitter<Throwable> emitter) throws Exception {
                Disposable disposable = publish.subscribe(
                        Functions.<ApiResult<DATA, ERROR>>emptyConsumer(),
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                emitter.onNext(throwable);
                                emitter.onComplete();
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                emitter.onComplete();
                            }
                        });
                emitter.setDisposable(disposable);
            }
        });

        // 处理成功结果的分支
        final Observable<DATA> onOk = Observable.create(new ObservableOnSubscribe<DATA>() {
            @Override
            public void subscribe(final ObservableEmitter<DATA> emitter) throws Exception {
                Disposable disposable = publish.compose(new ApiOkResultTransformer()).subscribe(
                        new Consumer<DATA>() {
                            @Override
                            public void accept(DATA data) throws Exception {
                                emitter.onNext(data);
                                emitter.onComplete();
                            }
                        },
                        Functions.<Throwable>emptyConsumer(),
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                emitter.onComplete();
                            }
                        });
                emitter.setDisposable(disposable);
            }
        });

        // 处理失败结果的分支
        final Observable<ERROR> onError = Observable.create(new ObservableOnSubscribe<ERROR>() {
            @Override
            public void subscribe(final ObservableEmitter<ERROR> emitter) throws Exception {
                Disposable disposable = publish.compose(new ApiErrorResultTransformer()).subscribe(
                        new Consumer<ERROR>() {
                            @Override
                            public void accept(ERROR error) throws Exception {
                                emitter.onNext(error);
                                emitter.onComplete();
                            }
                        },
                        Functions.<Throwable>emptyConsumer(),
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                emitter.onComplete();
                            }
                        });
                emitter.setDisposable(disposable);
            }
        });

        return new ApiResultWrapper<DATA, ERROR>() {
            @Override
            public Observable<DATA> onOk() {
                return onOk;
            }

            @Override
            public Observable<ERROR> onError() {
                return onError;
            }

            @Override
            public Observable<Throwable> onThrowable() {
                return onThrowable;
            }
        };
    }
}
