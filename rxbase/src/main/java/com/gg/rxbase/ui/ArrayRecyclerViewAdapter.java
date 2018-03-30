package com.gg.rxbase.ui;

import android.support.v7.widget.RecyclerView;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Administrator
 * @date 2018/3/30 16:36
 */

public abstract class ArrayRecyclerViewAdapter<T, A extends RecyclerView.Adapter> extends BaseRecyclerViewAdapterWrapper<A> {

    private List<T> mItems;

    private boolean mNotifyOnChange = true;

    public ArrayRecyclerViewAdapter(A adapter) {
        super(adapter);
    }

    public void changeAll(List<T> newData) {
        if (newData == null) {
            newData = new LinkedList<>();
        }
        mItems = newData;
        if (mNotifyOnChange)
            getInnerAdapter().notifyDataSetChanged();
    }

    public void insert(T newOne) {
        if (mNotifyOnChange) {
            int insertIdx = mItems.size();
            mItems.add(insertIdx, newOne);
            getInnerAdapter().notifyItemInserted(insertIdx);
        } else {
            mItems.add(newOne);
        }
    }

    public void insertSome(Collection<? extends T> some) {
        if (mNotifyOnChange) {
            int insertIdx = mItems.size();
            if (mItems.addAll(insertIdx, some)) {
                getInnerAdapter().notifyItemRangeInserted(insertIdx, some.size());
            }
        } else {
            mItems.addAll(some);
        }
    }

    public void insertSome(T... some) {
        if (mNotifyOnChange) {
            int insertIdx = mItems.size();
            if (Collections.addAll(mItems, some)) {
                getInnerAdapter().notifyItemRangeInserted(insertIdx, some.length);
            }
        } else {
            Collections.addAll(mItems, some);
        }
    }

    public void delete(T oldOne) {
        if (mNotifyOnChange) {
            int deleteIdx = mItems.indexOf(oldOne);
            if (deleteIdx == -1) {
                return;
            }
            mItems.remove(deleteIdx);
            getInnerAdapter().notifyItemRemoved(deleteIdx);
        } else {
            mItems.remove(oldOne);
        }
    }

    public void update(T oldOne) {
        int updateIdx = mItems.indexOf(oldOne);
        if (updateIdx == -1) {
            return;
        }
        mItems.set(updateIdx, oldOne);
        if (mNotifyOnChange)
            getInnerAdapter().notifyItemChanged(updateIdx);
    }

    public void update(T oldOne, Object payload) {
        int updateIdx = mItems.indexOf(oldOne);
        if (updateIdx == -1) {
            return;
        }
        mItems.set(updateIdx, oldOne);
        if (mNotifyOnChange)
            getInnerAdapter().notifyItemChanged(updateIdx, payload);
    }

    public void clear() {
        mItems.clear();
        if (mNotifyOnChange)
            getInnerAdapter().notifyDataSetChanged();
    }

    public void beginBulkModify() {
        mNotifyOnChange = false;
    }

    public void endBulkModify() {
        mNotifyOnChange = true;
    }
}
