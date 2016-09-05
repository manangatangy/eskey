package com.wolfie.eskey.expandingadaper;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.wolfie.eskey.R;
import com.wolfie.eskey.expandingadaper.DataSet;

/**
 * Created by david on 30/08/16.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private DataSet mDataSet;

    public ExpandableListAdapter(Context context, DataSet dataSet) {
        this.mContext = context;
        this.mDataSet = dataSet;

    }

    @Override
    public Object getGroup(int groupPosition) {
        String text = mDataSet.getGroupNames().get(groupPosition);
        System.out.println("getGroup " + groupPosition + " ==> " + text);
        return text;
    }

    @Override
    public int getGroupCount() {
        return mDataSet.getGroupNames().size();
    }

    /**
     * Gets the group ID which must be unique across groups.
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String groupName = (String)getGroup(groupPosition);
        String text = mDataSet.getChildNamesMap().get(groupName).get(childPosition);
        System.out.println("getChild " + groupPosition + ", " + childPosition + " ==> " + text);
        return text;
    }

    /**
     * Gets the number of children in the specified group.
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        String groupName = (String)getGroup(groupPosition);
        return mDataSet.getChildNamesMap().get(groupName).size();
    }

    /**
     * Gets the child ID which must be unique within the group.
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * Gets a View that displays the given child within the given group.
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        String childName = (String)getChild(groupPosition, childPosition);
        return getView(childName, R.layout.list_child, convertView, parent, false);
    }

    /**
     * Gets a View that displays the given group.
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String groupName = (String)getGroup(groupPosition);
        return getView(groupName, R.layout.list_group, convertView, parent, true);
    }

    private View getView(String text, int layoutResource,
                         View convertView, ViewGroup parent,
                         boolean isGroup) {
        if (convertView == null) {
            LayoutInflater inflater
                    = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResource, null);
        }
        TextView textView = (TextView)convertView.findViewById(R.id.text_name);
        textView.setText(text);
        if (isGroup) {
            textView.setTypeface(null, Typeface.BOLD);
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

