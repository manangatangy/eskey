package com.wolfie.eskey.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.wolfie.eskey.view.BaseUi;
import com.wolfie.eskey.presenter.DrawerPresenter.DrawerUi;
import com.wolfie.eskey.view.fragment.ListFragment;

import java.util.List;

public class DrawerPresenter extends BasePresenter<DrawerUi> {

    private final static String KEY_DRAWER_SHOWING = "KEY_DRAWER_SHOWING";
    private boolean mIsOpen;

    public DrawerPresenter(DrawerUi drawerUi) {
        super(drawerUi);
    }

    @Override
    public void resume() {
        super.resume();
        MainPresenter mainPresenter = getUi().findPresenter(null);
        if (!mIsOpen || mainPresenter == null || mainPresenter.getTimeoutMonitor().isTimedOut()) {
            getUi().closeDrawer();
        } else {
            getUi().openDrawer();
        }
    }

    @Override
    public void pause() {
        super.pause();
        mIsOpen = getUi().isDrawerOpen();
    }

    @Override
    public void onSaveState(Bundle outState) {
        outState.putBoolean(KEY_DRAWER_SHOWING, mIsOpen);
    }

    @Override
    public void onRestoreState(@Nullable Bundle savedState) {
        mIsOpen = savedState.getBoolean(KEY_DRAWER_SHOWING, false);
    }

//    public void setHeadings(List<String> headings, @Nullable String selected) {
//        getUi().refreshListWithHeadings(headings);
//        getUi().selectListItem(selected);
//    }

    public void onDrawerOpened() {
        // Fetch the headings and selected from ListPresenter and set to the view
        ListPresenter listPresenter = getUi().findPresenter(ListFragment.class);
        if (listPresenter != null) {
            List<String> headings = listPresenter.getHeadings();
            String selected = listPresenter.getGroupName();
            getUi().refreshListWithHeadings(headings);
            getUi().selectListItem(selected);
        }
    }

    public void closeDrawer() {
        getUi().closeDrawer();
    }

    public void onItemSelected(String groupName, boolean hasChanged) {
        getUi().closeDrawer();
        if (hasChanged) {
            // Inform ListPresenter of a new filtered group value
            ListPresenter listPresenter = getUi().findPresenter(ListFragment.class);
            if (listPresenter != null) {
                listPresenter.setGroupName(groupName);
            }
        }
    }

    @Override
    public boolean backPressed() {
        if (!getUi().isDrawerOpen()) {
            return true;        // Means: not consumed here
        }
        getUi().closeDrawer();
        return false;
    }

    public interface DrawerUi extends BaseUi {
        boolean isDrawerOpen();
        void closeDrawer();
        void refreshListWithHeadings(List<String> headings);
        void selectListItem(@Nullable String selected);
        void openDrawer();
    }

}
