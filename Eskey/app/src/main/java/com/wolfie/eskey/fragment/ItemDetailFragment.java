package com.wolfie.eskey.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wolfie.eskey.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by david on 20/09/16.
 */

public class ItemDetailFragment extends ActionSheetFragment {

    @Nullable
    @Bind(R.id.item_detail_save)
    Button mButtonSave;

    @Nullable
    @Bind(R.id.item_detail_cancel)
    Button mButtonCancel;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View content = inflater.inflate(R.layout.fragment_item_detail, container, false);
        mActionSheetHolderView.addView(content);
        // This bind will re-bind the superclass members, so the entire view hierarchy must be
        // available, hence the content should be added to the parent view first.
        ButterKnife.bind(this, view);

        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemDetailFragment.this.close();
            }
        });
        return view;
    }

}
