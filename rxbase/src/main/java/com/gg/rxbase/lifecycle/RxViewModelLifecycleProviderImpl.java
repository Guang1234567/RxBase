package com.gg.rxbase.lifecycle;

import android.support.annotation.NonNull;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * @author Guang1234567
 * @date 2018/3/9 14:28
 */

public final class RxViewModelLifecycleProviderImpl implements LifecycleProvider<ViewModelEvent> {

    private final BehaviorSubject<ViewModelEvent> mLifecycleSubject;

    public RxViewModelLifecycleProviderImpl() {
        mLifecycleSubject = BehaviorSubject.create();
    }

    public void onNext(@NonNull ViewModelEvent event) {
        mLifecycleSubject.onNext(event);
    }

    @NonNull
    @Override
    public Observable<ViewModelEvent> lifecycle() {
        return mLifecycleSubject.hide();
    }

    @NonNull
    @Override
    public <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ViewModelEvent event) {
        return RxLifecycle.bindUntilEvent(mLifecycleSubject, event);
    }

    @NonNull
    @Override
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycle.bind(mLifecycleSubject, ViewModelEvent.VIEWMODEL_LIFECYCLE);
    }
}