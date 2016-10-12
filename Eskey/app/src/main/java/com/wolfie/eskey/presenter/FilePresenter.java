package com.wolfie.eskey.presenter;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;

import com.wolfie.eskey.view.BaseUi;
import com.wolfie.eskey.presenter.FilePresenter.FileUi;

import java.io.File;
import java.io.IOException;

/**
 * Created by david on 12/10/16.
 */

public class FilePresenter extends BasePresenter<FileUi> {

    private final static String KEY_FILE_ACTION_SHEET_SHOWING = "KEY_FILE_ACTION_SHEET_SHOWING";

    private boolean mIsShowing;
    private boolean mIsExporting;

    public FilePresenter(FileUi fileUi) {
        super(fileUi);
    }

    public void exporting() {
        mIsExporting = true;
        getUi().show();
    }

    public void importing() {
        mIsExporting = false;
        getUi().show();
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
        getUi().dismissKeyboard(false);
    }

    @Override
    public void onSaveState(Bundle outState) {
        outState.putBoolean(KEY_FILE_ACTION_SHEET_SHOWING, mIsShowing);
    }

    @Override
    public void onRestoreState(@Nullable Bundle savedState) {
        mIsShowing = savedState.getBoolean(KEY_FILE_ACTION_SHEET_SHOWING, false);
    }

    public void onShow() {
        getUi().setTitleText(mIsExporting ? "Export to cleartext" : "Import from cleartext");
        getUi().clearErrorMessage();
        getUi().setFileName("eskey.txt");

        String desc = "External storage is not available";
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            String absolutePath = directory.getAbsolutePath();
            String canonicalPath = "<not available>";
            try {
                canonicalPath = directory.getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
            desc = mIsExporting
                    ? "Specify the export filename to be written into directory:\n"
                    : "Specify the import filename to be read from directory:\n";
            desc = desc + absolutePath;
            desc = desc + "\n(canonical: " + canonicalPath + ")";
            if (mIsExporting) {
                desc = desc + "\nWarning: this file is not encrypted and is world-readable";
            }
        } else {
            desc = desc + "\nstate is '" + state + "'";
        }
        getUi().setDescription(desc);

    }

    public void onClickOk(String fileName) {
        getUi().dismissKeyboard(true);
        getUi().hide();
//        MainPresenter mainPresenter = getUi().findPresenter(null);
//        if (mEntry.isNew()) {
//            mainPresenter.getEntryLoader().insert(mEntry, this);
//        } else {
//            mainPresenter.getEntryLoader().update(mEntry, this);
//        }
    }

    public void onClickCancel() {
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

//    public File getAlbumStorageDir(String albumName) {
//        // Get the directory for the user's public pictures directory.
//        File file = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_DOCUMENTS), albumName);
//        if (!file.mkdirs()) {
//            Log.e(LOG_TAG, "Directory not created");
//        }
//        return file;
//    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

    public interface FileUi extends BaseUi {

        void setTitleText(String title);
        void setFileName(String title);
        void setDescription(@StringRes int resourceId);
        void setDescription(String text);
        void clearDescription();
        void setErrorMessage(@StringRes int resourceId);
        void clearErrorMessage();

        // The following are implemented in ActionSheetFragment
        void dismissKeyboard(boolean andClose);
        boolean isKeyboardVisible();
        void show();
        void hide();
        boolean isShowing();
    }

}
