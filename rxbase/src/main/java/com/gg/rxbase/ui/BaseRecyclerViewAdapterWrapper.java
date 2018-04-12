package com.gg.rxbase.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * https://github.com/drakeet/MultiType/issues/194
 *
 * @author Guang1234567
 * @date 2017/5/13 17:52
 */

public abstract class BaseRecyclerViewAdapterWrapper<A extends RecyclerView.Adapter> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private A mInnerAdapter;

    protected BaseRecyclerViewAdapterWrapper(@NonNull A innerAdapter) {
        super();
        mInnerAdapter = innerAdapter;
    }

    final protected A getInnerAdapter() {
        return mInnerAdapter;
    }

    @Override
    final public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        mInnerAdapter.onBindViewHolder(holder, position, payloads);
    }

    @Override
    final public int getItemViewType(int position) {
        return mInnerAdapter.getItemViewType(position);
    }

    @Override
    final public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
        mInnerAdapter.setHasStableIds(hasStableIds);
    }

    @Override
    final public long getItemId(int position) {
        return mInnerAdapter.getItemId(position);
    }

    @Override
    final public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        mInnerAdapter.onViewRecycled(holder);
    }

    @Override
    final public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        return mInnerAdapter.onFailedToRecycleView(holder);
    }

    @Override
    final public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        mInnerAdapter.onViewAttachedToWindow(holder);
    }

    @Override
    final public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        mInnerAdapter.onViewDetachedFromWindow(holder);
    }

    @Override
    final public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        mInnerAdapter.registerAdapterDataObserver(observer);
    }

    @Override
    final public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
        mInnerAdapter.unregisterAdapterDataObserver(observer);
    }

    @Override
    final public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mInnerAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    final public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mInnerAdapter.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    final public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mInnerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    final public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mInnerAdapter.onBindViewHolder(holder, position);
    }

    @Override
    final public int getItemCount() {
        return mInnerAdapter.getItemCount();
    }

}
