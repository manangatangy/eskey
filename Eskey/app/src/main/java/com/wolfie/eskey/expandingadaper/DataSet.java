package com.wolfie.eskey.expandingadaper;

import java.util.HashMap;
import java.util.List;

/**
 * Created by david on 30/08/16.
 */

public class DataSet {

    // list of names of groups
    private List<String> mGroupNames;

    // map (indexed by group name) of lists of names of childs
    private HashMap<String, List<String>> mChildNamesMap;

    public DataSet(List<String> mGroupNames, HashMap<String, List<String>> mChildNamesMap) {
        this.mGroupNames = mGroupNames;
        this.mChildNamesMap = mChildNamesMap;
    }

    public List<String> getGroupNames() {
        return mGroupNames;
    }

    public void setGroupNames(List<String> groupNames) {
        mGroupNames = groupNames;
    }

    public HashMap<String, List<String>> getChildNamesMap() {
        return mChildNamesMap;
    }

    public void setChildNamesMap(HashMap<String, List<String>> childNamesMap) {
        mChildNamesMap = childNamesMap;
    }
}
