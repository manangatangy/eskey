package com.wolfie.eskey.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wolfie.eskey.R;
import com.wolfie.eskey.model.Entry;
import com.wolfie.eskey.util.KeyboardUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by david on 20/09/16.
 */

public class ItemDetailFragment extends ResizingFragment {

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
    private ItemEditListener mItemEditListener;

    public void setItemEditListener(ItemEditListener itemEditListener) {
        setActionSheetListener(itemEditListener);
        mItemEditListener = itemEditListener;
    }

    public void setEntry(Entry entry) {
        mEntry = entry;
        isCreate = (mEntry == null);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        View content = inflater.inflate(R.layout.fragment_item_detail, container, false);
        mResizingHolderView.addView(content);
        // This bind will re-bind the superclass members, so the entire view hierarchy must be
        // available, hence the content should be added to the parent view first.
        ButterKnife.bind(this, view);

        mViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.dismissKeyboard(view);
                // TODO - check for changes and alert before closing, losing changes
                ItemDetailFragment.this.hide();
            }
        });
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.dismissKeyboard(view);
                ItemDetailFragment.this.onClickSave();
            }
        });
        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.dismissKeyboard(view);
                ItemDetailFragment.this.onClickDelete();
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

    private void onClickSave() {
        // Place the view component values into the entry, check for emptiness and call the listener.
        String name = mEditName.getText().toString();
        String group = mEditGroup.getText().toString();
        String content = mEditContent.getText().toString();
        // TODO - better validation of these fields.  Probably should show inline error messages
        // instead of toasts.
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(mContext, "Name can't be empty", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(group)) {
            Toast.makeText(mContext, "Group can't be empty", Toast.LENGTH_SHORT).show();
        } else  if (TextUtils.isEmpty(content)) {
            Toast.makeText(mContext, "Content can't be empty", Toast.LENGTH_SHORT).show();
        } else {
            if (isCreate) {
                mEntry = Entry.create(name, group, content);
            } else {
                mEntry.setEntryName(name);
                mEntry.setGroupName(group);
                mEntry.setContent(content);
            }
            if (mItemEditListener != null) {
                mItemEditListener.onSave(mEntry, isCreate);
            }
        }
    }

    private void onClickDelete() {
        if (mItemEditListener != null) {
            mItemEditListener.onDelete(mEntry);
        }
    }

    public static class ItemEditListener extends ResizingListener {
        public void onSave(Entry entry, boolean isCreate) {
        }
        public void onDelete(Entry entry) {
        }
    }

}
