package com.wolfie.eskey.view.component;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.RadioGroup;

import com.wolfie.eskey.R;
import com.wolfie.eskey.view.adapter.ImagePagerAdapter;

import butterknife.BindView;

/**
 * Created by david on 24/10/16.
 */

public class SettingItemBackgroundPic extends SettingItemLayout implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.background_radio_group)
    RadioGroup mBackgroundGroup;

    @BindView(R.id.setting_item_viewpager)
    ViewPager mViewpager;

    private OnBackgroundPicSelectedListener mListener;

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
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBackgroundGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public boolean onShow() {
        mViewpager.setAdapter(new ImagePagerAdapter(getContext()));
        return super.onShow();
    }

    @Override
    public String getHeadingText() {
        return "Background Picture";
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
            case R.drawable.arkhangelsk_1:
                viewId = R.id.background_4;
                break;
            case R.drawable.church_of_saviour_on_spilt_blood_1:
                viewId = R.id.background_5;
                break;
            case R.drawable.church_of_saviour_on_spilt_blood_2:
                viewId = R.id.background_6;
                break;
            case R.drawable.moscow_immaculate_conception:
                viewId = R.id.background_7;
                break;
            case R.drawable.motherland_calls:
                viewId = R.id.background_8;
                break;
            case R.drawable.peter_paul_fortress_spire:
                viewId = R.id.background_9;
                break;
            case R.drawable.rostov_citadel:
                viewId = R.id.background_10;
                break;
            case R.drawable.sevastopol_memorial_1:
                viewId = R.id.background_11;
                break;
            case R.drawable.sevastopol_memorial_2:
                viewId = R.id.background_12;
                break;
            case R.drawable.valaam_chapel:
                viewId = R.id.background_13;
                break;
            case R.drawable.valaam_icon:
                viewId = R.id.background_14;
                break;
            case R.drawable.valaam_monastery_1:
                viewId = R.id.background_15;
                break;
            case R.drawable.valaam_monastery_2:
                viewId = R.id.background_16;
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
            case R.id.background_4:
                drawId = R.drawable.arkhangelsk_1;
                break;
            case R.id.background_5:
                drawId = R.drawable.church_of_saviour_on_spilt_blood_1;
                break;
            case R.id.background_6:
                drawId = R.drawable.church_of_saviour_on_spilt_blood_2;
                break;
            case R.id.background_7:
                drawId = R.drawable.moscow_immaculate_conception;
                break;
            case R.id.background_8:
                drawId = R.drawable.motherland_calls;
                break;
            case R.id.background_9:
                drawId = R.drawable.peter_paul_fortress_spire;
                break;
            case R.id.background_10:
                drawId = R.drawable.rostov_citadel;
                break;
            case R.id.background_11:
                drawId = R.drawable.sevastopol_memorial_1;
                break;
            case R.id.background_12:
                drawId = R.drawable.sevastopol_memorial_2;
                break;
            case R.id.background_13:
                drawId = R.drawable.valaam_chapel;
                break;
            case R.id.background_14:
                drawId = R.drawable.valaam_icon;
                break;
            case R.id.background_15:
                drawId = R.drawable.valaam_monastery_1;
                break;
            case R.id.background_16:
                drawId = R.drawable.valaam_monastery_2;
                break;

        }
        if (mListener != null) {
            mListener.onBackgroundPicChanged(drawId);
        }
    }

    public void setOnBackgroundPicSelectedListener(final OnBackgroundPicSelectedListener listener) {
        mListener = listener;
    }

    public interface OnBackgroundPicSelectedListener {
        /**
         * DrawId may be -1 if selection is cleared.
         */
        void onBackgroundPicChanged(@DrawableRes int drawId);
    }

}
