package com.wolfie.eskey.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wolfie.eskey.R;
import com.wolfie.eskey.view.adapter.NavMenuRecyclerAdapter;
import com.wolfie.eskey.view.adapter.NavMenuRecyclerAdapter.MenuItemViewHolder;
import com.wolfie.eskey.presenter.DrawerPresenter;
import com.wolfie.eskey.presenter.DrawerPresenter.DrawerUi;
import com.wolfie.eskey.util.DefaultLayoutManager;
import com.wolfie.eskey.view.activity.DrawerActivity;

import java.util.List;

import butterknife.BindView;

public class DrawerFragment extends BaseFragment implements
        DrawerUi,
        DrawerLayout.DrawerListener,
        NavMenuRecyclerAdapter.OnNavMenuItemClickListener {

    @BindView(R.id.navigation_text_view)
    TextView mTextView;

    @BindView(R.id.navigation_recycler_view)
    RecyclerView mRecyclerView;

    private DrawerPresenter mDrawerPresenter;
    private ActionBarDrawerToggle mToggle;

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
        mToggle = new ActionBarDrawerToggle(
                getDrawerActivity(),
                getDrawerActivity().mDrawer,
                getDrawerActivity().mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mToggle.syncState();
        getDrawerActivity().mDrawer.setDrawerListener(this);
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
        if (isDrawerOpen()) {
            getDrawerActivity().mDrawer.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void openDrawer() {
        if (!isDrawerOpen()) {
            getDrawerActivity().mDrawer.openDrawer(Gravity.LEFT);
        }
    }

    private DrawerActivity getDrawerActivity() {
        return (DrawerActivity)mBaseActivity;
    }

    // DrawerLayout.DrawerListener methods
    // We override these because we need to support presenter.onDrawerOpened
    @Override
    public void onDrawerOpened(View drawerView) {
        mToggle.onDrawerOpened(drawerView);
        mDrawerPresenter.onDrawerOpened();
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        mToggle.onDrawerSlide(drawerView, slideOffset);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        mToggle.onDrawerClosed(drawerView);
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        mToggle.onDrawerStateChanged(newState);
    }
}
