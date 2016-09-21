package com.wolfie.eskey.fragment;

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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by david on 20/09/16.
 */

public class ItemDetailFragment extends ActionSheetFragment {

    @Nullable
    @Bind(R.id.text_title)
    TextView mTextTitle;

    @Nullable
    @Bind(R.id.image_view_close)
    View mViewClose;

    @Nullable
    @Bind(R.id.edit_text_name)
    EditText mEditName;

    @Nullable
    @Bind(R.id.edit_text_group)
    EditText mEditGroup;

    @Nullable
    @Bind(R.id.edit_text_content)
    EditText mEditContent;

    @Nullable
    @Bind(R.id.button_save)
    Button mButtonSave;

    @Nullable
    @Bind(R.id.button_delete)
    Button mButtonDelete;

    private boolean isCreate;
    private Entry mEntry;

    public void setEntry(Entry entry) {
        mEntry = entry;
        isCreate = (mEntry == null);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View content = inflater.inflate(R.layout.fragment_item_detail, container, false);
        mActionSheetHolderView.addView(content);
        // This bind will re-bind the superclass members, so the entire view hierarchy must be
        // available, hence the content should be added to the parent view first.
        ButterKnife.bind(this, view);

        mViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - check for changes and alert before closing, losing changes
                ItemDetailFragment.this.close();
            }
        });
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemDetailFragment.this.onSave();
            }
        });
        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemDetailFragment.this.onDelete();
            }
        });
        if (isCreate) {
            mTextTitle.setText("Create Entry");
            mButtonDelete.setVisibility(View.GONE);
        } else {
            mTextTitle.setText("Modify Entry");
            mEditName.setText(mEntry.getEntryName());
            mEditGroup.setText(mEntry.getGroupName());
            mEditContent.setText(mEntry.getContent());
        }
        return view;
    }

    private void onSave() {

    }

    private void onDelete() {

    }

}
