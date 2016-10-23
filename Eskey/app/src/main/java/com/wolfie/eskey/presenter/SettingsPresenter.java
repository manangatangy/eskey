package com.wolfie.eskey.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.wolfie.eskey.view.ActionSheetUi;
import com.wolfie.eskey.presenter.SettingsPresenter.SettingsUi;

/**
 * Created by david on 23/10/16.
 */

public class SettingsPresenter extends BasePresenter<SettingsUi> {


    private final static String KEY_SETTINGS_ACTION_SHEET_SHOWING = "KEY_SETTINGS_ACTION_SHEET_SHOWING";

    private boolean mIsShowing;

    public SettingsPresenter(SettingsUi settingsUi) {
        super(settingsUi);
    }

    @Override
    public void resume() {
        super.resume();
        MainPresenter mainPresenter = getUi().findPresenter(null);
        if (!mIsShowing || mainPresenter == null || mainPresenter.getTimeoutMonitor().isTimedOut()) {
            getUi().hide();
        }
    }

    @Override
    public void pause() {
        super.pause();
        mIsShowing = getUi().isShowing();
        getUi().dismissKeyboard(false);
    }

    @Override
    public void onSaveState(Bundle outState) {
        outState.putBoolean(KEY_SETTINGS_ACTION_SHEET_SHOWING, mIsShowing);
    }

    @Override
    public void onRestoreState(@Nullable Bundle savedState) {
        mIsShowing = savedState.getBoolean(KEY_SETTINGS_ACTION_SHEET_SHOWING, false);
    }

    public void show() {
        getUi().show();
    }

    public void onClickClose() {
        getUi().dismissKeyboard(true);
        getUi().hide();
    }

    @Override
    public boolean backPressed() {
        if (!getUi().isShowing() || getUi().isKeyboardVisible()) {
            return true;        // Means: not consumed here
        }
        getUi().hide();
        return false;
    }


    public interface SettingsUi extends ActionSheetUi {


    }

}
