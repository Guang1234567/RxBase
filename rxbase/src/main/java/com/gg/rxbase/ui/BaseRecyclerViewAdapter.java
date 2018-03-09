package com.gg.rxbase.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gg.rxbase.lifecycle.RxViewHolderLifecycleProviderImpl;
import com.gg.rxbase.lifecycle.ViewHolderEvent;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.util.List;

import io.reactivex.Observable;

/**
 * 封装常用功能的 RecyclerView.Adapter 的子类.
 * <p>
 * 建议不要直接继承 RecyclerView.Adapter 而是继承它.
 *
 * @author Guang1234567
 * @date 2017/5/13 17:52
 */

public abstract class BaseRecyclerViewAdapter extends RecyclerView.Adapter<BaseRecyclerViewAdapter.BaseViewHolder> {

    public abstract static class BaseViewHolder<T, A extends BaseRecyclerViewAdapter> extends RecyclerView.ViewHolder {

        protected BaseViewHolder(View itemView) {
            super(itemView);
        }

        protected abstract void onViewRecycled(A outerAdapter);

        protected abstract void onBindViewHolder(T item, int position, A outerAdapter);

        protected abstract void onBindViewHolder(T item, int position, List<Object> payloads, A outerAdapter);
    }

    public abstract static class RxBaseViewHolder<T, A extends BaseRecyclerViewAdapter>
            extends BaseViewHolder<T, A>
            implements LifecycleProvider<ViewHolderEvent> {

        private final RxViewHolderLifecycleProviderImpl mLifecycleProvider;

        protected RxBaseViewHolder(View itemView) {
            super(itemView);
            mLifecycleProvider = new RxViewHolderLifecycleProviderImpl();
        }

        @Override
        protected void onViewRecycled(A outerAdapter) {
            mLifecycleProvider.onNext(ViewHolderEvent.RECYCLED);
        }

        @Override
        protected void onBindViewHolder(T item, int position, A outerAdapter) {
            mLifecycleProvider.onNext(ViewHolderEvent.BIND);
        }

        @Override
        protected void onBindViewHolder(T item, int position, List<Object> payloads, A outerAdapter) {
            mLifecycleProvider.onNext(ViewHolderEvent.BIND);
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
    }
}
