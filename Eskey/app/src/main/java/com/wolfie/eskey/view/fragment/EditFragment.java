package com.wolfie.eskey.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wolfie.eskey.R;
import com.wolfie.eskey.model.Entry;
import com.wolfie.eskey.presenter.EditPresenter;
import com.wolfie.eskey.presenter.EditPresenter.EditUi;
import com.wolfie.eskey.util.KeyboardUtils;
import com.wolfie.eskey.view.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by david on 30/09/16.
 */

public class EditFragment extends ActionSheetFragment implements EditUi {

    @Nullable
    @BindView(R.id.text_title)
    TextView mTextTitle;

    @Nullable
    @BindView(R.id.image_view_close)
    View mViewClose;

    @Nullable
    @BindView(R.id.edit_text_name)
    EditText mEditName;

    @Nullable
    @BindView(R.id.edit_text_group)
    EditText mEditGroup;

    @Nullable
    @BindView(R.id.edit_text_content)
    EditText mEditContent;

    @Nullable
    @BindView(R.id.button_save)
    Button mButtonSave;

    @Nullable
    @BindView(R.id.button_delete)
    Button mButtonDelete;

    private Unbinder mUnbinder2;

    private EditPresenter mEditPresenter;

    @Override
    public EditPresenter getPresenter() {
        return mEditPresenter;
    }

    public EditFragment() {
        mEditPresenter = new EditPresenter(this);
    }

    /**
     *
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        View content = inflater.inflate(R.layout.fragment_edit, container, false);
        mHolderView.addView(content);
        // This bind will re-bind the superclass members, so the entire view hierarchy must be
        // available, hence the content should be added to the parent view first.
        mUnbinder2 = ButterKnife.bind(this, view);
        mViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditPresenter.onClickClose();
            }
        });
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditPresenter.onClickSave();
            }
        });
        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditPresenter.onClickDelete();
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
    public void setTitleText(String title) {
        mTextTitle.setText(title);
    }

    @Override
    public void enableDeleteButton(boolean enable) {
        mButtonDelete.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setTextValues(Entry entry) {
        mEditName.setText(entry.getEntryName());
        mEditGroup.setText(entry.getGroupName());
        mEditContent.setText(entry.getContent());
    }

    @Override
    public Entry getTextValues(Entry entry) {
        // Place the view component values into the entry, check for emptiness and call the listener.
        String name = mEditName.getText().toString();
        String group = mEditGroup.getText().toString();
        String content = mEditContent.getText().toString();
        entry.setEntryName(name);
        entry.setGroupName(group);
        entry.setContent(content);
        return entry;
    }

    @Override
    public void dismissKeyboardAndClose() {
        if (getKeyboardVisibility() == BaseActivity.KeyboardVisibility.SHOWING) {
            KeyboardUtils.dismissKeyboard(getActivity());
        }
        hide();
    }

    @Override
    public void onShowComplete() {
        mEditPresenter.onShow();
    }

    @Override
    public void onHideComplete() {
    }

    @Override
    public void onTouchBackground() {
    }


}
