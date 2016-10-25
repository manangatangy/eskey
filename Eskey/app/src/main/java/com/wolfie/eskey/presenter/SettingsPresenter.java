package com.wolfie.eskey.presenter;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.wolfie.eskey.R;
import com.wolfie.eskey.model.loader.AsyncListeningTask;
import com.wolfie.eskey.model.loader.RemasterLoader;
import com.wolfie.eskey.view.ActionSheetUi;
import com.wolfie.eskey.presenter.SettingsPresenter.SettingsUi;
import com.wolfie.eskey.view.fragment.LoginFragment;

/**
 * Created by david on 23/10/16.
 */

public class SettingsPresenter extends BasePresenter<SettingsUi>
        implements AsyncListeningTask.Listener<String> {


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
        getUi().dismissKeyboard(false);
        if (getUi().onHideAll()) {
            getUi().hide();
        }
    }

    @Override
    public boolean backPressed() {
        if (!getUi().isShowing() || getUi().isKeyboardVisible()) {
            return true;        // Means: not consumed here
        }
        onClickClose();         // Checks if all the settings items are good to close.
        return false;
    }

    public void onTimeoutChanged(int timeoutInMillis) {

    }

    public void onChangePassword(String password, String confirm) {
        if (password == null || !password.equals(confirm)) {
            getUi().setPasswordError(R.string.st007);
        } else if (password.trim().length() != password.length()) {
            getUi().setPasswordError(R.string.st011);
        } else {
            getUi().dismissKeyboard(false);

            MainPresenter mainPresenter = getUi().findPresenter(null);
            LoginPresenter loginPresenter = getUi().findPresenter(LoginFragment.class);
            RemasterLoader remasterLoader = mainPresenter.makeRemasterLoader(loginPresenter.getMediumCrypter());
            remasterLoader.remaster(password, this);
        }
    }

    /**
     * Called from RemasterLoader
     */
    @Override
    public void onCompletion(String successMessage) {
        getUi().clearPasswordsAndHidePasswordsSetting();
        getUi().showBanner(successMessage);
        // Logout
        LoginPresenter loginPresenter = getUi().findPresenter(LoginFragment.class);
        loginPresenter.clearAndLogout();
    }

    public void onBackgroundPicChanged(@DrawableRes int drawId) {

    }

    public void hide() {
        getUi().hide();
    }

    public interface SettingsUi extends ActionSheetUi {

        // Try to close all the settings, return true if all closed ok
        boolean onHideAll();
        // Must clear the field so that hide isn't inhibited.
        void clearPasswordsAndHidePasswordsSetting();

        void setPasswordError(@StringRes int resId);

    }

}
