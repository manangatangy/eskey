package com.wolfie.eskey.view.component;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wolfie.eskey.R;

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
    public String getHeadingText() {
        return "Change Password";
    }

    @Override
    public boolean onClickHide() {
        return true;        // Default allow hide to proceed.
    }

}
