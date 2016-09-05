package com.wolfie.eskey.nestedlist;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.wolfie.eskey.R;

import java.util.List;

/**
 * Created by david on 31/08/16.
 */

public class Adapter extends BaseExpandableListAdapter {

    private Context mContext;
    private DataManager mDataManager;

    private int mDepth;
    private String mFullName;
    private List<DataManager.Info> mGroupInfo;

    /**
     * @param name - should not contain any separator "." characters
     * @param parentAdapter - may be null
     */
    public Adapter(Context context, DataManager dataManager, String name, Adapter parentAdapter) {
        mContext = context;
        mDataManager = dataManager;

        mDepth = (parentAdapter == null) ? 0 : (parentAdapter.getDepth() + 1);
        mFullName = (parentAdapter == null ? "" : parentAdapter.getFullName()) + "." + name;
        mGroupInfo = mDataManager.getChildren(mDepth, mFullName);
    }

    private int getDepth() {
        return mDepth;
    }

    private String getFullName() {
        return mFullName;
    }

    /**
     * A leaf node has one group, and when expanded to show the "children" it will
     * show the details of that leaf node.
     */
    @Override
    public int getGroupCount() {
        return mGroupInfo.size();
    }

    /**
     * Gets a View that displays the given group.
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parentView) {
        DataManager.Info groupInfo = (DataManager.Info)getGroup(groupPosition);
        convertView = convertView(convertView, null, R.layout.nested_list_group);
        TextView textView = (TextView)convertView.findViewById(R.id.group_name);
        textView.setText((isExpanded ? "G " : "g ") + getFullName() + ":" + groupInfo.mName);
        textView.setTypeface(null, Typeface.BOLD);
        return convertView;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroupInfo.get(groupPosition);
    }

    /**
     * Gets the number of children in the group at the specified position.
     * This is always one; a leaf node will be expanded into a leaf layout
     * and a non leaf node will be expanded into a new ExpandableListView
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    /**
     * Gets a View that displays the given child within the given group.
     * This will be a leaf layout or a new ExpandableListView
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parentView) {
        DataManager.Info childInfo = (DataManager.Info)getChild(groupPosition, childPosition);
        // Does this have real children ?
//        childInfo.mIsLeaf = true;
        if (childInfo.mIsLeaf) {
            convertView = convertView(convertView, null, R.layout.nested_list_leaf);
            TextView textView = (TextView)convertView.findViewById(R.id.text_name);
            textView.setText(" " + getFullName() + ":" + childInfo.mName);
        } else {
            // This 'child' has real children; show them with a new ExpandableListView
            convertView = convertView(convertView, null, R.layout.nested_list_expandable);
            ExpandableListView expandableView = (ExpandableListView)convertView.findViewById(R.id.expandable_view);
            Adapter adapter = new Adapter(mContext, mDataManager, childInfo.mName, this);
            expandableView.setAdapter(adapter);
        }
        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // Should only ever be called for childPosition 0, since there is only one child.
        // The actual return value is not important.
        if (childPosition != 0) {
            System.out.println("*** childPosition not zero ==> " + childPosition);
        }
        return getGroup(groupPosition);
    }

    /**
     * Gets the group ID which must be unique across groups.
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    /**
     * Gets the child ID which must be unique within the group.
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    @Override
    public boolean hasStableIds() {
        return false;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private View convertView(View convertView, ViewGroup parentView, int layoutId) {
        if (convertView == null) {
            LayoutInflater inflater
                    = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutId, parentView);
        }
        return convertView;
    }

}
