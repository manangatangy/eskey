package com.wolfie.eskey.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import static android.text.TextUtils.isEmpty;
import android.util.Log;

import com.wolfie.eskey.R;
import com.wolfie.eskey.model.MasterData;
import com.wolfie.eskey.model.loader.AsyncListeningTask;
import com.wolfie.eskey.util.crypto.Crypter;
import com.wolfie.eskey.util.crypto.SpongyCrypter;
import com.wolfie.eskey.view.ActionSheetUi;

import com.wolfie.eskey.presenter.LoginPresenter.LoginUi;
import com.wolfie.eskey.view.fragment.DrawerFragment;
import com.wolfie.eskey.view.fragment.EditFragment;
import com.wolfie.eskey.view.fragment.FileFragment;
import com.wolfie.eskey.view.fragment.HelpFragment;
import com.wolfie.eskey.view.fragment.ListFragment;
import com.wolfie.eskey.util.TimeoutMonitor.UserInactivityTimeoutListener;
import com.wolfie.eskey.view.fragment.SettingsFragment;

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

    private final static String KEY_LOGIN_SHOWING = "KEY_LOGIN_SHOWING";
    private final static String KEY_LOGIN_STATE = "KEY_LOGIN_STATE";
    private final static String KEY_MASTER_SALT = "KEY_MASTER_SALT";
    private final static String KEY_MASTER_KEY = "KEY_MASTER_KEY";
    private final static String KEY_DECRYPTED_KEY = "KEY_DECRYPTED_KEY";

    private boolean mIsShowing;
    private State mState = State.UNKNOWN;
//    private String mMasterSalt;
//    private String mMasterKey;
    private String mDecryptedKey;
    private MasterData mMasterData;

    //private MasterData mMasterData;

    // This member is not saved and restored directly, rather it is recreated from other
    // saved/restored fields; mState.  Entry decryption needs medium strength
    private Crypter mMediumCrypter;

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
        outState.putBoolean(KEY_LOGIN_SHOWING, mIsShowing);
        outState.putString(KEY_LOGIN_STATE, mState.name());
        outState.putString(KEY_MASTER_SALT, (mMasterData == null) ? null : mMasterData.getSalt());
        outState.putString(KEY_MASTER_KEY, (mMasterData == null) ? null : mMasterData.getKey());
        outState.putString(KEY_DECRYPTED_KEY, mDecryptedKey);
    }

    @Override
    public void onRestoreState(@Nullable Bundle savedState) {
        mIsShowing = savedState.getBoolean(KEY_LOGIN_SHOWING, false);
        mState = State.valueOf(savedState.getString(KEY_LOGIN_STATE));
        String salt = savedState.getString(KEY_MASTER_SALT);
        String key = savedState.getString(KEY_MASTER_KEY);
        mDecryptedKey = savedState.getString(KEY_DECRYPTED_KEY);

        mMediumCrypter =
                (mState == State.LOGGED_IN
                        && !isEmpty(salt)
                        && !isEmpty(mDecryptedKey))
                        ? SpongyCrypter.makeMedium(salt, mDecryptedKey)
                        : null;
        mMasterData = (!isEmpty(salt) && !isEmpty(key)) ? new MasterData(salt, key) : null;
    }

    /**
     * Returns null if not logged in.  Otherwise returns a Crypter ready for use with
     * the EntryLoader.  This may be called prior to resume, since the mMediumCrypter fields
     * would have been restored/recreated during onRestoreState().
     */
    public Crypter getMediumCrypter() {
        return mMediumCrypter;
    }

    /**
     * The MasterData, as returned from the database, i.e., with the key encrypted.
     */
    public MasterData getMasterData() {
        return mMasterData;
    }

    @Override
    public void onUserInactivityTimeout() {
        // Note that MasterLoader won't be blocked by TimingOutSource
        closeAll(false);
        startLoginForExistingWithTimeoutMessage();
    }

    public void clearAndLogout() {
        closeAll(true);
        startLoginForExistingWithRemasterSuccessMessage();
    }

    private void closeAll(boolean clearCrypter) {
        unregisterTimeoutListenerAndStopTimer();

        DrawerPresenter drawerPresenter = getUi().findPresenter(DrawerFragment.class);
        drawerPresenter.closeDrawer();
        EditPresenter editPresenter = getUi().findPresenter(EditFragment.class);
        editPresenter.hide();
        FilePresenter filePresenter = getUi().findPresenter(FileFragment.class);
        filePresenter.hide();
        HelpPresenter helpPresenter = getUi().findPresenter(HelpFragment.class);
        helpPresenter.hide();
        SettingsPresenter settingsPresenter = getUi().findPresenter(SettingsFragment.class);
        settingsPresenter.hide();

        ListPresenter listPresenter = getUi().findPresenter(ListFragment.class);
        // To cause the listPresenter to clear its list, either the time out
        // should occur, or the crypter should be clear.
        if (clearCrypter) {
            mMediumCrypter = null;
        }
        listPresenter.loadEntries();    // This will clear list if null crypter.

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
    private void startLoginForExistingWithRemasterSuccessMessage() {
        Log.d("LoginPresenter", "startLoginForExistingWithRemasterSuccessMessage");
        startLoginSequence(R.string.st029, -1);
    }

    /**
     * Show the login view, clear the MasterData and state and call the loader.
     * Optionally display a timeout message on the view.
     */
    private void startLoginSequence(int descriptionId, int errorMessageId) {
        // Show login view, setup for existing user, which will be adjusted in onCompletion
        getUi().setTitle(R.string.st002);
        getUi().setDescription(descriptionId);
        getUi().clearPasswords();

        getUi().setConfirmVisibility(false);
        getUi().setButtonsVisibleAndEnabled(false, false);
        if (errorMessageId != -1) {
            getUi().setErrorMessage(errorMessageId);
        } else {
            getUi().clearErrorMessage();
        }
        getUi().show();

        // Set MasterData info null, Mode UNKNOWN; determined in onCompletion
        mState = State.UNKNOWN;
        mMasterData = null;
        mDecryptedKey = null;
        mMediumCrypter = null;

        // Make the load master data call,
        MainPresenter mainPresenter = getUi().findPresenter(null);
        mainPresenter.getMasterLoader().read(new AsyncListeningTask.Listener<MasterData>() {
            @Override
            public void onCompletion(MasterData masterData) {
                Log.d("LoginPresenter", "read.onCompletion, masterData is " + masterData);
                mState = (masterData == null) ? State.FIRST_TIME : State.EXISTING_USER;
                if (mState == State.FIRST_TIME) {
                    getUi().setTitle(R.string.st001);
                    getUi().setDescription(R.string.st003);
                    getUi().setConfirmVisibility(true);
                } else {
                    mMasterData = masterData;
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

        // Fetch the salt and encrypted master-key from the database.
        // Set the user-entered password and attempt to decrypt the master-key.
        SpongyCrypter strongCrypter = SpongyCrypter.makeStrong(mMasterData.getSalt(), password);
        mDecryptedKey = strongCrypter.decrypt(mMasterData.getKey());
        // decrypted key is stored for re-making mMediumCrypter upon restore

        Log.d("LoginPresenter", "onClickLogin, attempt is " + (mDecryptedKey == null));
        if (mDecryptedKey == null) {
            getUi().setErrorMessage(R.string.st006);
        } else {
            mState = State.LOGGED_IN;
            mMediumCrypter = SpongyCrypter.makeMedium(mMasterData.getSalt(), mDecryptedKey);
            // mMediumCrypter is returned by getCrypter() for ListPresenter to apply to EntryLoader.

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

        boolean match = (password != null && password.equals(confirm));
        boolean hasLeadingTrailingWhiteSpace = (password != null &&
                password.trim().length() != password.length());
        Log.d("LoginPresenter", "onClickInitialise, match is " + match);
        if (!match) {
            getUi().setErrorMessage(R.string.st007);
        } else if (hasLeadingTrailingWhiteSpace) {
            getUi().setErrorMessage(R.string.st011);
        } else {
            // First time database use; generate new salt and master-key.  Then set the
            // user-created password in a strong Crypter and use it to encrypt the master-key.
            // The resulting encrypted master-key and the salt are then stored in the database.
            String salt = SpongyCrypter.generateSalt();
            String masterKey = SpongyCrypter.generateMasterKey();
            String encryptedMasterKey = SpongyCrypter.makeStrong(salt, password).encrypt(masterKey);

            MasterData masterData = new MasterData(salt, encryptedMasterKey);
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

    public interface LoginUi extends ActionSheetUi {

        void setTitle(@StringRes int resourceId);
        void setDescription(@StringRes int resourceId);
        void setConfirmVisibility(boolean visibility);
        void setButtonsVisibleAndEnabled(boolean firstTime, boolean enabled);
        String getPassword();
        String getConfirm();
        void clearPasswords();
        void setErrorMessage(@StringRes int resourceId);
        void clearErrorMessage();
        void finish();

    }

}
