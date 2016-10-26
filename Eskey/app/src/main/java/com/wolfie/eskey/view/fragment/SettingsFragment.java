package com.wolfie.eskey.view.fragment;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wolfie.eskey.R;
import com.wolfie.eskey.presenter.SettingsPresenter.SettingsUi;
import com.wolfie.eskey.presenter.SettingsPresenter;
import com.wolfie.eskey.view.activity.EskeyActivity;
import com.wolfie.eskey.view.component.SettingItemBackgroundPic;
import com.wolfie.eskey.view.component.SettingItemChangePassword;
import com.wolfie.eskey.view.component.SettingItemTimeout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettingsFragment extends ActionSheetFragment implements SettingsUi,
        SettingItemTimeout.OnTimeoutSelectedListener,
        SettingItemChangePassword.OnChangePasswordListener,
        SettingItemBackgroundPic.OnBackgroundPicSelectedListener
{

    @Nullable
    @BindView(R.id.setting_item_timeout)
    SettingItemTimeout mSettingItemTimeout;

    @Nullable
    @BindView(R.id.setting_item_change_password)
    SettingItemChangePassword mSettingItemChangePassword;

    @Nullable
    @BindView(R.id.setting_item_background)
    SettingItemBackgroundPic mSettingItemBackgroundPic;

    @Nullable
    @BindView(R.id.button_close)
    Button mButtonClose;

    private Unbinder mUnbinder2;

    private SettingsPresenter mSettingsPresenter;

    @Nullable
    @Override
    public SettingsPresenter getPresenter() {
        return mSettingsPresenter;
    }

    public SettingsFragment() {
        mSettingsPresenter = new SettingsPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        View content = inflater.inflate(R.layout.fragment_settings, container, false);
        mHolderView.addView(content);
        // This bind will re-bind the superclass members, so the entire view hierarchy must be
        // available, hence the content should be added to the parent view first.
        mUnbinder2 = ButterKnife.bind(this, view);
        mButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSettingsPresenter.onClickClose();
            }
        });
        mSettingItemTimeout.setOnTimeoutSelectedListener(this);
        mSettingItemChangePassword.setOnChangePasswordListener(this);
        mSettingItemBackgroundPic.setOnBackgroundPicSelectedListener(this);
        return view;
    }

    @Override
    public boolean onHideAll() {
        boolean onHide1 = mSettingItemTimeout.onHide();
        boolean onHide2 = mSettingItemChangePassword.onHide();
        boolean onHide3 = mSettingItemBackgroundPic.onHide();
        return (onHide1 && onHide2 && onHide3);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder2.unbind();
    }

    @Override
    public void setTimeout(int timeoutInMillis) {
        mSettingItemTimeout.setTimeout(timeoutInMillis);
    }

    @Override
    public void onTimeoutChanged(int timeoutInMillis) {
        mSettingsPresenter.onTimeoutChanged(timeoutInMillis);
    }
    @Override
    public void onChangePassword(String password, String confirm) {
        mSettingsPresenter.onChangePassword(password, confirm);
    }

    @Override
    public void clearPasswordsAndHidePasswordsSetting() {
        mSettingItemChangePassword.clearPasswordFields();
        mSettingItemChangePassword.onHide();
    }

    @Override
    public void setPasswordError(@StringRes int resId) {
        mSettingItemChangePassword.setErrorMessage(resId);
    }

    @Override
    public void onBackgroundPicChanged(@DrawableRes int drawId) {
        mSettingsPresenter.onBackgroundPicChanged(drawId);
    }

    @Override
    public void setBackgroundImage(@DrawableRes int resourceId) {
        mSettingItemBackgroundPic.setBackgroundPic(resourceId);
    }

    @Override
    public void setActivityBackgroundImage(@DrawableRes int resourceId) {
        FragmentActivity activity = getActivity();
        if (activity instanceof EskeyActivity) {
            ((EskeyActivity)activity).setBackgroundImage(resourceId);
        }
    }

    @Override
    public void onShowComplete() {
    }

    @Override
    public void onHideComplete() {
    }

    @Override
    public void onTouchBackground() {
    }

}
