package com.wolfie.eskey.view.component.Settings;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.wolfie.eskey.R;

import butterknife.BindView;

public class ItemEmailAddress extends ItemLayout {

    private OnHideEmailSettingListener mListener;

    @Nullable
    @BindView(R.id.edit_text_email_address)
    TextView mTextEmailAddress;

    public ItemEmailAddress(Context context) {
        super(context);
    }

    public ItemEmailAddress(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemEmailAddress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ItemEmailAddress(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public String getHeadingText() {
        return "Email address for backup";
    }

    public void setEmailAddress(String text) {
        mTextEmailAddress.setText(text);
    }

    @Override
    public void hide() {
        super.hide();
        if (mListener != null) {
            mListener.onHideEmailSetting(mTextEmailAddress.getText().toString());
        }
    }

    public void setOnHideEmailSettingListener(final OnHideEmailSettingListener listener) {
        mListener = listener;
    }

    public interface OnHideEmailSettingListener {
        void onHideEmailSetting(String emailAddress);
    }

}
