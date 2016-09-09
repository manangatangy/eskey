package com.wolfie.eskey.zzzdeprecated.recycleadapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by david on 29/08/16.
 */

public class EntryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    /**
     * Replace the contents of the view (specified by the viewHolder) with the element
     * at the specified position in the dataset.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
//        switch (viewHolder.getItemViewType()) {
//
//            case TYPE_IMAGE:
//                ImageViewHolder imageViewHolder = (ImageViewHolder) viewHolder;
//                imageViewHolder.mImage.setImageResource(...);
//                break;
//
//            case TYPE_GROUP:
//                GroupViewHolder groupViewHolder = (GroupViewHolder) viewHolder;
//                groupViewHolder.mContent.setText(...)
//                groupViewHolder.mTitle.setText(...);
//                break;
//        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    /**
     * Return the item type of the element at the specified position in the dataset.
     */
    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return position % 2 * 2;
    }

}
