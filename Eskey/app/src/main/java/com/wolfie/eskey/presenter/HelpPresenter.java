package com.wolfie.eskey.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.wolfie.eskey.presenter.HelpPresenter.HelpUi;
import com.wolfie.eskey.view.ActionSheetUi;

public class HelpPresenter extends BasePresenter<HelpUi> {

    private final static String KEY_HELP_ACTION_SHEET_SHOWING = "KEY_HELP_ACTION_SHEET_SHOWING";

    private boolean mIsShowing;

    public HelpPresenter(HelpUi helpUi) {
        super(helpUi);
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
        outState.putBoolean(KEY_HELP_ACTION_SHEET_SHOWING, mIsShowing);
    }

    @Override
    public void onRestoreState(@Nullable Bundle savedState) {
        mIsShowing = savedState.getBoolean(KEY_HELP_ACTION_SHEET_SHOWING, false);
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

    public void hide() {
        getUi().hide();
    }

    public interface HelpUi extends ActionSheetUi {


    }
}
