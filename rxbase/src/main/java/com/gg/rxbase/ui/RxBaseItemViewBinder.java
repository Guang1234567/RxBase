package com.gg.rxbase.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.gg.rxbase.lifecycle.RxViewHolderLifecycleProviderImpl;
import com.gg.rxbase.lifecycle.ViewHolderEvent;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.util.List;

import io.reactivex.Observable;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author Guang123456
 * @date 2018/3/30 14:42
 */

public abstract class RxBaseItemViewBinder<T, VH extends RecyclerView.ViewHolder>
        extends ItemViewBinder<T, VH>
        implements LifecycleProvider<ViewHolderEvent> {

    private final RxViewHolderLifecycleProviderImpl mLifecycleProvider;

    protected RxBaseItemViewBinder() {
        super();
        mLifecycleProvider = new RxViewHolderLifecycleProviderImpl();
    }

    @NonNull
    @Override
    public final Observable<ViewHolderEvent> lifecycle() {
        return mLifecycleProvider.lifecycle();
    }

    @NonNull
    @Override
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ViewHolderEvent event) {
        return mLifecycleProvider.bindUntilEvent(event);
    }

    @NonNull
    @Override
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return mLifecycleProvider.bindToLifecycle();
    }

    @Override
    protected void onBindViewHolder(@NonNull VH holder, @NonNull T item, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, item, payloads);
        mLifecycleProvider.onNext(ViewHolderEvent.BIND);
    }

    @Override
    protected void onBindViewHolder(@NonNull VH holder, @NonNull T item) {
        mLifecycleProvider.onNext(ViewHolderEvent.BIND);

    }

    @Override
    protected void onViewRecycled(@NonNull VH holder) {
        super.onViewRecycled(holder);
        mLifecycleProvider.onNext(ViewHolderEvent.RECYCLED);
    }


}
