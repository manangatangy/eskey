package com.wolfie.eskey.view.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import com.wolfie.eskey.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by david on 18/09/16.
 */

public class HeadingViewHolder extends BaseViewHolder {

    @BindView(R.id.heading_text_view)
    TextView mTextView;

    public HeadingViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
    public void bind(Object item) {
        String text = (String)item;
        mTextView.setText(text);
    }
}

