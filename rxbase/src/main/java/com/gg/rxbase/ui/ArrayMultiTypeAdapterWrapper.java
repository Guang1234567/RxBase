package com.gg.rxbase.ui;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author Guang1234567
 * @date 2018/3/30 16:36
 */

public class ArrayMultiTypeAdapterWrapper<T, A extends MultiTypeAdapter> extends BaseRecyclerViewAdapterWrapper<A> {

    private List<T> mItems;

    private boolean mNotifyOnChange;

    public ArrayMultiTypeAdapterWrapper(@NonNull A adapter) {
        super(adapter);

        mItems = (List<T>) adapter.getItems();
        mNotifyOnChange = true;
    }

    public void changeAll(List<T> newData) {
        if (newData == null) {
            newData = new LinkedList<>();
        }
        mItems = newData;
        getInnerAdapter().setItems(newData);
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

    public void sort(@NonNull Comparator<? super T> comparator) {
        Collections.sort(mItems, comparator);
        if (mNotifyOnChange)
            getInnerAdapter().notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        if (mNotifyOnChange)
            getInnerAdapter().notifyDataSetChanged();
    }

    public void beginBulkAction() {
        mNotifyOnChange = false;
    }

    public void endBulkAction() {
        mNotifyOnChange = true;
        getInnerAdapter().notifyDataSetChanged();
    }
}
