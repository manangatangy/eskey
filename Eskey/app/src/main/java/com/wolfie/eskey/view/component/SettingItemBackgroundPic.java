package com.wolfie.eskey.view.component;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.RadioGroup;

import com.wolfie.eskey.R;

import butterknife.BindView;

/**
 * Created by david on 24/10/16.
 */

public class SettingItemBackgroundPic extends SettingItemLayout implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.background_radio_group)
    RadioGroup mBackgroundGroup;

    public SettingItemBackgroundPic(Context context) {
        super(context);
    }

    public SettingItemBackgroundPic(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SettingItemBackgroundPic(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SettingItemBackgroundPic(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public String getHeadingText() {
        return "Background Picture";
    }

    @Override
    public boolean onClickHide() {
        return true;        // Default allow hide to proceed.
    }

    public void setBackgroundPic(@DrawableRes int drawId) {
        int viewId = -1;        // Clears all selections.
        switch (drawId) {
            case R.drawable.st_basils_cathedral_1:
                viewId = R.id.background_1;
                break;
            case R.drawable.st_basils_cathedral_2:
                viewId = R.id.background_2;
                break;
            case R.drawable.tall_trees:
                viewId = R.id.background_3;
                break;
        }
        mBackgroundGroup.check(viewId);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        @DrawableRes int drawId = -1;
        switch (checkedId) {
            case R.id.background_1:
                drawId = R.drawable.st_basils_cathedral_1;
                break;
            case R.id.background_2:
                drawId = R.drawable.st_basils_cathedral_2;
                break;
            case R.id.background_3:
                drawId = R.drawable.tall_trees;
                break;
        }
        if (mListener != null) {
            mListener.onBackgroundPicChanged(drawId);
        }
    }

    private OnBackgroundPicSelectedListener mListener;

    public void onSelection(final OnBackgroundPicSelectedListener listener) {
        mListener = listener;
    }

    public interface OnBackgroundPicSelectedListener {
        /**
         * DrawId may be -1 if selection is cleared.
         */
        void onBackgroundPicChanged(@DrawableRes int drawId);
    }

}
