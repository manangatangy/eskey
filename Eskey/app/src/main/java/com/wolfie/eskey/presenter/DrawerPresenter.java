package com.wolfie.eskey.presenter;

import android.support.annotation.Nullable;

import com.wolfie.eskey.view.BaseUi;
import com.wolfie.eskey.presenter.DrawerPresenter.DrawerUi;
import com.wolfie.eskey.view.fragment.DrawerFragment;
import com.wolfie.eskey.view.fragment.ListFragment;

import java.util.List;

public class DrawerPresenter extends BasePresenter<DrawerUi> {

    public DrawerPresenter(DrawerUi drawerUi) {
        super(drawerUi);
    }

    public void setHeadings(List<String> headings, @Nullable String selected) {
        getUi().refreshListWithHeadings(headings);
        getUi().selectListItem(selected);
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
    }

}
