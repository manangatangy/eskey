package com.wolfie.eskey.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.wolfie.eskey.R;

/**
 * Created by david on 29/10/16.
 */

public enum ImageEnum {

    IMAGE01(R.string.im001, R.drawable.st_basils_cathedral_1),
    IMAGE02(R.string.im002, R.drawable.st_basils_cathedral_2),
    IMAGE03(R.string.im003, R.drawable.tall_trees),
    IMAGE04(R.string.im004, R.drawable.arkhangelsk_1),
    IMAGE05(R.string.im005, R.drawable.church_of_saviour_on_spilt_blood_1),
    IMAGE06(R.string.im006, R.drawable.church_of_saviour_on_spilt_blood_2),
    IMAGE07(R.string.im007, R.drawable.moscow_immaculate_conception),
    IMAGE08(R.string.im008, R.drawable.motherland_calls),
    IMAGE09(R.string.im009, R.drawable.peter_paul_fortress_spire),
    IMAGE10(R.string.im010, R.drawable.rostov_citadel),
    IMAGE11(R.string.im011, R.drawable.sevastopol_memorial_1),
    IMAGE12(R.string.im012, R.drawable.sevastopol_memorial_2),
    IMAGE13(R.string.im013, R.drawable.valaam_chapel),
    IMAGE14(R.string.im014, R.drawable.valaam_icon),
    IMAGE15(R.string.im015, R.drawable.valaam_monastery_1),
    IMAGE16(R.string.im016, R.drawable.valaam_monastery_2);

    private @StringRes int mTitleResId;
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
