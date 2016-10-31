package com.wolfie.eskey.view.fragment;

import android.os.Bundle;
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
import com.wolfie.eskey.view.component.Settings.GroupSetting;
import com.wolfie.eskey.view.component.Settings.ItemImageSelector;
import com.wolfie.eskey.view.component.Settings.ItemChangePassword;
import com.wolfie.eskey.view.component.Settings.ItemTimeout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettingsFragment extends ActionSheetFragment implements SettingsUi,
        ItemTimeout.OnTimeoutSelectedListener,
        ItemChangePassword.OnChangePasswordListener,
        ItemImageSelector.OnImageSelectedListener {

    @Nullable
    @BindView(R.id.setting_item_timeout)
    ItemTimeout mItemTimeout;

    @Nullable
    @BindView(R.id.setting_item_change_password)
    ItemChangePassword mItemChangePassword;

    @Nullable
    @BindView(R.id.setting_item_background)
    ItemImageSelector mItemImageSelector;

    @Nullable
    @BindView(R.id.button_close)
    Button mButtonClose;

    private Unbinder mUnbinder2;

    private SettingsPresenter mSettingsPresenter;

    private GroupSetting mGroupSetting = new GroupSetting();

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
        mItemTimeout.setOnTimeoutSelectedListener(this);
        mItemChangePassword.setOnChangePasswordListener(this);
        mItemImageSelector.setOnImageSelectedListener(this);

        mItemTimeout.setGroupSetting(mGroupSetting);
        mItemChangePassword.setGroupSetting(mGroupSetting);
        mItemImageSelector.setGroupSetting(mGroupSetting);

        return view;
    }

    @Override
    public void hide() {
        // Can only hide the action sheet if the group agrees
        if (mGroupSetting.requestToHideCurrent()) {
            super.hide();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder2.unbind();
    }

    @Override
    public void setTimeout(int timeoutInMillis) {
        mItemTimeout.setTimeout(timeoutInMillis);
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
        mItemChangePassword.clearPasswordFields();
        mItemChangePassword.onHide();
    }

    @Override
    public void setPasswordError(@StringRes int resId) {
        mItemChangePassword.setErrorMessage(resId);
    }

    @Override
    public void onImageSelected(int enumIndex) {
        // Change the stored pref setting.
        mSettingsPresenter.onImageSelected(enumIndex);
    }

    @Override
    public void setImageItem(int enumIndex) {
        mItemImageSelector.setCurrentItem(enumIndex);
    }

    @Override
    public void setActivityBackgroundImage(int enumIndex) {
        FragmentActivity activity = getActivity();
        if (activity instanceof EskeyActivity) {
            ((EskeyActivity)activity).setBackgroundImage(enumIndex);
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
