package com.wolfie.eskey.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wolfie.eskey.R;
import com.wolfie.eskey.controller.NavMenuRecyclerAdapter;
import com.wolfie.eskey.controller.NavMenuRecyclerAdapter.MenuItemViewHolder;
import com.wolfie.eskey.model.EntryGroup;
import com.wolfie.eskey.presenter.DrawerPresenter;
import com.wolfie.eskey.presenter.DrawerPresenter.DrawerUi;
import com.wolfie.eskey.presenter.Presenter;
import com.wolfie.eskey.util.DefaultLayoutManager;
import com.wolfie.eskey.view.activity.DrawerActivity;
import com.wolfie.eskey.view.adapter.GroupingRecyclerAdapter;

import java.util.List;

import butterknife.BindView;

public class DrawerFragment extends BaseFragment implements
        DrawerUi,
        NavMenuRecyclerAdapter.OnNavMenuItemClickListener {

    @BindView(R.id.navigation_text_view)
    TextView mTextView;

    @BindView(R.id.navigation_recycler_view)
    RecyclerView mRecyclerView;

    private DrawerPresenter mDrawerPresenter;

    @Nullable
    @Override
    public DrawerPresenter getPresenter() {
        return mDrawerPresenter;
    }

    public DrawerFragment() {
        mDrawerPresenter = new DrawerPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextView.setText("Hello Preston");
        mRecyclerView.setLayoutManager(new DefaultLayoutManager(getContext()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDrawerActivity().setSupportActionBar(getDrawerActivity().mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getDrawerActivity(),
                getDrawerActivity().mDrawer,
                getDrawerActivity().mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        getDrawerActivity().mDrawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void refreshListWithHeadings(List<String> headings) {
        getAdapter().setMenuItems(headings);
    }

    @Override
    public void selectListItem(@Nullable String selected) {
        // selected == null means select the top item (ALL_GROUPS)
        final int adapterPosition = (selected == null) ? 0 : getAdapter().getAdapterPositionForItem(selected);
        if (adapterPosition >= 0) {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    MenuItemViewHolder viewHolder =
                            (MenuItemViewHolder)mRecyclerView.findViewHolderForAdapterPosition(adapterPosition);
                    viewHolder.setSelected(false);       // Won't cause call to onNavMenuItemClick
                }
            });
        }
    }

    private NavMenuRecyclerAdapter getAdapter() {
        NavMenuRecyclerAdapter adapter = (NavMenuRecyclerAdapter)mRecyclerView.getAdapter();
        if (adapter == null) {
            adapter = new NavMenuRecyclerAdapter(getContext());
            adapter.setNavMenuItemClickListener(this);
            mRecyclerView.setAdapter(adapter);
        }
        return adapter;
    }

    /**
     * @param groupName - will be null to indicate ALL_GROUPS_NAV_HEADING
     * @param hasChanged - means not already selected
     */
    @Override
    public void onNavMenuItemClick(String groupName, boolean hasChanged) {
        mDrawerPresenter.onItemSelected(groupName, hasChanged);
    }

    @Override
    public boolean isDrawerOpen() {
        return getDrawerActivity().mDrawer.isDrawerOpen(Gravity.LEFT);
    }

    @Override
    public void closeDrawer() {
        getDrawerActivity().mDrawer.closeDrawer(Gravity.LEFT);
    }

    private DrawerActivity getDrawerActivity() {
        return (DrawerActivity)mBaseActivity;
    }

}
