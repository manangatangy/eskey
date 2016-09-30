package com.wolfie.eskey.presenter;

import android.support.annotation.Nullable;

import com.wolfie.eskey.model.DataSet;
import com.wolfie.eskey.model.EntryGroup;
import com.wolfie.eskey.model.loader.AsyncListeningTask;
import com.wolfie.eskey.presenter.ListPresenter.ListUi;
import com.wolfie.eskey.view.BaseUi;
import com.wolfie.eskey.view.fragment.DrawerFragment;

import java.util.List;

public class ListPresenter extends BasePresenter<ListUi> implements
        AsyncListeningTask.Listener<DataSet> {

    // If non-null, then only show entries from this group.
    @Nullable
    private String mGroupName;

    // This value is not saved, but refreshed upon resume.
    private DataSet mDataSet;

    public ListPresenter(ListUi listUi) {
        super(listUi);
    }

    @Override
    public void resume() {
        super.resume();
        loadEntries();
    }

    @Override
    public void pause() {
        super.pause();
    }

    public void loadEntries() {
        MainPresenter mainPresenter = getUi().findPresenter(null);
        // TODO check for loader availability (maybe locked out)
        mainPresenter.getEntryLoader().read(this);
    }

    /**
     * Set the DataSet.  Use the existing group name and display the (possibly
     * filtered) list on the ui. Then build a new list of group headings
     * which are passed to the DrawerPresenter.
     */
    @Override
    public void onCompletion(DataSet dataSet) {
        mDataSet = dataSet;
        setGroupName(mGroupName);
        DrawerPresenter drawerPresenter = getUi().findPresenter(DrawerFragment.class);
        if (drawerPresenter != null) {
            List<String> headings = EntryGroup.buildHeadingsList(dataSet);
            drawerPresenter.setHeadings(headings, mGroupName);
        }
    }

    /**
     * Set the new group name, use it with the existing (already loaded) DataSet
     * to build a list of structured entries, and pass to the ui for display.
     */
    public void setGroupName(@Nullable String groupName) {
        mGroupName = groupName;
        if (mDataSet != null) {
            List<EntryGroup> groups = EntryGroup.buildGroups(mGroupName, mDataSet);
            getUi().refreshListWithDataSet(groups);
        }
    }

    @Nullable
    public String getGroupName() {
        return mGroupName;
    }

    public interface ListUi extends BaseUi {

        void refreshListWithDataSet(List<EntryGroup> groups);

    }

}
