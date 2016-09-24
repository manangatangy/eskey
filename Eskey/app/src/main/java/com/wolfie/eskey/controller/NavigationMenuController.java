package com.wolfie.eskey.controller;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.wolfie.eskey.R;
import com.wolfie.eskey.adapter.GroupingRecyclerAdapter;
import com.wolfie.eskey.model.Entry;
import com.wolfie.eskey.util.DefaultLayoutManager;
import com.wolfie.eskey.controller.NavMenuRecyclerAdapter.MenuItemViewHolder;
import com.wolfie.eskey.controller.NavMenuRecyclerAdapter.OnNavMenuItemClickListener;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by david on 13/09/16.
 */

public class NavigationMenuController implements OnNavMenuItemClickListener {

    @Bind(R.id.navigation_recycler_view)
    RecyclerView mNavRecyclerList;

    private NavMenuRecyclerAdapter mNavMenuRecyclerAdapter;

    public void setOnNavItemSelectedListener(OnNavItemSelectedListener mOnNavItemSelectedListener) {
        this.mOnNavItemSelectedListener = mOnNavItemSelectedListener;
    }

    private OnNavItemSelectedListener mOnNavItemSelectedListener;

    public NavigationMenuController(Context context, NavigationView navigationView) {

        ButterKnife.bind(this, navigationView);

        mNavRecyclerList.setLayoutManager(new DefaultLayoutManager(context));
        mNavMenuRecyclerAdapter = new NavMenuRecyclerAdapter(context);
        mNavMenuRecyclerAdapter.setNavMenuItemClickListener(this);
        mNavRecyclerList.setLayoutManager(new LinearLayoutManager(context));
        mNavRecyclerList.setAdapter(mNavMenuRecyclerAdapter);
    }

    public void setItemsTexts(List<String> itemTexts) {
        mNavMenuRecyclerAdapter.setMenuItems(itemTexts);
        // The above call leaves all menu items unselected, so "select" the first item.
        mNavRecyclerList.post(new Runnable() {
            @Override
            public void run() {
                MenuItemViewHolder viewHolder
                        = (MenuItemViewHolder)mNavRecyclerList.findViewHolderForAdapterPosition(0);
                viewHolder.setSelected();       // WIll cause call to onNavMenuItemClick
            }
        });
    }

    @Override
    public void onNavMenuItemClick(String groupName, boolean hasChanged) {
        if (mOnNavItemSelectedListener != null) {
            mOnNavItemSelectedListener.onNavItemSelected(groupName, hasChanged);
        }
    }

    public interface OnNavItemSelectedListener {
        /**
         * @param itemText - will be null to indicate ALL_GROUPS_NAV_HEADING
         *                         String groupName
         * @param changed - means not already selected
         */
        void onNavItemSelected(String itemText, boolean changed);
    }

}
