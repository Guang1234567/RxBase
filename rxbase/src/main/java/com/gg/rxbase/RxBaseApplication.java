package com.gg.rxbase;

import android.app.Application;
import android.support.annotation.NonNull;

import com.gg.rxbase.lifecycle.ApplicationEvent;
import com.gg.rxbase.lifecycle.RxApplicationLifecycleProviderImpl;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;

import io.reactivex.Observable;

/**
 * @author Guang1234567
 * @date 2017/7/13 10:45
 */

public abstract class RxBaseApplication extends Application implements LifecycleProvider<ApplicationEvent> {

    private final RxApplicationLifecycleProviderImpl mLifecycleProvider;

    public RxBaseApplication() {
        super();
        mLifecycleProvider = new RxApplicationLifecycleProviderImpl();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLifecycleProvider.onNext(ApplicationEvent.CREATE);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mLifecycleProvider.onNext(ApplicationEvent.TERMINATE);
    }

    final protected void triggerTerminateEvent() {
        mLifecycleProvider.onNext(ApplicationEvent.TERMINATE);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mLifecycleProvider.onNext(ApplicationEvent.LOW_MEMORY);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        mLifecycleProvider.onNext(ApplicationEvent.TRIM_MEMORY);
    }

    @NonNull
    @Override
    public final Observable<ApplicationEvent> lifecycle() {
        return mLifecycleProvider.lifecycle();
    }

    @NonNull
    @Override
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ApplicationEvent event) {
        return mLifecycleProvider.bindUntilEvent(event);
    }

    @NonNull
    @Override
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return mLifecycleProvider.bindToLifecycle();
    }
}
