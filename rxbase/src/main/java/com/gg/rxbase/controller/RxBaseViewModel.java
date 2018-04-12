package com.gg.rxbase.controller;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.gg.rxbase.lifecycle.RxViewModelLifecycleProviderImpl;
import com.gg.rxbase.lifecycle.ViewModelEvent;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;

import io.reactivex.Observable;

/**
 * @author Guang1234567
 * @date 2018/3/9 16:07
 */

public abstract class RxBaseViewModel extends ViewModel implements LifecycleProvider<ViewModelEvent> {

    private final RxViewModelLifecycleProviderImpl mLifecycleProvider;

    protected RxBaseViewModel() {
        mLifecycleProvider = new RxViewModelLifecycleProviderImpl();
        mLifecycleProvider.onNext(ViewModelEvent.CREATE);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mLifecycleProvider.onNext(ViewModelEvent.DESTROY);
    }

    @NonNull
    @Override
    public final Observable<ViewModelEvent> lifecycle() {
        return mLifecycleProvider.lifecycle();
    }

    @NonNull
    @Override
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ViewModelEvent event) {
        return mLifecycleProvider.bindUntilEvent(event);
    }

    @NonNull
    @Override
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return mLifecycleProvider.bindToLifecycle();
    }
}
