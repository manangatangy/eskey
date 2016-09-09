package com.wolfie.eskey.custom.adapter;

import android.content.Context;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wolfie.eskey.custom.model.Entry;
import com.wolfie.eskey.R;

import java.util.List;

/**
 * Created by david on 8/09/16.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context mContext;
    private List<Entry> mEntries;

    public RecyclerAdapter(Context context) {
        mContext = context;
    }

    public void setEntries(List<Entry> entries) {
        mEntries = entries;
        System.out.println("RecyclerAdapter received " + getItemCount() + " entries");
        notifyDataSetChanged();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.entry_list_item, null);
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the entry from the data at this position
        if (position < getItemCount()) {
            Entry entry = mEntries.get(position);
            ((ItemViewHolder)holder).bind(entry);
        }
    }

    @Override
    public int getItemCount() {
        return (mEntries == null) ? 0 : mEntries.size();
    }

    class ItemViewHolder extends ViewHolder {
        private TextView mTextView;

        public ItemViewHolder(View view) {
            super(view);
            mTextView = (TextView)view.findViewById(R.id.text_name);
        }

        public void bind(Entry entry) {
            mTextView.setText(entry.getGroupName() + "/" + entry.getEntryName());
        }
    }

}
