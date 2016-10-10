package com.wolfie.eskey.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.wolfie.eskey.model.DataSet;
import com.wolfie.eskey.model.Entry;
import com.wolfie.eskey.model.EntryGroup;
import com.wolfie.eskey.model.MasterData;
import com.wolfie.eskey.model.loader.AsyncListeningTask;
import com.wolfie.eskey.presenter.ListPresenter.ListUi;
import com.wolfie.eskey.util.crypto.SpongyCrypter;
import com.wolfie.eskey.view.BaseUi;
import com.wolfie.eskey.view.fragment.EditFragment;
import com.wolfie.eskey.view.fragment.LoginFragment;

import java.util.List;

import static com.wolfie.eskey.util.crypto.SpongyCrypter.MEDIUM_SECRET_KEY_FACTORY_ALGORITHM;

public class ListPresenter extends BasePresenter<ListUi> implements
        AsyncListeningTask.Listener<DataSet> {

    private final static String KEY_LIST_GROUPNAME = "KEY_LIST_GROUPNAME";
    private final static String KEY_LIST_MASTER_SALT = "KEY_LIST_MASTER_SALT";
    private final static String KEY_LIST_MASTER_KEY = "KEY_LIST_MASTER_KEY";

    // If non-null, then only show entries from this group.
    @Nullable
    private String mGroupName;
    private MasterData mMasterData;

    // These values are not saved, but refreshed upon resume.
    // Note that mHeadings and mGroupName are taken from here by
    // DrawerPresenter.resume() to reload the nav menu
    private DataSet mDataSet;
    private List<String> mHeadings;

    public ListPresenter(ListUi listUi) {
        super(listUi);
    }

    @Override
    public void resume() {
        super.resume();
        // The EntryLoader.Crypter state may have been lost while paused, so
        // re-construct it with the salt/masterKey that are held here
        makeCrypter();
        loadEntries();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void onSaveState(Bundle outState) {
        outState.putString(KEY_LIST_GROUPNAME, mGroupName);
        String salt = (mMasterData == null) ? null : mMasterData.getSalt();
        String key = (mMasterData == null) ? null : mMasterData.getMasterKey();
        outState.putString(KEY_LIST_MASTER_SALT, salt);
        outState.putString(KEY_LIST_MASTER_KEY, key);
    }

    @Override
    public void onRestoreState(@Nullable Bundle savedState) {
        mGroupName = savedState.getString(KEY_LIST_GROUPNAME);
        String salt = savedState.getString(KEY_LIST_MASTER_SALT);
        String key = savedState.getString(KEY_LIST_MASTER_KEY);
        mMasterData = new MasterData(salt, key);
    }

    public void setMasterDataAndMakeCrypter(MasterData masterData) {
        mMasterData = masterData;
        makeCrypter();
    }

    private void makeCrypter() {
        SpongyCrypter entryCrypter = new SpongyCrypter(mMasterData.getSalt(), MEDIUM_SECRET_KEY_FACTORY_ALGORITHM);
        entryCrypter.setPassword(mMasterData.getMasterKey());
        MainPresenter mainPresenter = getUi().findPresenter(null);
        mainPresenter.getEntryLoader().setCrypter(entryCrypter);
    }

    /**
     * This will clear the list view if TimeoutMonitor.isTimeout() or if
     * not loginPresenter.isLoggedIn().  The former test is needed to handle when
     * resume()ing from a pause.  The latter test is needed when first starting
     * up, and no timeout has yet occurred.
     */
    public void loadEntries() {
        boolean clearTheList = true;
        MainPresenter mainPresenter = getUi().findPresenter(null);
        if (mainPresenter != null && !mainPresenter.getTimeoutMonitor().isTimedOut()) {
            // Disallow entry reading until logged in.
            LoginPresenter loginPresenter = getUi().findPresenter(LoginFragment.class);
            if (loginPresenter.isLoggedIn()) {
                mainPresenter.getEntryLoader().read(this);
                clearTheList = false;
            }
        }
        if (clearTheList) {
            getUi().refreshListWithDataSet(null);
        }

    }

    /**
     * Set the DataSet.  Use the existing group name and display the (possibly
     * filtered) list on the ui. Then build a new list of group headings
     * which are passed to the DrawerPresenter.
     */
    @Override
    public void onCompletion(DataSet dataSet) {
        mDataSet = dataSet;
        mHeadings = EntryGroup.buildHeadingsList(dataSet);
        setGroupName(mGroupName);
//        DrawerPresenter drawerPresenter = getUi().findPresenter(DrawerFragment.class);
//        if (drawerPresenter != null) {
//            drawerPresenter.setHeadings(headings, mGroupName);
//        }
    }

    /**
     * Set the new group name for filtering, use it with the existing (already loaded) DataSet
     * to build a list of structured entries, and pass to the ui for display.
     */
    public void setGroupName(@Nullable String groupName) {
        mGroupName = groupName;
        if (mDataSet != null) {
            List<EntryGroup> groups = EntryGroup.buildGroups(mGroupName, mDataSet);
            getUi().refreshListWithDataSet(groups);
        }
    }

    public void onListItemClick(Entry selectedEntry) {
        EditPresenter editPresenter = getUi().findPresenter(EditFragment.class);
        if (editPresenter != null) {
            editPresenter.editEntry(selectedEntry);
        }
    }

    @Nullable
    public String getGroupName() {
        return mGroupName;
    }

    public List<String> getHeadings() {
        return mHeadings;
    }

    public interface ListUi extends BaseUi {

        void refreshListWithDataSet(List<EntryGroup> groups);

    }

}
