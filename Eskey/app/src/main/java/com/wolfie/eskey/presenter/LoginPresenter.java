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
    private final static String KEY_MASTER_SALT = "KEY_MASTER_SALT";
    private final static String KEY_MASTER_KEY = "KEY_MASTER_KEY";
    private final static String KEY_LOGIN_STATE = "KEY_LOGIN_STATE";

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
    public boolean backPressed() {
        if (!getUi().isShowing() || getUi().isKeyboardVisible()) {
            return true;        // Means: not consumed here
        }
        getUi().finish();
        return false;
    }

    @Override
    public void pause() {
        super.pause();
        mIsShowing = getUi().isShowing();
        Log.d("LoginPresenter", "pause, mIsShowing is " + mIsShowing);
        unregisterTimeoutListenerAndStopTimer();
    }

    @Override
    public void onSaveState(Bundle outState) {
        outState.putBoolean(KEY_LOGIN_ACTION_SHEET_SHOWING, mIsShowing);
        String salt = (mMasterData == null) ? null : mMasterData.getSalt();
        String key = (mMasterData == null) ? null : mMasterData.getMasterKey();
        outState.putString(KEY_MASTER_SALT, salt);
        outState.putString(KEY_MASTER_KEY, key);
        outState.putString(KEY_LOGIN_STATE, mState.name());
    }

    @Override
    public void onRestoreState(@Nullable Bundle savedState) {
        mIsShowing = savedState.getBoolean(KEY_LOGIN_ACTION_SHEET_SHOWING, false);
        String salt = savedState.getString(KEY_MASTER_SALT);
        String key = savedState.getString(KEY_MASTER_KEY);
        mMasterData = (key == null || salt == null) ? null : new MasterData(salt, key);
        mState = State.valueOf(savedState.getString(KEY_LOGIN_STATE));
    }

    /**
     * Used by other presenters (eg during resume()) to check if data can be displayed.
     * Note that LoginPresenter.mState has its value restored by onRestoreState, before
     * any presenter's resume()s are called.
     */
    public boolean isLoggedIn() {
        return (mState == State.LOGGED_IN);
    }

    @Override
    public void onUserInactivityTimeout() {
        // Note that MasterLoader won't be blocked by TimingOutSource
        unregisterTimeoutListenerAndStopTimer();

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
        // Show login view, setup for existing user, which will be adjusted in onCompletion
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

        // Set MasterData null and Mode UNKNOWN; determined in onCompletion
        mState = State.UNKNOWN;
        mMasterData = null;

        // Make the load master data call,
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
    }

    /**
     * Note that after this call, and before the call to registerTimeoutListenerAndStartTimer,
     * call to isTimedOut() can still return true/false.  This allows the listPresenter to
     * populate itself upon resume.
     */
    private void unregisterTimeoutListenerAndStopTimer() {
        MainPresenter mainPresenter = getUi().findPresenter(null);
        mainPresenter.getTimeoutMonitor().setUserInactivityTimeoutListener(null);
        mainPresenter.getTimeoutMonitor().stopTimer();
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
        void setErrorMessage(@StringRes int resourceId);
        void clearErrorMessage();
        void finish();

        // The following are implemented in ActionSheetFragment
        void dismissKeyboard(boolean andClose);
        boolean isKeyboardVisible();
        void show();
        void hide();
        boolean isShowing();
    }

}
