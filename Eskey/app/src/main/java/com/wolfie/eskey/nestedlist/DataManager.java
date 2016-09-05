package com.wolfie.eskey.nestedlist;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 31/08/16.
 */

public class DataManager {

    public class Info {
        public String mName;
        public boolean mIsLeaf;

        public Info(String name, boolean isLeaf) {
            mName = name;
            mIsLeaf = isLeaf;
        }
    }

    /**
     * @param parentName
     * @param depth - 0 means root
     * @return list of child names (not fullnames) or null if this node is a leaf.
     * Does never return an empty list.
     */
    public List<Info> getChildren(int depth, String parentName) {
        if (depth > 2) {
            return null;
        }
        boolean isLeaf = (depth == 2);
        List<Info> children = new ArrayList<>();
        children.add(new Info("one", isLeaf));
//        children.add(new Info("two", isLeaf));
//        children.add(new Info("three", isLeaf));
        return children;
    }
}
