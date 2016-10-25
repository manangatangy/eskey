package com.wolfie.eskey.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.wolfie.eskey.presenter.Presenter;
import com.wolfie.eskey.view.fragment.BaseFragment;

/**
 * This interface is implemented by fragments; presenters for activities that have no use
 * for a ui, implement
 */
public interface BaseUi {

    Context getContext();

    /**
     * Useful for inter-presenter communication.
     */
    @Nullable
    <F extends BaseFragment, P extends Presenter> P findPresenter(Class<F> fragClass);

    FragmentActivity getActivity();

    void showBanner(String message);
}
