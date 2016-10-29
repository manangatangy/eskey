package com.wolfie.eskey.view.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wolfie.eskey.R;
import com.wolfie.eskey.view.adapter.viewholder.BaseViewHolder;
import com.wolfie.eskey.view.adapter.viewholder.HeadingViewHolder;
import com.wolfie.eskey.view.adapter.viewholder.ItemViewHolder;
import com.wolfie.eskey.model.Entry;
import com.wolfie.eskey.model.EntryGroup;

import java.util.ArrayList;
import java.util.List;

public class GroupingRecyclerAdapter
        extends PlaceholderRecyclerAdapter<BaseViewHolder> {

    private static final int VIEW_TYPE_TITLE = 0;
    private static final int VIEW_TYPE_ENTRY = 1;

    private OnItemInListClickedListener mOnItemInListClickedListener;
    private List<EntryGroup> mGroups = new ArrayList<>();
    private static Context mContext;

    public GroupingRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == -1) {
            return null;
        }

        View view;
        final BaseViewHolder viewHolder;
        switch (viewType) {
            case VIEW_TYPE_TITLE:
                view = inflateView(parent, R.layout.view_list_heading);
                viewHolder = new HeadingViewHolder(view);
                break;
            case VIEW_TYPE_ENTRY:
            default:
                view = inflateView(parent, R.layout.view_list_item);
                viewHolder = new ItemViewHolder(view, mOnItemInListClickedListener);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ItemViewHolder)viewHolder).toggleDetailView();
//                        if (mOnItemInListClickedListener != null) {
//                            int position = viewHolder.getAdapterPosition();
//                            Entry entry = (Entry) getItemAt(position);
//                            mOnItemInListClickedListener.onListItemClick(entry);
//                        }
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
    public void onBindViewHolder(BaseViewHolder holder, int position) {
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

}
