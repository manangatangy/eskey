package com.wolfie.eskey.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.wolfie.eskey.R;

/**
 * Created by david on 29/10/16.
 */

public enum ImageEnum {

    RED(R.string.st001, R.drawable.arkhangelsk_1),
    BLUE(R.string.st002, R.drawable.st_basils_cathedral_1),
    ORANGE(R.string.st003, R.drawable.church_of_saviour_on_spilt_blood_1);

    private @StringRes int mTitleResId;
//    private int mLayoutResId;
    private @DrawableRes int mImageResId;


    ImageEnum(int titleResId, int imageResId) {
        mTitleResId = titleResId;
        mImageResId = imageResId;
    }

    public @StringRes int getTitleResId() {
        return mTitleResId;
    }

    public @DrawableRes int getImageResId() {
        return mImageResId;
    }

}
