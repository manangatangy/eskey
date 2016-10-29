package com.wolfie.eskey.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wolfie.eskey.R;
import com.wolfie.eskey.presenter.LoginPresenter;
import com.wolfie.eskey.presenter.LoginPresenter.LoginUi;
import com.wolfie.eskey.util.KeyboardUtils;
import com.wolfie.eskey.view.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by david on 2/10/16.
 */

public class LoginFragment extends ActionSheetFragment implements LoginUi {

    @Nullable
    @BindView(R.id.text_title)
    TextView mTextTitle;

    @Nullable
    @BindView(R.id.text_description)
    TextView mTextDescription;

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
    @BindView(R.id.button_login)
    Button mButtonLogin;

    @Nullable
    @BindView(R.id.button_init)
    Button mButtonInitialise;

    private Unbinder mUnbinder2;

    private LoginPresenter mLoginPresenter;

    @Override
    public LoginPresenter getPresenter() {
        return mLoginPresenter;
    }

    public LoginFragment() {
        mLoginPresenter = new LoginPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        View content = inflater.inflate(R.layout.fragment_login, container, false);
        mHolderView.addView(content);
        // This bind will re-bind the superclass members, so the entire view hierarchy must be
        // available, hence the content should be added to the parent view first.
        mUnbinder2 = ButterKnife.bind(this, view);
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginPresenter.onClickLogin(getPassword());
            }
        });
        mButtonInitialise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginPresenter.onClickInitialise(getPassword(), getConfirm());
            }
        });
        return view;
    }

    @Override
    public void finish() {
        getActivity().finish();
    }

    @Override
    public void setTitle(@StringRes int resourceId) {
        mTextTitle.setText(resourceId);
    }

    @Override
    public void setDescription(@StringRes int resourceId) {
        mTextDescription.setText(resourceId);
    }

    @Override
    public void setConfirmVisibility(boolean visibility) {
        mEditConfirm.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setButtonsVisibleAndEnabled(boolean firstTime, boolean enabled) {
        mButtonInitialise.setVisibility(firstTime ? View.VISIBLE : View.GONE);
        mButtonLogin.setVisibility(firstTime ? View.GONE : View.VISIBLE);

        mButtonInitialise.setEnabled(enabled);
        mButtonLogin.setEnabled(enabled);
    }

    @Override
    public void setErrorMessage(@StringRes int resourceId) {
        mTextError.setVisibility(View.VISIBLE);
        mTextError.setText(resourceId);
    }

    @Override
    public void clearErrorMessage() {
        mTextError.setVisibility(View.GONE);
    }

    @Override
    public String getPassword() {
        return mEditPassword.getText().toString();
    }

    @Override
    public String getConfirm() {
        return mEditConfirm.getText().toString();
    }

    @Override
    public void clearPasswords() {
        mEditPassword.setText("");
        mEditConfirm.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder2.unbind();
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
