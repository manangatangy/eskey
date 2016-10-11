package com.wolfie.eskey.view.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.DrawerLayout;

import com.wolfie.eskey.R;
import com.wolfie.eskey.presenter.MainPresenter;
import com.wolfie.eskey.view.fragment.EditFragment;
import com.wolfie.eskey.view.fragment.ListFragment;
import com.wolfie.eskey.view.fragment.DrawerFragment;
import com.wolfie.eskey.view.fragment.LoginFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class DrawerActivity extends SimpleActivity {

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
        setBackgroundImage(R.drawable.st_basils_cathedral_1);

        mMainPresenter = new MainPresenter(null, getApplicationContext());

        // Create the main content fragment into it's container.
        setupFragment(ListFragment.class.getName(), R.id.fragment_container_activity_simple, null);

        // Create the drawer fragment into it's container.
        setupFragment(DrawerFragment.class.getName(), R.id.fragment_container_activity_drawer, null);

        // Create the entry edit (activity sheet) fragment into it's container.
        setupFragment(EditFragment.class.getName(), R.id.fragment_container_edit, null);

        // Create the login (activity sheet) fragment into it's container.
        setupFragment(LoginFragment.class.getName(), R.id.fragment_container_login, null);

    }

    @Override
    public void onUserInteraction() {
        mMainPresenter.onUserInteraction();
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

//    @Override
//    public void onResume() {
//        super.onResume();
//        resetDisconnectTimer();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        stopDisconnectTimer();
//    }
}
