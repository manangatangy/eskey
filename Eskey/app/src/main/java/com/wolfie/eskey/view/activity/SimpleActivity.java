package com.wolfie.eskey.view.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.wolfie.eskey.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class SimpleActivity extends BaseActivity {

    @BindView(R.id.layout_activity_simple)
    View mActivityRootView;

    // Needed public by child frags
    @BindView(R.id.toolbar)
    public Toolbar mToolbar;

    protected Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        unbinder = ButterKnife.bind(this);
    }

    @LayoutRes
    public int getLayoutResource() {
        return R.layout.activity_simple;
    }

    @Override
    public View getActivityRootView() {
        return mActivityRootView;
    }

    /**
     * Create the named fragment and add its view to the specified container.
     * If not null, the specified Bundle will be given to the fragment as args.
     */
    protected Fragment setupFragment(String fragClassName, @IdRes int containerViewId,
                                     @Nullable Bundle args) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragClassName);
        if (fragment == null) {
            fragment = Fragment.instantiate(this, fragClassName, args);
            getSupportFragmentManager().beginTransaction().add(containerViewId, fragment, fragClassName).commit();
        }
        return fragment;
    }

    /**
     * Remove the specified fragment from the activity state and its container.
     */
    protected void teardownFragment(String fragClassName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(fragClassName);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
    }

//    @Override
//    public void showToolbar(boolean show) {
//        mToolbar.setVisibility(show ? View.VISIBLE : View.GONE);
//    }
//
//    protected void setupToolbar(boolean inhibitElevationAdjust) {
//        mToolbar.setTitleTextAppearance(this, R.style.AppTheme_Text_H1);
//        mToolbar.setSubtitleTextColor(ContextCompat.getColor(this, R.color.black));
//        if (inhibitElevationAdjust) {
//            fragmentContainer.setForeground(null);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                mToolbar.setElevation(0f);
//            }
//        }
//        setSupportActionBar(mToolbar);
//    }

    protected void setupTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void setTitle(int resId) {
        getSupportActionBar().setTitle(resId);
    }

    protected void setupHomeUp(boolean homeAsUp) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(homeAsUp);
    }

//    private void setupBackArrowColour() {
//        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
//        upArrow.setColorFilter(getResources().getColor(R.color.red_on_black), PorterDuff.Mode.SRC_ATOP);
//        getSupportActionBar().setHomeAsUpIndicator(upArrow);
//    }

    protected void setupUpIndicator(int resId) {
        getSupportActionBar().setHomeAsUpIndicator(resId);
    }

}
