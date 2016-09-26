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
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.wolfie.eskey.R;
import com.wolfie.eskey.util.KeyboardVisibilityObserver;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by david on 20/09/16.
 * Simple full screen fragment that observes keyboard appearance, and then resizes to
 * occupy the remaining space above the keyboard.  The client layout will therefore
 * always be fully visible.
 */

/**
 * Base class for Action sheet behaviour, which animates the open and close, calling onClose
 * and onOpen (which may be overridden) when the action is complete.  Subclass may override
 * the onBackgroundClick method, if they wish to for example close the action sheet.
 *
 * TODO deal with keyboard taking up space
 * ref http://stackoverflow.com/questions/2150078/how-to-check-visibility-of-software-keyboard-in-android
 * in particular http://stackoverflow.com/a/26152562
 *
 * how to close keyboard
 * ref http://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard?rq=1
 *
 */
public class ResizingFragment extends Fragment {

    @Nullable
    @Bind(R.id.resizing_background_view)
    View mResizingBackgroundView;               // This is GONE/VISIBLE

    @Nullable
    @Bind(R.id.resizing_padding_view)
    View mResizingPaddingView;                  // This has variable bottom padding

    @Nullable
    @Bind(R.id.resizing_animating_view)
    RelativeLayout mResizingAnimatingView;      // This animates open/close

    @Nullable
    @Bind(R.id.resizing_holder_view)
    ScrollView mResizingHolderView;             // This holds the content

    private KeyboardVisibilityObserver mKeyboardVisibilityObserver;
    protected Context mContext;
    private ResizingListener mResizingListener;

    public void setContext(Context context) {
        mContext = context;
    }

    public void setActionSheetListener(ResizingListener resizingListener) {
        mResizingListener = resizingListener;
    }

    /**
     * Subclass to overide this and inflate their layout into the action_sheet_holder_view
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resizing, container, false);
        // Must bind only the ResizingFrag else the other subclass members cause a problem?
        ButterKnife.bind(ResizingFragment.this, view);
        return view;
    }

    @OnClick(R.id.resizing_background_view)
    public void backGroundViewClicked() {
        if (isShowing()) {
            if (mResizingListener != null) {
                mResizingListener.onBackgroundClick();
            }
        }
    }

    protected boolean isShowing() {
        return mResizingBackgroundView != null && mResizingBackgroundView.getVisibility() == View.VISIBLE;
    }

    public void hide() {
        if (isShowing()) {
            Animation bottomDown = AnimationUtils.loadAnimation(mContext, R.anim.action_sheet_down);
            bottomDown.setAnimationListener(new SimpleListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    mResizingBackgroundView.setVisibility(View.GONE);
                    mKeyboardVisibilityObserver = null;     // Stop observing the keyboard.
                    invokeShowHideHandler();                // Callback on ui thread.
                }
            });
            mResizingAnimatingView.startAnimation(bottomDown);
        }
    }

    public void show() {
        if (!isShowing()) {
            Animation bottomUp = AnimationUtils.loadAnimation(mContext, R.anim.action_sheet_up);
            bottomUp.setAnimationListener(new SimpleListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    invokeShowHideHandler();            // Callback on ui thread.
                }
            });
            mResizingAnimatingView.startAnimation(bottomUp);
            mResizingBackgroundView.setVisibility(View.VISIBLE);
            invokeShowHideHandler();                // Callback on ui thread.

            mKeyboardVisibilityObserver = new KeyboardVisibilityObserver(mResizingPaddingView,
                    new KeyboardVisibilityObserver.KeyboardVisibilityListener() {
                        @Override
                        public void onShow(int keyboardHeight) {
                            mResizingPaddingView.setPadding(0, 0, 0, keyboardHeight);
                        }
                        @Override
                        public void onHide() {
                            mResizingPaddingView.setPadding(0, 0, 0, 0);
                        }
                    });
        }
    }

    private void invokeShowHideHandler() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mResizingListener != null) {
                    if (isShowing()) {
                        mResizingListener.onShow();
                    } else {
                        mResizingListener.onHide();
                    }
                }
            }
        });
    }

    public static class ResizingListener {
        public void onShow() {
        }
        public void onHide() {
        }
        public void onBackgroundClick() {
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
