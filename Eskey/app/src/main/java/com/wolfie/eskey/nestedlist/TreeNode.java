package com.wolfie.eskey.nestedlist;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 31/08/16.
 */

public class TreeNode {

    private TreeNode mParent;               // null means we are root.
    private List<TreeNode> mChildren;       // null means we are leaf.
    private String mName;                   // Relative name for node.

    public TreeNode(String name) {
        mName = name;
    }

    public void addChild(TreeNode child) {
        if (mChildren == null) {
            mChildren = new ArrayList<>();
        }
        mChildren.add(child);
    }
}
