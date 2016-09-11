package com.wolfie.eskey.custom.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.StringDef;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wolfie.eskey.R;
import com.wolfie.eskey.custom.model.Entry;
import com.wolfie.eskey.custom.model.EntryGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 11/09/16.
 */

public class GroupingRecyclerAdapter
        extends PlaceholderRecyclerAdapter<GroupingRecyclerAdapter.GroupingViewHolder> {

    private static final int VIEW_TYPE_TITLE = 0;
    private static final int VIEW_TYPE_ENTRY = 1;

    private OnItemInListClickedListener mOnItemInListClickedListener;
    private List<EntryGroup> mGroups = new ArrayList<>();

    @Override
    public GroupingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == -1) {
            return null;
        }

        View view;
        final GroupingViewHolder viewHolder;
        switch (viewType) {
            case VIEW_TYPE_TITLE:
                view = inflateView(parent, R.layout.entry_list_heading);
                viewHolder = new HeadingViewHolder(view);
                break;
            case VIEW_TYPE_ENTRY:
            default:
                view = inflateView(parent, R.layout.entry_list_item);
                viewHolder = new ItemViewHolder(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnItemInListClickedListener != null) {
                            int position = viewHolder.getAdapterPosition();
                            Entry entry = (Entry) getItemAt(position);
                            mOnItemInListClickedListener.onListItemClick(entry);
                        }
                    }
                });
                break;
        }
        return viewHolder;
    }

    private View inflateView(ViewGroup parent, @LayoutRes int layout) {
        return LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
    }

    /**
     * Update the viewHolder with the contents of the item at the given position in the data set.
     */
    @Override
    public void onBindViewHolder(GroupingViewHolder holder, int position) {
        Object item = getItemAt(position);
        if (holder != null && item != null) {
            holder.bind(item);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = -1;
        Object item = getItemAt(position);
        if (item != null) {
            if (item instanceof String) {
                viewType = VIEW_TYPE_TITLE;
            } else {
                viewType = VIEW_TYPE_ENTRY;
            }
        }
        return viewType;
    }

    /**
     * The data in mGroups is mapped to the adapter one to one, plus one item for the
     * heading of each group.  An EntryGroup must not have null fields.
     * @return
     */
    @Override
    public int getItemCount() {
        int count = 0;
        for (EntryGroup group : mGroups) {
            ++count;
            count += group.getEntries().size();
        }
        return count;
    }

    /**
     * @return either the String (heading) or Entry at the specified position in the adapter.
     */
    public Object getItemAt(int position) {
        int count = 0;
        for (EntryGroup group : mGroups) {
            // Check if the heading is at the position.
            if (count++ == position) {
                return group.getHeading();
            }
            // Skip this part if there's no items in the group
            // Only iterate through the list if we know the position falls within this list
            if (count + group.getEntries().size() > position) {
                for (Entry entry : group.getEntries()) {
                    if (count++ == position) {
                        return entry;
                    }
                }
            } else {
                count += group.getEntries().size();
            }
        }
        return null;
    }

    public void setGroups(List<EntryGroup> groups) {
        mGroups.clear();
        mGroups.addAll(groups);
        notifyDataSetChanged();
    }

    public void clearItems() {
        mGroups.clear();
        notifyDataSetChanged();
    }

    public void setOnItemInListClickerListener(OnItemInListClickedListener listener) {
        mOnItemInListClickedListener = listener;
    }

    public interface OnItemInListClickedListener {
        void onListItemClick(Entry selectedEntry);
    }

    public abstract static class GroupingViewHolder extends RecyclerView.ViewHolder {
        public GroupingViewHolder(View itemView) {
            super(itemView);
        }
        public abstract void bind(Object item);
    }

    public static class HeadingViewHolder extends GroupingViewHolder {
        private TextView mTextView;
        public HeadingViewHolder(View view) {
            super(view);
            mTextView = (TextView)view.findViewById(R.id.text_heading);
        }
        public void bind(Object item) {
            String text = (String)item;
            mTextView.setText(text);
        }
    }

    public static class ItemViewHolder extends GroupingViewHolder {
        private TextView mTextView;
        public ItemViewHolder(View view) {
            super(view);
            mTextView = (TextView)view.findViewById(R.id.text_item);
        }
        public void bind(Object item) {
            Entry entry = (Entry)item;
            mTextView.setText(entry.getEntryName());
        }
    }
}
