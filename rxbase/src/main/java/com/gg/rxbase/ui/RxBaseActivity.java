package com.gg.rxbase.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.gg.rxbase.thread.RxSchedulers;
import com.trello.navi2.Event;
import com.trello.navi2.NaviComponent;
import com.trello.navi2.component.support.NaviAppCompatActivity;
import com.trello.navi2.rx.RxNavi;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.navi.NaviLifecycle;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author Guang1234567
 * @date 2017/5/17 18:33:53
 */

public abstract class RxBaseActivity extends NaviAppCompatActivity implements LifecycleProvider<ActivityEvent> {
    private final NaviComponent naviComponent = this;

    private final LifecycleProvider<ActivityEvent> provider
            = NaviLifecycle.createActivityLifecycleProvider(naviComponent);

    public RxBaseActivity() {
        naviObserve(Event.CREATE).subscribe(new Consumer<Bundle>() {
            @Override
            public void accept(Bundle bundle) throws Exception {
                //setContentView(R.layout.main);
            }
        });

        // Counter that operates on screen only while resumed; automatically ends itself on destroy
        naviObserve(Event.RESUME)
                .flatMap(new Function<Object, Observable<Long>>() {

                    @Override
                    public Observable<Long> apply(Object v) {
                        return Observable.interval(1, TimeUnit.SECONDS)
                                .takeUntil(naviObserve(Event.PAUSE));
                    }
                })
                .compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                .startWith(-1L)
                .observeOn(RxSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long count) {
                    }
                });
    }

    @NonNull
    protected final <T> Observable<T> naviObserve(Event<T> naviEvent) {
        return RxNavi.observe(naviComponent, naviEvent);
    }

    @NonNull
    @Override
    public final Observable<ActivityEvent> lifecycle() {
        return provider.lifecycle();
    }

    @NonNull
    @Override
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ActivityEvent activityEvent) {
        return provider.bindUntilEvent(activityEvent);
    }

    @NonNull
    @Override
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return provider.bindToLifecycle();
    }
}
