package com.gg.rxbase.lifecycle;

import com.trello.rxlifecycle2.OutsideLifecycleException;

import io.reactivex.functions.Function;

/**
 * @author Guang1234567
 * @date 2017/7/13 10:30
 */

public enum ApplicationEvent {
    CREATE,
    TERMINATE,
    LOW_MEMORY,
    TRIM_MEMORY;

    public final static Function<ApplicationEvent, ApplicationEvent> VIEWHOLDER_LIFECYCLE =
            new Function<ApplicationEvent, ApplicationEvent>() {
                @Override
                public ApplicationEvent apply(ApplicationEvent lastEvent) throws Exception {
                    switch (lastEvent) {
                        case CREATE:
                            return TERMINATE;
                        case TERMINATE:
                            throw new OutsideLifecycleException("Cannot bind to Application lifecycle when outside of it.");
                        default:
                            throw new UnsupportedOperationException("Binding to " + lastEvent + " not yet implemented");
                    }
                }
            };
}
