package com.wolfie.eskey.view.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by david on 18/09/16.
 */

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    protected ViewGroup mItemView;
    public BaseViewHolder(View itemView) {
        super(itemView);
        mItemView = (ViewGroup)itemView;
    }
    public abstract void bind(Object item);
}
