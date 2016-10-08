package com.wolfie.eskey.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.wolfie.eskey.presenter.Presenter;
import com.wolfie.eskey.view.BaseUi;
import com.wolfie.eskey.view.activity.BaseActivity;
import com.wolfie.eskey.view.activity.BaseActivity.KeyboardVisibility;
import com.wolfie.eskey.view.activity.DrawerActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment implements BaseUi {

    protected BaseActivity mBaseActivity;
    protected Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Normally the inject call would go here
    }

    /**
     * This method will bind the views, unless the Unbinder has already been used.
     * This means that subclasses may perform their binding in their implementation
     * of onCreateView, after they have inflated the view.  This could be necessary
     * for subclasses that can't wait until onViewCreated to do the binding.
     * Note that unbinding is still performed here (in onDestroyView) regardless
     * of where the bind is called.
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (unbinder == null) {
            unbinder = ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBaseActivity = (BaseActivity)getActivity();
        if (getPresenter() != null) {
            getPresenter().onCreate(savedInstanceState);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            if (getPresenter() != null) {
                getPresenter().onRestoreState(savedInstanceState);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getPresenter() != null) {
            getPresenter().resume();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (getPresenter() != null) {
            getPresenter().onSaveState(outState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getPresenter() != null) {
            getPresenter().pause();
        }
//        hideBannerMessage();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        if (getPresenter() != null) {
            getPresenter().onDestroy();
        }
        super.onDestroy();
    }

    @Nullable
    public abstract Presenter getPresenter();

    /**
     * Will return the Presenter corresponding to the specified fragment, or null if
     * the fragment hasn't yet been created.  As an assistance, specifying null for
     * the fragment class will return the MainPresenter for the DrawerActivity (or
     * null if the activity is the wrong type).
     */
    @Nullable
    @Override
    public <F extends BaseFragment, P extends Presenter> P findPresenter(Class<F> fragClass) {
        if (fragClass != null) {
            return mBaseActivity.findPresenter(fragClass);
        }
        if (mBaseActivity instanceof DrawerActivity) {
            // noinspection unchecked
            return (P)((DrawerActivity)mBaseActivity).getPresenter();
        }
        return null;
    }

    /**
     * Should return true, to let the framework handle the back press.
     * If handled here, then return false.
     */
    public boolean onBackPressed() {
        if (getPresenter() != null) {
            return getPresenter().backPressed();
        }
        return false;
    }

    /**
     * Should return true, to let the framework handle the back press.
     * If handled here, then return false.
     */
    public boolean onHomeAsUpPressed() {
        if (getPresenter() != null) {
            return getPresenter().homeAsUpPressed();
        }
        return true;
    }

    public KeyboardVisibility getKeyboardVisibility() {
        return (mBaseActivity != null) ? mBaseActivity.getKeyboardVisibility() : KeyboardVisibility.UNKNOWN;
    }

    public boolean isKeyboardVisible() {
        return getKeyboardVisibility() == KeyboardVisibility.SHOWING;
    }

    /**
     * A listener for keyboard visibility change events.
     */
    public void onKeyboardVisibilityChanged(KeyboardVisibility keyboardVisibility) {
        if (getPresenter() != null) {
            getPresenter().onKeyboardVisibilityChanged(keyboardVisibility);
        }
    }

}
