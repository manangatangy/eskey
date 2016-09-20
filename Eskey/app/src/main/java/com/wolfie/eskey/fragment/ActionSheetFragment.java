package com.wolfie.eskey.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;

import com.wolfie.eskey.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by david on 20/09/16.
 */

/**
 * Base class for Action sheet behaviour, which animates the open and close, calling onClose
 * and onOpen (which may be overridden) when the action is complete.  Subclass may override
 * the onBackgroundClick method, if they wish to for example close the action sheet.
 */
public class ActionSheetFragment extends Fragment {

    @Nullable
    @Bind(R.id.action_sheet_background_view)
    View mActionSheetBackgroundView;            // This is GONE/VISIBLE

    @Nullable
    @Bind(R.id.action_sheet_holder_view)
    FrameLayout mActionSheetHolderView;         // This is animated.

    private Context mContext;
    private ActionSheetListener mActionSheetListener;

    /**
     * Subclass to overide this and inflate their layout into the action_sheet_holder_view
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_action_sheet, container, false);
        // Must bind only the ActionSheetFrag else the other subclass members cause a problem?
        ButterKnife.bind(ActionSheetFragment.this, view);
        return view;
    }

    public void setContext(Context context) {
        mContext = context;
    }
    public void setActionSheetListener(ActionSheetListener actionSheetListener) {
        mActionSheetListener = actionSheetListener;
    }

    @OnClick(R.id.action_sheet_background_view)
    public void backGroundViewClicked() {
        if (isOpen()) {
            if (mActionSheetListener != null) {
                mActionSheetListener.onActionSheetBackgroundClick();
            }
        }
    }

    public boolean isOpen() {
        return mActionSheetBackgroundView != null && mActionSheetBackgroundView.getVisibility() == View.VISIBLE;
    }

    public void close() {
        if (isOpen()) {
            Animation bottomDown = AnimationUtils.loadAnimation(mContext, R.anim.action_sheet_down);
            bottomDown.setAnimationListener(new SimpleListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    mActionSheetBackgroundView.setVisibility(View.GONE);
                    invokeHandler();            // Callback on ui thread.
                }
            });
            mActionSheetHolderView.startAnimation(bottomDown);
        }
    }

    public void open() {
        if (!isOpen()) {
            Animation bottomUp = AnimationUtils.loadAnimation(mContext, R.anim.action_sheet_up);
            bottomUp.setAnimationListener(new SimpleListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    invokeHandler();            // Callback on ui thread.
                }
            });
            mActionSheetHolderView.startAnimation(bottomUp);
            mActionSheetBackgroundView.setVisibility(View.VISIBLE);
        }
    }

    private void invokeHandler() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mActionSheetListener != null) {
                    if (isOpen()) {
                        mActionSheetListener.onOpenActionSheet();
                    } else {
                        mActionSheetListener.onCloseActionSheet();
                    }
                }
            }
        });
    }

    public static class ActionSheetListener {
        public void onOpenActionSheet() {
        }
        public void onCloseActionSheet() {
        }
        public void onActionSheetBackgroundClick() {
        }
    }

    public static class SimpleListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }
        @Override
        public void onAnimationEnd(Animation animation) {
        }
        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }
}
