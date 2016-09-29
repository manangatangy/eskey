package com.wolfie.eskey;

import android.os.Bundle;

import com.wolfie.eskey.view.activity.SimpleActivity;

public class SampleActivity extends SimpleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setupToolbar();
//        setupTitle("");
//        setupHomeUp();
//        setupBackArrowColour();
//        setupUpIndicator();
        setupFragment(SampleFragment.class.getName(), R.id.fragment_container_activity_simple, null);
    }
}
