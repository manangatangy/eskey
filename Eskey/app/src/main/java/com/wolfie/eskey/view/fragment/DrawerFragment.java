package com.wolfie.eskey.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wolfie.eskey.R;
import com.wolfie.eskey.presenter.DrawerPresenter;
import com.wolfie.eskey.presenter.DrawerPresenter.DrawerUi;
import com.wolfie.eskey.presenter.Presenter;
import com.wolfie.eskey.view.activity.DrawerActivity;

import butterknife.BindView;

public class DrawerFragment extends BaseFragment implements DrawerUi {

    @BindView(R.id.navigation_text_view)
    TextView mTextView;

    @BindView(R.id.navigation_recycler_view)
    RecyclerView mRecyclerView;

    private DrawerPresenter mPresenter;

    @Nullable
    @Override
    public Presenter getPresenter() {
        return mPresenter;
    }

    public DrawerFragment() {
        mPresenter = new DrawerPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer, container, false);

        getDrawerActivity().setSupportActionBar(getDrawerActivity().mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getDrawerActivity(),
                getDrawerActivity().mDrawer,
                getDrawerActivity().mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        getDrawerActivity().mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextView.setText("Hello Preston");
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
        return (DrawerActivity)getActivity();
    }

}
