package com.wolfie.eskey.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wolfie.eskey.R;
import com.wolfie.eskey.presenter.SettingsPresenter.SettingsUi;
import com.wolfie.eskey.presenter.SettingsPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettingsFragment extends ActionSheetFragment implements SettingsUi {

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
        return view;
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
