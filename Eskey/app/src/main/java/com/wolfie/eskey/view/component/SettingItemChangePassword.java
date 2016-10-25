package com.wolfie.eskey.view.component;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wolfie.eskey.R;
import com.wolfie.eskey.util.StringUtils;

import butterknife.BindView;

/**
 * Created by david on 24/10/16.
 */

public class SettingItemChangePassword extends SettingItemLayout {

    @Nullable
    @BindView(R.id.edit_text_password)
    EditText mEditPassword;

    @Nullable
    @BindView(R.id.edit_text_confirm)
    EditText mEditConfirm;

    @Nullable
    @BindView(R.id.text_error)
    TextView mTextError;

    @Nullable
    @BindView(R.id.button_change_password)
    Button mButtonChangePassword;

    @Nullable
    @BindView(R.id.button_cancel)
    Button mButtonCancel;

    private OnChangePasswordListener mListener;

    public SettingItemChangePassword(Context context) {
        super(context);
    }

    public SettingItemChangePassword(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SettingItemChangePassword(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SettingItemChangePassword(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mButtonCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditPassword.setText("");
                mEditConfirm.setText("");
                onHide();
            }
        });
        mButtonChangePassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    String password = mEditPassword.getText().toString();
                    String confirm = mEditConfirm.getText().toString();
                    mListener.onChangePassword(password, confirm);
                }
            }
        });
    }

    @Override
    public String getHeadingText() {
        return "Change Password";
    }

    @Override
    public void show() {
        clearPasswordFields();
        super.show();
    }

    public void clearPasswordFields() {
        mEditPassword.setText("");
        mEditConfirm.setText("");
        mTextError.setText("");
    }

    @Override
    public boolean onHide() {
        // Only allow hide to proceed if there is no text in the input fields.
        String password = mEditPassword.getText().toString();
        String confirm = mEditConfirm.getText().toString();
        boolean hasText = StringUtils.isNotBlank(password) || StringUtils.isNotBlank(confirm);
        if (hasText) {
            mTextError.setText(R.string.st028);
            return false;
        } else {
            return super.onHide();
        }
    }

    public void setErrorMessage(@StringRes int resId) {
        mTextError.setText(resId);
    }
    public void setOnChangePasswordListener(final OnChangePasswordListener listener) {
        mListener = listener;
    }

    public interface OnChangePasswordListener {
        void onChangePassword(String password, String confirm);
    }
}
