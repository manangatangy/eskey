package com.wolfie.eskey.presenter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.wolfie.eskey.R;
import com.wolfie.eskey.view.BaseUi;
import com.wolfie.eskey.presenter.FilePresenter.FileUi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Created by david on 12/10/16.
 */

public class FilePresenter extends BasePresenter<FileUi> {

    public final static String STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final static String KEY_FILE_ACTION_SHEET_SHOWING = "KEY_FILE_ACTION_SHEET_SHOWING";
    private final static String KEY_FILE_PUBLIC_STORAGE_SELECTED = "KEY_FILE_PUBLIC_STORAGE_SELECTED";

    private boolean mIsShowing;
    private boolean mIsExporting;
    private boolean mPublicStorageSelected;

    public FilePresenter(FileUi fileUi) {
        super(fileUi);
    }

    public void exporting() {
        mIsExporting = true;
        getUi().setTitleText(R.string.st014);
        getUi().setFileName("eskey.txt");
        getUi().setStorageType(StorageType.TYPE_PRIVATE);
        getUi().show();
    }

    public void importing() {
        mIsExporting = false;
        getUi().setTitleText(R.string.st015);
        getUi().setFileName("eskey.txt");
        getUi().setStorageType(StorageType.TYPE_PRIVATE);
        getUi().show();
    }

    @Override
    public void resume() {
        super.resume();
        MainPresenter mainPresenter = getUi().findPresenter(null);
        if (!mIsShowing || mainPresenter == null || mainPresenter.getTimeoutMonitor().isTimedOut()) {
            getUi().hide();
        } else {
            // TODO check that onSHow is called by show(), even if the frag is already showing
            // since onShow will not be called if the activitySheetFragment is already showing,
            // which it might be if the views are restored automatically.
            getUi().show();
        }
    }

    @Override
    public void pause() {
        super.pause();
        mIsShowing = getUi().isShowing();
        mPublicStorageSelected = (getUi().getStorageType() == StorageType.TYPE_PUBLIC);
        getUi().dismissKeyboard(false);
    }

    @Override
    public void onSaveState(Bundle outState) {
        outState.putBoolean(KEY_FILE_ACTION_SHEET_SHOWING, mIsShowing);
        outState.putBoolean(KEY_FILE_PUBLIC_STORAGE_SELECTED, mPublicStorageSelected);
    }

    @Override
    public void onRestoreState(@Nullable Bundle savedState) {
        mIsShowing = savedState.getBoolean(KEY_FILE_ACTION_SHEET_SHOWING, false);
        mPublicStorageSelected = savedState.getBoolean(KEY_FILE_PUBLIC_STORAGE_SELECTED, false);
    }

    public void onRequestStorageTypeSelect(StorageType requestedStorageType) {
        // Click to private is always allowed, but click to public triggers permissions check.
        if (requestedStorageType == StorageType.TYPE_PRIVATE) {
            getUi().setStorageType(StorageType.TYPE_PRIVATE);
        } else {
            checkIfPublicStorageIsAllowed();
        }
    }

    public void onShow() {
        // If media isn't available, then disable OK and storage type, set storage type private
        // Otherwise, enable OK and storage type
        getUi().clearErrorMessage();

        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            getUi().setErrorMessage(R.string.st013);
            getUi().setDescription(R.string.st018);
            getUi().setStorageType(StorageType.TYPE_PRIVATE);
            getUi().setEnabledOkButton(false);
            getUi().setStorageTypeEnabled(false);
            // TODO - set radio button texts to not include paths

            return;
        }
        getUi().setEnabledOkButton(true);
        getUi().setStorageTypeEnabled(true);
        getUi().setDescription(R.string.st019);
        // TODO - get paths for private and public and set to the radio button texts

        // If resuming from a previous setting of PUBLIC storage, then check it is still permitted.
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

    private String absolutePath;

    public void onClickOk(String fileName) {
        getUi().dismissKeyboard(true);
        File ioFile = new File(absolutePath, fileName);
        if (mIsExporting) {
            try {
                FileOutputStream f = new FileOutputStream(ioFile);
                PrintWriter pw = new PrintWriter(f);
                pw.println("Hi , How are you");
                pw.println("Hello");
                pw.flush();
                pw.close();
                f.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i("TAG", "******* File not found. Did you" +
                        " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

//            InputStream is = getUi().getActivity().getResources().openRawResource(R.raw.textfile);
            FileInputStream fis;
            try {
                fis = new FileInputStream(ioFile);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr, 8192);    // 2nd arg is buffer size
                String test;
                while (true) {
                    test = br.readLine();
                    // readLine() returns null if no more lines in the file
                    if(test == null) break;
                }
                br.close();
                isr.close();
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();        // thrown by new FileInputStream()
            } catch (IOException e) {           // thrown by BufferedReader.readLine() and BufferedReader.close()
                e.printStackTrace();
            }
        }
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

    public enum StorageType {
        TYPE_PRIVATE,
        TYPE_PUBLIC
    }

    public interface FileUi extends BaseUi {

        void setTitleText(@StringRes int resourceId);
        void setFileName(String fileName);
        void setDescription(@StringRes int resourceId);
        void setDescription(String text);
        void clearDescription();
        void setErrorMessage(@StringRes int resourceId);
        void setEnabledOkButton(boolean enabled);
        void clearErrorMessage();

        void setStorageTypeEnabled(boolean enabled);
        void setStorageType(StorageType storageType);
        StorageType getStorageType();

        void requestStoragePermission();

        // The following are implemented in ActionSheetFragment
        void dismissKeyboard(boolean andClose);
        boolean isKeyboardVisible();
        void show();
        void hide();
        boolean isShowing();
    }

}
