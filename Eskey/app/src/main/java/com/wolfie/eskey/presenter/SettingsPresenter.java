package com.wolfie.eskey.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.wolfie.eskey.R;
import com.wolfie.eskey.model.ImageEnum;
import com.wolfie.eskey.model.database.TimingOutSource;
import com.wolfie.eskey.model.loader.AsyncListeningTask;
import com.wolfie.eskey.model.loader.RemasterLoader;
import com.wolfie.eskey.util.TimeoutMonitor;
import com.wolfie.eskey.view.ActionSheetUi;
import com.wolfie.eskey.presenter.SettingsPresenter.SettingsUi;
import com.wolfie.eskey.view.activity.SimpleActivity;
import com.wolfie.eskey.view.fragment.LoginFragment;

/**
 * Created by david on 23/10/16.
 */

public class SettingsPresenter extends BasePresenter<SettingsUi>
        implements AsyncListeningTask.Listener<String> {

    private final static String KEY_SETTINGS_ACTION_SHEET_SHOWING = "KEY_SETTINGS_ACTION_SHEET_SHOWING";
    public final static String PREF_SESSION_TIMEOUT = "PREF_SESSION_TIMEOUT";
    public final static String PREF_SESSION_BACKGROUND_IMAGE = "PREF_SESSION_BACKGROUND_IMAGE";

    private boolean mIsShowing;

    private SharedPreferences mPrefs;

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
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
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
        // Load the current settings into the view elements.
        int sessionTimeout = mPrefs.getInt(PREF_SESSION_TIMEOUT, TimeoutMonitor.DEFAULT_TIMEOUT);
        getUi().setTimeout(sessionTimeout);

        int enumIndex = mPrefs.getInt(PREF_SESSION_BACKGROUND_IMAGE, SimpleActivity.DEFAULT_BACKGROUND_IMAGE);
        getUi().setImageItem(enumIndex);

        getUi().show();
    }

    public void onClickClose() {
        getUi().dismissKeyboard(false);
        getUi().hide();
    }

    @Override
    public boolean backPressed() {
        if (!getUi().isShowing() || getUi().isKeyboardVisible()) {
            return true;        // Means: not consumed here
        }
        onClickClose();         // Checks if all the settings items are good to close.
        return false;
    }

    public void onImageSelected(int enumIndex) {
        // Change the actual background image
        getUi().setActivityBackgroundImage(enumIndex);

        // Save in prefs
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(PREF_SESSION_BACKGROUND_IMAGE, enumIndex);
        editor.apply();
    }

    public void onTimeoutChanged(int timeoutInMillis) {
        // Change the timing source
        MainPresenter mainPresenter = getUi().findPresenter(null);
        mainPresenter.setTimeout(timeoutInMillis, true);        // Also restart timer

        // Save in prefs
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(PREF_SESSION_TIMEOUT, timeoutInMillis);
        editor.apply();
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

    public void hide() {
        getUi().hide();
    }

    public interface SettingsUi extends ActionSheetUi {

        void setTimeout(int timeoutInMillis);

        // Must clear the field so that hide isn't inhibited.
        void clearPasswordsAndHidePasswordsSetting();
        void setPasswordError(@StringRes int resId);
        void setImageItem(int enumIndex);
        // This method actually changes the image via the EskeyActivity
        void setActivityBackgroundImage(int enumIndex);
    }

}
