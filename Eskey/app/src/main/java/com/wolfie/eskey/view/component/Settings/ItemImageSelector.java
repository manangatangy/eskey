package com.wolfie.eskey.view.component.Settings;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RadioGroup;

import com.wolfie.eskey.R;
import com.wolfie.eskey.view.adapter.ImagePagerAdapter;

import butterknife.BindView;

public class ItemImageSelector extends ItemLayout {

    @BindView(R.id.background_radio_group)
    RadioGroup mBackgroundGroup;

    @BindView(R.id.setting_item_viewpager)
    ViewPager mViewpager;

    private int mEnumIndex;

    private OnImageSelectedListener mListener;

    public ItemImageSelector(Context context) {
        super(context);
    }

    public ItemImageSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemImageSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ItemImageSelector(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void show() {
        Log.d("eskey", "ItemBackgroundPic.show(" + getHeadingText() + ")");
        super.show();
        mViewpager.setAdapter(new ImagePagerAdapter(getContext()));
        mViewpager.setCurrentItem(mEnumIndex);
    }

    @Override
    public void hide() {
        Log.d("eskey", "ItemBackgroundPic.hide(" + getHeadingText() + ")");
        super.hide();
        if (mListener != null) {
            mListener.onImageSelected(getCurrentItem());
        }
    }

    public int getCurrentItem() {
        int enumIndex = mViewpager.getCurrentItem();
        return enumIndex;
    }

    public void setCurrentItem(int enumIndex) {
        // Store item value for after the pager is visible, since this method is
        // called by presenter.show() and the actual items are all hidden.
        mEnumIndex = enumIndex;
    }

    public void setOnImageSelectedListener(final OnImageSelectedListener listener) {
        mListener = listener;
    }

    public interface OnImageSelectedListener {
        void onImageSelected(int enumIndex);
    }

    @Override
    public String getHeadingText() {
        return "Background Picture";
    }
}
