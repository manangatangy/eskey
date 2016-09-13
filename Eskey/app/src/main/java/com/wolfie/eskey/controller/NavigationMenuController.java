package com.wolfie.eskey.controller;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.wolfie.eskey.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by david on 13/09/16.
 */

public class NavigationMenuController implements RadioGroup.OnCheckedChangeListener {

    public static final String ALL_GROUPS_NAV_HEADING = "All groups";

    @Bind(R.id.navigation_radio_group)
    RadioGroup mRadioGroup;

    public void setOnNavItemSelectedListener(OnNavItemSelectedListener mOnNavItemSelectedListener) {
        this.mOnNavItemSelectedListener = mOnNavItemSelectedListener;
    }

    private OnNavItemSelectedListener mOnNavItemSelectedListener;

    public NavigationMenuController(NavigationView navigationView) {
        ButterKnife.bind(this, navigationView);
        mRadioGroup.setOnCheckedChangeListener(this);
    }

    public void setItemsTexts(List<String> itemTexts) {
        mRadioGroup.removeAllViews();
        addItem(ALL_GROUPS_NAV_HEADING).setChecked(true);
        for (String itemText : itemTexts) {
            addItem(itemText);
        }
    }

    private RadioButton addItem(String itemText) {
        View view = LayoutInflater.from(mRadioGroup.getContext()).inflate(R.layout.navigation_item, null, false);
        RadioButton radioButton = (RadioButton)view;
        radioButton.setText(itemText);
        ViewGroup.LayoutParams layoutParams =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mRadioGroup.addView(radioButton, layoutParams);
        return radioButton;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton radioButton = (RadioButton)mRadioGroup.findViewById(checkedId);
        String itemText = radioButton.getText().toString();
        if (mOnNavItemSelectedListener != null) {
            mOnNavItemSelectedListener.onNavItemSelected(itemText);
        }
    }

    public interface OnNavItemSelectedListener {
        void onNavItemSelected(String itemText);
    }

}
