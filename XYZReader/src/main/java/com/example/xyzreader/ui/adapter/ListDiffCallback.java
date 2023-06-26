package com.example.xyzreader.ui.adapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class ListDiffCallback<T> extends DiffUtil.Callback {

    private List<T> mOld;
    private List<T> mNew;

    public ListDiffCallback(List<T> oldList , List<T> newList ) {
        super();
        mOld = oldList;
        mNew = newList;
    }


    @Override
    public int getOldListSize() {
        return mOld.size();
    }

    @Override
    public int getNewListSize() {
        return mNew.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOld.get(oldItemPosition).equals( mNew.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mOld.get(oldItemPosition).equals( mNew.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //you can return particular field for changed item.
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }

}
