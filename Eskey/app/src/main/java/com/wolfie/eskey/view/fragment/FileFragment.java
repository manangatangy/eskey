package com.wolfie.eskey.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    @BindView(R.id.text_error)
    TextView mTextError;

    @Nullable
    @BindView(R.id.storage_radio_group)
    RadioGroup mStorageTypeGroup;

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
        mStorageTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            /**
             * The radio buttons do not change checked'ness immediately upon the user click.
             * Instead, the OnRequestCheckedChangeListener is called and should it return
             * true, then change is made to the view.  Otherwise the view state is left with
             * the previous button still checked.
             */
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Only process if we're not in the middle of a call to setStorageType().
                if (mAllowOnRequestCheckedChangeCallback) {
                    StorageType requestedStorageType;
                    // First, place radio group (back) in state prior to this click.
                    if (checkedId == R.id.storage_type_public) {
                        mStorageTypePrivate.setChecked(true);
                        mStorageTypePublic.setChecked(false);
                        requestedStorageType = StorageType.TYPE_PUBLIC;
                    } else {
                        mStorageTypePrivate.setChecked(false);
                        mStorageTypePublic.setChecked(true);
                        requestedStorageType = StorageType.TYPE_PRIVATE;
                    }
                    // Then ask listener to handle it.  They may make call to setStorageType()
                    // either while this call is active, or after it returns.  Regardless, it
                    // will not result in further calls to this handler.
                    mFilePresenter.onRequestStorageTypeSelect(requestedStorageType);
                }
            }
        });
        return view;
    }

    /**
     * Calls to the setStorageType do not cause a propagation to the
     * OnRequestCheckedChangeListener.
     */
    @Override
    public void setStorageType(StorageType storageType) {
        mAllowOnRequestCheckedChangeCallback = false;
        if (storageType == StorageType.TYPE_PUBLIC) {
            mStorageTypePublic.setChecked(true);
        } else {
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
        mStorageTypeGroup.setEnabled(enabled);
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
    public void setDescription(String text) {
        mTextDescription.setText(text);
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

