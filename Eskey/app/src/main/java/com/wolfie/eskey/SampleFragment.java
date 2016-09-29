package com.wolfie.eskey;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wolfie.eskey.presenter.Presenter;
import com.wolfie.eskey.view.fragment.BaseFragment;
import com.wolfie.eskey.SamplePresenter.SampleUi;

import butterknife.BindView;

public class SampleFragment extends BaseFragment implements SampleUi {

    @BindView(R.id.textview_hello)
    TextView mTextView;

    private SamplePresenter mPresenter;

    @Nullable
    @Override
    public Presenter getPresenter() {
        return mPresenter;
    }

    public SampleFragment() {
        mPresenter = new SamplePresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextView.setText("Hello Wolfgang");
    }

}
