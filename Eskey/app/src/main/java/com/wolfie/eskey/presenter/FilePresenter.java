package com.wolfie.eskey.presenter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.wolfie.eskey.R;
import com.wolfie.eskey.model.MasterData;
import com.wolfie.eskey.model.loader.AsyncListeningTask;
import com.wolfie.eskey.model.loader.IoLoader;
import com.wolfie.eskey.view.ActionSheetUi;
import com.wolfie.eskey.presenter.FilePresenter.FileUi;
import com.wolfie.eskey.view.fragment.ListFragment;
import com.wolfie.eskey.view.fragment.LoginFragment;

import java.io.File;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Backup and restore are using cipher text, while export and import are in clear text.
 * Three different storage targets are supported; public, private and internal.  The
 * first two are on the "removable media", so are only enabled after checking if the
 * media is mounted lol.  The internal storage option is always available.  Furthermore
 * permission is needed to access the external storage, so checks are also made for
 * that. There is an email-backup option that is only available for backup of cipher text.
 * If this option is chosen, the storage type is forced to internal, because that is the
 * only file path that can be shared with the email activity.
 */

public class FilePresenter extends BasePresenter<FileUi>
    implements AsyncListeningTask.Listener<IoLoader.IoResult> {

    public final static String STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final static String KEY_FILE_ACTION_SHEET_SHOWING = "KEY_FILE_ACTION_SHEET_SHOWING";
    private final static String KEY_FILE_IS_EXPORTING = "KEY_FILE_IS_EXPORTING";
    private final static String KEY_FILE_IS_CLEAR_TEXT = "KEY_FILE_IS_CLEAR_TEXT";

    private boolean mIsShowing;
    private boolean mIsExporting;
    private boolean mIsClearText;

    //These members are assigned by onShow, after resume, so they don't need to be saved.
    private File mPathPublic;
    private File mPathPrivate;
    private File mPathInternal;
    boolean mMediaIsMounted;

    public FilePresenter(FileUi fileUi) {
        super(fileUi);
    }

    public void exporting() {
        mIsExporting = true;
        mIsClearText = true;
        getUi().setOkButtonText(R.string.st022);
        init();
    }

    public void importing() {
        mIsExporting = false;
        mIsClearText = true;
        getUi().setOkButtonText(R.string.st023);
        init();
    }

    public void backup() {
        mIsExporting = true;
        mIsClearText = false;
        getUi().setOkButtonText(R.string.st024);
        init();
    }

    public void restore() {
        mIsExporting = false;
        mIsClearText = false;
        getUi().setOkButtonText(R.string.st025);
        init();
    }

    private void init() {
        int id = (mIsClearText
                ? (mIsExporting ? R.string.st014 : R.string.st015)
                : (mIsExporting ? R.string.st014a : R.string.st015a));
        getUi().setTitleText(id);
        // Only need a password from the user if restoring.
        getUi().setPasswordVisibility(!mIsClearText && !mIsExporting);
        // Only need overwrite switch for importing
        getUi().setOverwriteSwitchVisibility(!mIsExporting);
        getUi().setEmailBackupSwitchVisibility(mIsExporting && !mIsClearText);
        getUi().setFileName("eskey.txt");
        getUi().setStorageType(StorageType.TYPE_INTERNAL);
        getUi().show();
    }

    @Override
    public void resume() {
        super.resume();
        MainPresenter mainPresenter = getUi().findPresenter(null);
        if (!mIsShowing || mainPresenter == null || mainPresenter.getTimeoutMonitor().isTimedOut()) {
            hide();
        } else {
            // The user may have altered media/storage-access while we were paused, must re-check
            onShow();
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
        outState.putBoolean(KEY_FILE_ACTION_SHEET_SHOWING, mIsShowing);
        outState.putBoolean(KEY_FILE_IS_EXPORTING, mIsExporting);
        outState.putBoolean(KEY_FILE_IS_CLEAR_TEXT, mIsClearText);
    }

    @Override
    public void onRestoreState(@Nullable Bundle savedState) {
        mIsShowing = savedState.getBoolean(KEY_FILE_ACTION_SHEET_SHOWING, false);
        mIsExporting = savedState.getBoolean(KEY_FILE_IS_EXPORTING, false);
        mIsClearText = savedState.getBoolean(KEY_FILE_IS_CLEAR_TEXT, false);
    }

    public void onRequestStorageTypeSelect(StorageType requestedStorageType) {
        // Click to private/internal is always allowed, but click to public triggers permissions check.
        if (requestedStorageType == StorageType.TYPE_PUBLIC) {
            checkIfPublicStorageIsAllowed();
        } else {
            getUi().setStorageType(requestedStorageType);
        }
    }

    public void onEmailSwitchChanged(boolean isChecked) {
        if (isChecked) {
            // Only allow internal storage.
            getUi().setStorageTypePublicEnabled(false);
            getUi().setStorageTypePrivateEnabled(false);
            getUi().setStorageType(StorageType.TYPE_INTERNAL);
        } else {
            if (mMediaIsMounted) {
                getUi().setStorageTypePublicEnabled(true);
                getUi().setStorageTypePrivateEnabled(true);
            }
        }
    }

    public void onShow() {
        // If media isn't available, then only allow internal storage type.
        // Otherwise, enable OK and all storage types.

        mPathInternal = getContext().getFilesDir();
        // This path is typically /data/user/0/com.wolfie.eskey/files and is not accessible
        // via usb mounting, but it can be shared with a FileProvider, for handing to the email app.
        getUi().setInternalButtonLabel(getContext().getString(R.string.st033, mPathInternal.getPath()));
        getUi().setStorageTypeInternalEnabled(true);       // Internal always allowed.

        String state = Environment.getExternalStorageState();
        mMediaIsMounted = Environment.MEDIA_MOUNTED.equals(state);
        if (!mMediaIsMounted) {
            getUi().setErrorMessage(R.string.st013);
            getUi().setDescription(R.string.st018);
            getUi().setStorageTypePublicEnabled(false);
            getUi().setStorageTypePrivateEnabled(false);
            getUi().setPrivateButtonLabel(getContext().getString(R.string.st020, "<not available>"));
            getUi().setPublicButtonLabel(getContext().getString(R.string.st021, "<not available>"));
            getUi().setStorageType(StorageType.TYPE_INTERNAL);
        } else {
            getUi().clearErrorMessage();
            getUi().setDescription(R.string.st019);
            getUi().setStorageTypePublicEnabled(true);      // Public and private are now allowed.
            getUi().setStorageTypePrivateEnabled(true);
            mPathPublic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            // This path is /storage/emulated/0/Downloads/
            // It is the same location accessed by various file browsers and is also visible
            // when the usb port is mounted to the host for read/writing.
            // Trying to access this location from the code may cause EACCES (Permission Denied)
            mPathPrivate = getContext().getExternalFilesDir(null);
            // This path is /storage/emulated/0/Android/data/com.wolfie.eskey/files/
            // and the file can be written by the activity without asking for permission.
            // However the file is not accessible from file browsers.
            // It can be accessed using adb from the host computer if the device has debug enabled.
            // $ adb pull /storage/emulated/0/Android/data/com.wolfie.eskey/files/test.txt local_file.txt
            // and $ adb push settings.gradle /storage/emulated/0/Android/data/com.wolfie.eskey/files/.
            getUi().setPrivateButtonLabel(getContext().getString(R.string.st020, mPathPrivate.getPath()));
            getUi().setPublicButtonLabel(getContext().getString(R.string.st021, mPathPublic.getPath()));
        }
        getUi().setEnabledOkButton(true);

        // If resuming from a previous setting of PUBLIC storage, then check we are still permitted.
        // For this check, reset storage to private until the check is confirmed.
        boolean publicStorageSelected = (getUi().getStorageType() == StorageType.TYPE_PUBLIC);
        if (publicStorageSelected) {
            getUi().setStorageType(StorageType.TYPE_PRIVATE);
            checkIfPublicStorageIsAllowed();
        }
    }

    private void checkIfPublicStorageIsAllowed() {
        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), STORAGE_PERMISSION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            getUi().setStorageType(StorageType.TYPE_PUBLIC);
        } else {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getUi().getActivity(), STORAGE_PERMISSION)) {
                getUi().setErrorMessage(R.string.st016);
            } else {
                getUi().requestStoragePermission();
            }
        }
    }

    public void onRequestStoragePermissionsResult(int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was granted, yay!
            getUi().setStorageType(StorageType.TYPE_PUBLIC);
        } else {
            getUi().setErrorMessage(R.string.st017);
        }
    }

    private File getFile() {
        return new File(
                (getUi().getStorageType() == StorageType.TYPE_PUBLIC) ? mPathPublic
                        : (getUi().getStorageType() == StorageType.TYPE_PRIVATE) ? mPathPrivate
                        : mPathInternal,
                getUi().getFileName());
    }

    public void onClickOk() {
        getUi().dismissKeyboard(false);
        getUi().clearErrorMessage();

        File ioFile = getFile();

        MainPresenter mainPresenter = getUi().findPresenter(null);
        LoginPresenter loginPresenter = getUi().findPresenter(LoginFragment.class);
        if (mainPresenter != null && loginPresenter != null) {
            // If cipher text (ie backup/restore) then must provide password (for import) and MasterData
            MasterData masterData = null;
            String password = null;
            if (!mIsClearText) {
                masterData = loginPresenter.getMasterData();
                password = getUi().getPassword();
            }
            IoLoader ioLoader = mainPresenter.makeIoLoader(loginPresenter.getMediumCrypter());
            if (mIsExporting) {
                ioLoader.export(masterData, ioFile, this);
            } else {
                ioLoader.inport(password, getUi().isOverwrite(), ioFile, this);
            }
        }
    }

    @Override
    public void onCompletion(IoLoader.IoResult ioResult) {
        if (ioResult.mFailureMessage != null) {
            getUi().setErrorMessage(ioResult.mFailureMessage);
        }
        if (ioResult.mSuccessMessage != null) {
            if (!mIsExporting) {
                // Refresh list
                ListPresenter listPresenter  = getUi().findPresenter(ListFragment.class);
                listPresenter.loadEntries();
            }
            if (mIsExporting && !mIsClearText && getUi().isEmailBackup()) {
                File ioFile = getFile();
                getUi().navigateToEmail("david.x.weiss@gmail.com", "Eskey backup file", ioFile);
            } else {
                getUi().showBanner(ioResult.mSuccessMessage);       // Result of file i/o
            }
            hide();     // Close the action sheet.
        }
    }

    public void onEmailActivityResult(int resultCode, Intent intent) {
        if (resultCode == RESULT_CANCELED) {
            getUi().showBanner(getContext().getString(R.string.st032));
        } else if (resultCode == RESULT_OK) {
            getUi().showBanner(getContext().getString(R.string.st031));
        }
    }

    public void onClickCancel() {
        getUi().dismissKeyboard(true);
    }

    @Override
    public boolean backPressed() {
        if (!getUi().isShowing() || getUi().isKeyboardVisible()) {
            return true;        // Means: not consumed here
        }
        hide();
        return false;
    }

    public void hide() {
        getUi().hide();
    }

    public enum StorageType {
        TYPE_PRIVATE,
        TYPE_PUBLIC,
        TYPE_INTERNAL
    }

    public interface FileUi extends ActionSheetUi {

        void setTitleText(@StringRes int resourceId);
        void setFileName(String fileName);
        void setDescription(@StringRes int resourceId);
        void clearDescription();
        void setErrorMessage(@StringRes int resourceId);
        void setErrorMessage(String text);
        void setEnabledOkButton(boolean enabled);
        void clearErrorMessage();
        void setOkButtonText(@StringRes int resourceId);

        void setPrivateButtonLabel(String text);
        void setInternalButtonLabel(String text);
        void setPublicButtonLabel(String text);

//        void setStorageTypeEnabled(boolean enabled);
        String getFileName();
        String getPassword();
        void setPasswordVisibility(boolean isVisible);
        boolean isOverwrite();
        void setOverwriteSwitchVisibility(boolean isVisible);
        boolean isEmailBackup();
        void setEmailBackupSwitchVisibility(boolean isVisible);
        void setStorageTypePrivateEnabled(boolean enable);
        void setStorageTypePublicEnabled(boolean enable);
        void setStorageTypeInternalEnabled(boolean enable);
        void setStorageType(StorageType storageType);
        StorageType getStorageType();

        void requestStoragePermission();
        void navigateToEmail(String emailAddress, String subject, File backupFile);
    }

}
