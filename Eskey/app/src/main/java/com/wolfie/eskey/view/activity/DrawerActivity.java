package com.wolfie.eskey.view.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.DrawerLayout;

import com.wolfie.eskey.R;
import com.wolfie.eskey.presenter.BasePresenter;
import com.wolfie.eskey.presenter.ListPresenter;
import com.wolfie.eskey.presenter.MainPresenter;
import com.wolfie.eskey.presenter.Presenter;
import com.wolfie.eskey.view.fragment.BaseFragment;
import com.wolfie.eskey.view.fragment.ListFragment;
import com.wolfie.eskey.view.fragment.DrawerFragment;

import butterknife.BindView;

public class DrawerActivity extends SimpleActivity  {

    @BindView(R.id.layout_activity_drawer)
    public DrawerLayout mDrawer;

    private MainPresenter mMainPresenter;

    @Override
    public MainPresenter getPresenter() {
        return mMainPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainPresenter = new MainPresenter(null);
        mMainPresenter.init(getApplicationContext());

        // Create the drawer fragment into it's container.
        setupFragment(DrawerFragment.class.getName(), R.id.fragment_container_activity_drawer, null);

        // Create the main content fragment into it's container.
        setupFragment(ListFragment.class.getName(), R.id.fragment_container_activity_simple, null);

    }

    @Override
    @LayoutRes
    public int getLayoutResource() {
        // Specify the layout to use for the DrawerActivity.  This layout include
        // the activity_simple layout, which contains the toolbar and the
        // fragment_container_activity_simple container (for ListFragment) as
        // well as fragment_container_activity_drawer for the DrawerFragment
        return R.layout.activity_drawer;
    }

}
