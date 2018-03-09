package com.gg.rxbase.lifecycle;

import com.trello.rxlifecycle2.OutsideLifecycleException;

import io.reactivex.functions.Function;

/**
 * @author Guang1234567
 * @date 2018/3/9 14:27
 */

public enum ViewModelEvent {
    CREATE,
    DESTROY;

    public final static Function<ViewModelEvent, ViewModelEvent> VIEWMODEL_LIFECYCLE =
            new Function<ViewModelEvent, ViewModelEvent>() {
                @Override
                public ViewModelEvent apply(ViewModelEvent lastEvent) throws Exception {
                    switch (lastEvent) {
                        case CREATE:
                            return DESTROY;
                        case DESTROY:
                            throw new OutsideLifecycleException("Cannot bind to ViewModel lifecycle when outside of it.");
                        default:
                            throw new UnsupportedOperationException("Binding to " + lastEvent + " not yet implemented");
                    }
                }
            };
}
