package com.gg.rxbase.lifecycle;

import com.trello.rxlifecycle2.OutsideLifecycleException;

import io.reactivex.functions.Function;

/**
 * @author Guang1234567
 * @date 2018/3/9 14:27
 */

public enum ViewHolderEvent {
    BIND,
    RECYCLED;

    public final static Function<ViewHolderEvent, ViewHolderEvent> VIEWHOLDER_LIFECYCLE =
            new Function<ViewHolderEvent, ViewHolderEvent>() {
                @Override
                public ViewHolderEvent apply(ViewHolderEvent lastEvent) throws Exception {
                    switch (lastEvent) {
                        case BIND:
                            return RECYCLED;
                        case RECYCLED:
                            throw new OutsideLifecycleException("Cannot bind to ViewHolder lifecycle when outside of it.");
                        default:
                            throw new UnsupportedOperationException("Binding to " + lastEvent + " not yet implemented");
                    }
                }
            };
}
