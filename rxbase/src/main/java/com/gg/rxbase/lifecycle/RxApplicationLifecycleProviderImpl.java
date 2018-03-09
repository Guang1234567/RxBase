package com.gg.rxbase.lifecycle;

import android.support.annotation.NonNull;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * @author Guang1234567
 * @date 2017/7/13 10:46
 */

public class RxApplicationLifecycleProviderImpl implements LifecycleProvider<ApplicationEvent> {
    private final BehaviorSubject<ApplicationEvent> mLifecycleSubject;

    public RxApplicationLifecycleProviderImpl() {
        mLifecycleSubject = BehaviorSubject.create();
    }

    public void onNext(@NonNull ApplicationEvent event) {
        mLifecycleSubject.onNext(event);
    }

    @NonNull
    @Override
    public Observable<ApplicationEvent> lifecycle() {
        return mLifecycleSubject.hide();
    }

    @NonNull
    @Override
    public <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ApplicationEvent event) {
        return RxLifecycle.bindUntilEvent(mLifecycleSubject, event);
    }

    @NonNull
    @Override
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycle.bind(mLifecycleSubject, ApplicationEvent.VIEWHOLDER_LIFECYCLE);
    }
}
