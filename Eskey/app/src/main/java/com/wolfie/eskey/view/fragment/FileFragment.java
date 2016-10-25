package com.wolfie.eskey.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.wolfie.eskey.R;
import com.wolfie.eskey.presenter.FilePresenter;
import com.wolfie.eskey.presenter.FilePresenter.FileUi;
import com.wolfie.eskey.presenter.FilePresenter.StorageType;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.support.design.widget.Snackbar.LENGTH_LONG;

/**
 * Created by david on 12/10/16.
 */

public class FileFragment extends ActionSheetFragment implements FileUi {

    public static final int PERMISSIONS_REQUEST_STORAGE = 123;

    @Nullable
    @BindView(R.id.text_title)
    TextView mTextTitle;

    @Nullable
    @BindView(R.id.text_description)
    TextView mTextDescription;

    @Nullable
    @BindView(R.id.edit_text_name)
    EditText mEditName;

    @Nullable
    @BindView(R.id.input_layout_password)
    View mPasswordLayout;

    @Nullable
    @BindView(R.id.edit_text_password)
    EditText mPassword;

    @Nullable
    @BindView(R.id.text_error)
    TextView mTextError;

    @Nullable
    @BindView(R.id.storage_type_private)
    RadioButton mStorageTypePrivate;

    @Nullable
    @BindView(R.id.storage_type_public)
    RadioButton mStorageTypePublic;

    @Nullable
    @BindView(R.id.button_ok)
    Button mButtonOk;

    @Nullable
    @BindView(R.id.button_cancel)
    Button mButtonCancel;

    @Nullable
    @BindView(R.id.overwrite_existing_switch)
    SwitchCompat mOverwriteSwitch;

    private boolean mAllowOnRequestCheckedChangeCallback = true;

    private Unbinder mUnbinder2;

    private FilePresenter mFilePresenter;

    @Override
    public FilePresenter getPresenter() {
        return mFilePresenter;
    }

    public FileFragment() {
        mFilePresenter = new FilePresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        View content = inflater.inflate(R.layout.fragment_file, container, false);
        mHolderView.addView(content);
        // This bind will re-bind the superclass members, so the entire view hierarchy must be
        // available, hence the content should be added to the parent view first.
        mUnbinder2 = ButterKnife.bind(this, view);
        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilePresenter.onClickOk(mEditName.getText().toString());
            }
        });
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilePresenter.onClickCancel();
            }
        });
        mStorageTypePrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Only process if we're not in the middle of a call to setStorageType().
                if (isChecked) {
                    if (mAllowOnRequestCheckedChangeCallback) {
                        mStorageTypePrivate.setChecked(false);
                        // Ask listener to handle the click/checking.  They may make call to setStorageType()
                        // either while this call is active, or after it returns.  Regardless, it
                        // will not result in further calls to this handler.
                        mFilePresenter.onRequestStorageTypeSelect(StorageType.TYPE_PRIVATE);
                    }
                }
            }
        });
        mStorageTypePublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mAllowOnRequestCheckedChangeCallback) {
                        mStorageTypePublic.setChecked(false);
                        mFilePresenter.onRequestStorageTypeSelect(StorageType.TYPE_PUBLIC);
                    }
                }
            }
        });
        return view;
    }

    @Override
    public String getPassword() {
        return mPassword.getText().toString();
    }

    @Override
    public void setPasswordVisibility(boolean isVisible) {
        mPasswordLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean isOverwrite() {
        return mOverwriteSwitch.isChecked();
    }

    @Override
    public void setOverwriteSwitchVisibility(boolean isVisible) {
        mOverwriteSwitch.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * Calls to setStorageType do not cause a propagation to the
     * OnRequestCheckedChangeListener.
     */
    @Override
    public void setStorageType(StorageType storageType) {
        mAllowOnRequestCheckedChangeCallback = false;
        if (storageType == StorageType.TYPE_PUBLIC) {
            mStorageTypePublic.setChecked(true);
            mStorageTypePrivate.setChecked(false);
        } else {
            mStorageTypePublic.setChecked(false);
            mStorageTypePrivate.setChecked(true);
        }
        mAllowOnRequestCheckedChangeCallback = true;
    }

    @Override
    public StorageType getStorageType() {
        return (mStorageTypePublic.isChecked() ? StorageType.TYPE_PUBLIC : StorageType.TYPE_PRIVATE);
    }

    @Override
    public void setStorageTypeEnabled(boolean enabled) {
        mStorageTypePublic.setEnabled(enabled);
        mStorageTypePrivate.setEnabled(enabled);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder2.unbind();
    }

    @Override
    public void requestStoragePermission() {
        mBaseActivity.requestPermissions(this,
                new String[] { FilePresenter.STORAGE_PERMISSION },
                PERMISSIONS_REQUEST_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        mFilePresenter.onRequestStoragePermissionsResult(grantResults);
    }

    @Override
    public void setTitleText(@StringRes int resourceId) {
        mTextTitle.setText(resourceId);
    }

    @Override
    public void setFileName(String fileName) {
        mEditName.setText(fileName);
    }

    @Override
    public void setDescription(@StringRes int resourceId) {
        mTextDescription.setText(resourceId);
    }

    @Override
    public void setErrorMessage(String text) {
        mTextError.setVisibility(View.VISIBLE);
        mTextError.setText(text);
    }

    @Override
    public void clearDescription() {
        mTextDescription.setText("");
    }

    @Override
    public void setErrorMessage(@StringRes int resourceId) {
        mTextError.setVisibility(View.VISIBLE);
        mTextError.setText(resourceId);
    }

    @Override
    public void setEnabledOkButton(boolean enabled) {
        mButtonOk.setEnabled(enabled);
    }

    @Override
    public void clearErrorMessage() {
        mTextError.setVisibility(View.GONE);
    }

    @Override
    public void setOkButtonText(@StringRes int resourceId) {
        mButtonOk.setText(resourceId);
    }

    @Override
    public void setPrivateButtonLabel(String text) {
        mStorageTypePrivate.setText(text);
    }

    @Override
    public void setPublicButtonLabel(String text) {
        mStorageTypePublic.setText(text);
    }

    @Override
    public void onShowComplete() {
        mFilePresenter.onShow();
    }

    @Override
    public void onHideComplete() {
    }

    @Override
    public void onTouchBackground() {
    }


}

