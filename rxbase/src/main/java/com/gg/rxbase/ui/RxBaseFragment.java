package com.gg.rxbase.ui;

import android.support.annotation.NonNull;

import com.trello.navi2.Event;
import com.trello.navi2.NaviComponent;
import com.trello.navi2.component.support.NaviFragment;
import com.trello.navi2.rx.RxNavi;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.navi.NaviLifecycle;

import io.reactivex.Observable;

/**
 * @author Guang1234567
 * @date 2018/3/9 15:52
 */

public abstract class RxBaseFragment extends NaviFragment implements LifecycleProvider<FragmentEvent> {

    private final NaviComponent naviComponent = this;

    private final LifecycleProvider<FragmentEvent> provider
            = NaviLifecycle.createFragmentLifecycleProvider(naviComponent);

    public RxBaseFragment() {
    }

    @NonNull
    protected final <T> Observable<T> naviObserve(Event<T> naviEvent) {
        return RxNavi.observe(naviComponent, naviEvent);
    }

    @NonNull
    @Override
    public final Observable<FragmentEvent> lifecycle() {
        return provider.lifecycle();
    }

    @NonNull
    @Override
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent fragmentEvent) {
        return provider.bindUntilEvent(fragmentEvent);
    }

    @NonNull
    @Override
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return provider.bindToLifecycle();
    }
}
