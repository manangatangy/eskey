package com.wolfie.eskey.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.wolfie.eskey.model.Entry;
import com.wolfie.eskey.model.loader.AsyncListeningTask;
import com.wolfie.eskey.view.BaseUi;
import com.wolfie.eskey.presenter.EditPresenter.EditUi;
import com.wolfie.eskey.view.fragment.ListFragment;

/**
 * Created by david on 30/09/16.
 */

public class EditPresenter extends BasePresenter<EditUi> implements
        AsyncListeningTask.Listener<Boolean> {

    private final static String KEY_EDIT_ACTION_SHEET_SHOWING = "KEY_EDIT_ACTION_SHEET_SHOWING";
    private final static String KEY_EDIT_ACTION_SHEET_ENTRY = "KEY_EDIT_ACTION_SHEET_ENTRY";

    private Entry mEntry;
    private boolean mIsShowing;

    public EditPresenter(EditUi editUi) {
        super(editUi);
    }

    @Override
    public void resume() {
        super.resume();
        MainPresenter mainPresenter = getUi().findPresenter(null);
        if (!mIsShowing || mainPresenter == null || mainPresenter.getTimeoutMonitor().isTimedOut()) {
            getUi().hide();
        } else {
            getUi().show();
        }
    }

    @Override
    public void pause() {
        super.pause();
        mIsShowing = getUi().isShowing();
    }

    @Override
    public void onSaveState(Bundle outState) {
        outState.putBoolean(KEY_EDIT_ACTION_SHEET_SHOWING, mIsShowing);
        // TODO save/restore Entry
    }

    @Override
    public void onRestoreState(@Nullable Bundle savedState) {
        mIsShowing = savedState.getBoolean(KEY_EDIT_ACTION_SHEET_SHOWING, false);
    }

    /**
     * A null entry is allowed, it means create a new entry for editing.
     */
    public void editEntry(@Nullable Entry entry) {
        mEntry = (entry != null) ? entry : Entry.create("", "", "");
        getUi().show();
    }

    public void editNewEntry(String groupName) {
        editEntry(Entry.create(groupName, "", ""));
    }

    public void onShow() {
        getUi().enableDeleteButton(!mEntry.isNew());
        getUi().setTitleText(mEntry.isNew() ? "Create Entry" : "Modify Entry");
        getUi().setTextValues(mEntry);
    }

    public void onClickSave() {
        mEntry = getUi().getTextValues(mEntry);
        getUi().dismissKeyboardAndClose();

        MainPresenter mainPresenter = getUi().findPresenter(null);
        // TODO check for loader availability (maybe locked out)
        if (mEntry.isNew()) {
            mainPresenter.getEntryLoader().insert(mEntry, this);
        } else {
            mainPresenter.getEntryLoader().update(mEntry, this);
        }
        // TODO after edit, contract the view box
    }

    public void hide() {
        getUi().hide();
    }

    public void onClickDelete() {
        mEntry = getUi().getTextValues(mEntry);
        getUi().dismissKeyboardAndClose();

        MainPresenter mainPresenter = getUi().findPresenter(null);
        // TODO check for loader availability (maybe locked out)
        mainPresenter.getEntryLoader().delete(mEntry, this);
    }

    public void onClickClose() {
        // TODO - check for changes and alert before closing, losing changes
        getUi().dismissKeyboardAndClose();
    }

    /**
     * callback from getEntryLoader().insert/update/delete
     */
    @Override
    public void onCompletion(Boolean aBoolean) {
        ListPresenter listPresenter = getUi().findPresenter(ListFragment.class);
        if (listPresenter != null) {
            listPresenter.loadEntries();
        }
    }

    public interface EditUi extends BaseUi {

        void setTitleText(String title);
        void enableDeleteButton(boolean enable);
        void setTextValues(Entry entry);
        Entry getTextValues(Entry entry);
        void dismissKeyboardAndClose();
        void show();
        void hide();
        boolean isShowing();
    }
}
