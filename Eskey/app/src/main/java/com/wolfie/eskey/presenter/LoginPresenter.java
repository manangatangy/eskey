package com.wolfie.eskey.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;

import com.wolfie.eskey.R;
import com.wolfie.eskey.model.MasterData;
import com.wolfie.eskey.model.loader.AsyncListeningTask;
import com.wolfie.eskey.view.BaseUi;

import com.wolfie.eskey.presenter.LoginPresenter.LoginUi;
import com.wolfie.eskey.view.fragment.DrawerFragment;
import com.wolfie.eskey.view.fragment.EditFragment;
import com.wolfie.eskey.view.fragment.ListFragment;
import com.wolfie.eskey.util.TimeoutMonitor.UserInactivityTimeoutListener;

/**
 * Created by david on 2/10/16.
 */

/**
 * This class is responsible for the login/logout/password/timeout processing.
 * The state transitions are as follows:
 * 1. UNKNOWN --> (calls load-master) --> either FIRST_TIME or EXISTING_USER
 * the view configures differently depending on the state,
 * 2. (login new) --> (calls save-master)
 * on save response, if OK then --> LOGGED_IN, (hide, refresh listFrag) else UNKNOWN, re-load-master
 * 3. (login existing) if password-is-good then --> LOGGED_IN, (hide, refresh listFrag) else UNKNOWN, re-load-master
 * 4. resume --> if LOGGED_IN and timed-out then --> UNKNOWN, re-load-master
 * 5. resume --> if UNKNOWN then start load-master
 * 5. resume --> else show/hide as state was on pause
 */
public class LoginPresenter extends BasePresenter<LoginUi> implements
        UserInactivityTimeoutListener {

    private final static String KEY_LOGIN_ACTION_SHEET_SHOWING = "KEY_LOGIN_ACTION_SHEET_SHOWING";

    private boolean mIsShowing;
    private MasterData mMasterData;
    private State mState = State.UNKNOWN;

    private enum State {
        UNKNOWN,            // No query has yet been made to the database for the MasterData
        FIRST_TIME,         // database returned null MasterData
        EXISTING_USER,      // database returned non-null MasterData
        LOGGED_IN           // non-null MasterData has been decrypted
    }

    public LoginPresenter(LoginUi loginUi) {
        super(loginUi);
    }

    @Override
    public void resume() {
        super.resume();
        Log.d("LoginPresenter", "resume, mState is " + mState);
        if (mState == State.UNKNOWN) {
            // This is the start of this activation/session.
            startLoginForExisting();
        } else {
            // We can only have timed out if LOGGED_IN since timer is not running otherwise.
            MainPresenter mainPresenter = getUi().findPresenter(null);
            if (mainPresenter.getTimeoutMonitor().isTimedOut()) {
                // We must have timed out while paused, re-initialise.
                // No need to hide other frags; their resume()s will do that.
                startLoginForExistingWithTimeoutMessage();
            } else {
                // Restore it's show/hide state which may be showing (FIRST_TIME, EXISTING_USER)
                // or hidden (LOGGED_IN), in which case restart the timer.
                if (mIsShowing) {
                    // Assert(mState == FIRST_TIME or EXISTING_USER)
                    getUi().show();
                    getUi().dismissKeyboard(false);
                } else {
                    // Assert(mState == LOGGED_IN)
                    getUi().hide();
                    registerTimeoutListenerAndStartTimer();
                }
            }
        }
    }

    @Override
    public void pause() {
        super.pause();
        mIsShowing = getUi().isShowing();
        Log.d("LoginPresenter", "pause, mIsShowing is " + mIsShowing);
        // Leave entry access enabled so that listPresenter can reload upon resume()
        unregisterTimeoutListenerAndStopTimer(true);
    }

    @Override
    public void onSaveState(Bundle outState) {
        outState.putBoolean(KEY_LOGIN_ACTION_SHEET_SHOWING, mIsShowing);
        // TODO save/restore MasterData and State
    }

    @Override
    public void onRestoreState(@Nullable Bundle savedState) {
        mIsShowing = savedState.getBoolean(KEY_LOGIN_ACTION_SHEET_SHOWING, false);
    }

    @Override
    public void onUserInactivityTimeout() {
        // This can only occur when the presenter is resumed, since pause disables
        // timer, therefore the other frags must be hidden/cleared explicitly.

        // Must stop timer so that MasterLoader is not blocked by TimingOutSource
        // This will also disallow access to the Entries for listPresenter.
        unregisterTimeoutListenerAndStopTimer(false);

        DrawerPresenter drawerPresenter = getUi().findPresenter(DrawerFragment.class);
        drawerPresenter.closeDrawer();
        EditPresenter editPresenter = getUi().findPresenter(EditFragment.class);
        editPresenter.hide();
        ListPresenter listPresenter = getUi().findPresenter(ListFragment.class);
        listPresenter.loadEntries();

        startLoginForExistingWithTimeoutMessage();
    }

    private void startLoginForExisting() {
        Log.d("LoginPresenter", "startLoginForExisting");
        startLoginSequence(R.string.st004, -1);
    }
    private void startLoginForExistingWithTimeoutMessage() {
        Log.d("LoginPresenter", "startLoginForExistingWithTimeoutMessage");
        startLoginSequence(R.string.st004, R.string.st005);
    }
    private void startLoginForExistingWithInitSuccessMessage() {
        Log.d("LoginPresenter", "startLoginForExistingWithInitSuccessMessage");
        startLoginSequence(R.string.st009, -1);
    }
    private void startLoginForExistingWithWriteFailed() {
        Log.d("LoginPresenter", "startLoginForExistingWithWriteFailed");
        startLoginSequence(R.string.st008, -1);
    }

    /**
     * Show the login view, clear the MasterData and state and call the loader.
     * Optionally display a timeout message on the view.
     */
    private void startLoginSequence(int descriptionId, int errorMessageId) {
        // - Show login view, set for existing user, which will be adjusted in onCompletion
        getUi().setTitle(R.string.st002);
        getUi().setDescription(descriptionId);

        getUi().setConfirmVisibility(false);
        getUi().setButtonsVisibleAndEnabled(false, false);
        if (errorMessageId != -1) {
            getUi().setErrorMessage(errorMessageId);
        } else {
            getUi().clearErrorMessage();
        }
        getUi().show();

        // - Set MasterData null and Mode UNKNOWN
        mState = State.UNKNOWN;
        mMasterData = null;

        // - Make the load master data call, callback to onLoadFinished()
        MainPresenter mainPresenter = getUi().findPresenter(null);
        mainPresenter.getMasterLoader().read(new AsyncListeningTask.Listener<MasterData>() {
            @Override
            public void onCompletion(MasterData masterData) {
                Log.d("LoginPresenter", "read.onCompletion, masterData is " + masterData);
                mMasterData = masterData;
                mState = (mMasterData == null) ? State.FIRST_TIME : State.EXISTING_USER;
                if (mState == State.FIRST_TIME) {
                    getUi().setTitle(R.string.st001);
                    getUi().setDescription(R.string.st003);
                    getUi().setConfirmVisibility(true);
                }
                getUi().setButtonsVisibleAndEnabled(mState == State.FIRST_TIME, true);
            }
        });
    }

    private void registerTimeoutListenerAndStartTimer() {
        MainPresenter mainPresenter = getUi().findPresenter(null);
        mainPresenter.getTimeoutMonitor().setUserInactivityTimeoutListener(this);
        mainPresenter.getTimeoutMonitor().markTimeAndStart();
        mainPresenter.setSourceAllowEntryAccess(true);
    }

    private void unregisterTimeoutListenerAndStopTimer(boolean allowEntryAccess) {
        MainPresenter mainPresenter = getUi().findPresenter(null);
        mainPresenter.getTimeoutMonitor().setUserInactivityTimeoutListener(null);
        mainPresenter.getTimeoutMonitor().stopTimer();
        mainPresenter.setSourceAllowEntryAccess(allowEntryAccess);
    }

    public void onClickLogin(String password) {
        getUi().dismissKeyboard(false);
        getUi().clearErrorMessage();        // May have been set from startLoginSequence

        // TODO - decrypt and check password (assume ok for now)
        boolean attempt = true;
        Log.d("LoginPresenter", "onClickLogin, attempt is " + attempt);
        if (!attempt) {
            getUi().setErrorMessage(R.string.st006);
        } else {
            mState = State.LOGGED_IN;
            //         mTimingOutSource.setAllowEntryAccess(false);        // Disallow entry reading until logged in.

            registerTimeoutListenerAndStartTimer();
            getUi().hide();
            ListPresenter listPresenter = getUi().findPresenter(ListFragment.class);
            if (listPresenter != null) {
                listPresenter.loadEntries();
            }
        }
    }

    public void onClickInitialise(String password, String confirm) {
        getUi().dismissKeyboard(false);
        getUi().clearErrorMessage();        // May have been set from startLoginSequence

        // TODO - decrypt and check password (assume ok for now)
        boolean match = (password != null && password.equals(confirm));
        Log.d("LoginPresenter", "onClickInitialise, match is " + match);
        if (!match) {
            getUi().setErrorMessage(R.string.st007);
        } else {
            MasterData masterData = new MasterData("salt", password);
            MainPresenter mainPresenter = getUi().findPresenter(null);
            mainPresenter.getMasterLoader().store(masterData, new AsyncListeningTask.Listener<Boolean>() {
                @Override
                public void onCompletion(Boolean success) {
                    Log.d("LoginPresenter", "store.onCompletion, result is " + success);
                    if (success) {
                        startLoginForExistingWithInitSuccessMessage();
                    } else {
                        // Not sure what to do here, try again from the start I guess
                        startLoginForExistingWithWriteFailed();
                    }
                }
            });
        }
    }

    public interface LoginUi extends BaseUi {
        void setTitle(@StringRes int resourceId);
        void setDescription(@StringRes int resourceId);
        void setConfirmVisibility(boolean visibility);
        void setButtonsVisibleAndEnabled(boolean firstTime, boolean enabled);
        String getPassword();
        String getConfirm();
        void dismissKeyboard(boolean andClose);
        void show();
        void hide();
        boolean isShowing();
        void setErrorMessage(@StringRes int resourceId);
        void clearErrorMessage();
    }

}
