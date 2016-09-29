package com.wolfie.eskey.view.adapter.viewholder;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.wolfie.eskey.R;
import com.wolfie.eskey.view.adapter.GroupingRecyclerAdapter;
import com.wolfie.eskey.model.Entry;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by david on 18/09/16.
 */

public class ItemViewHolder extends BaseViewHolder {

    private static int ANIM_DURATION = 200;

    // item_layout gives the expanded with, excluding the left hand spacer
    @BindView(R.id.item_layout)
    View mLayoutView;

    // item_detail_frame is the parent of a single child item_detail_view
    // it is width=0/height=0 when "collapsed" and is width=maxFrameWidth/
    // height=maxFrameHeight when "expanded".
    @BindView(R.id.item_detail_frame)
    View mDetailLayoutFrame;

    @BindView(R.id.item_detail_view)
    View mDetailLayoutView;

    @BindView(R.id.item_left_spacer)
    View mDetailLeftSpacerView;

    @BindView(R.id.item_text_view)
    TextView mTitleTextView;

    @BindView(R.id.content_text_view)
    TextView mContentTextView;

    @BindView(R.id.edit_image)
    View mEditView;

    private int mLeftSpacedWidth;
    private Entry mEntry;
    private GroupingRecyclerAdapter.OnItemInListClickedListener mListener;

    public ItemViewHolder(View view, GroupingRecyclerAdapter.OnItemInListClickedListener listener) {
        super(view);
        mListener = listener;
        ButterKnife.bind(this, view);
    }
    public void bind(Object item) {
        mEntry = (Entry)item;
        mTitleTextView.setText(mEntry.getEntryName());
        mContentTextView.setText(mEntry.getContent());
        mEditView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onListItemClick(mEntry);
                }
            }
        });
    }

    public void toggleDetailView() {
        boolean doExpand = (mDetailLayoutFrame.getHeight() == 0);

        // Of the three dimensions that are animated, two have target values that are determined
        // from other (fixed) view dimensions, but the third does not. Therefore we must store
        // the width of the leftSpacer while in the "collapsed" state, for later use during collapse.
        if (doExpand) {
            mLeftSpacedWidth = mDetailLeftSpacerView.getWidth();        // should be 53
        }

        // onExpand mDetailLayoutFrame    animates from 0 --> mLayoutView.getWidth() [974]
        // onExpand mDetailLayoutFrame    animates from 0 --> mDetailLayoutView.getHeight() [184]
        // onExpand mDetailLeftSpacerView animates from mDetailLeftSpacerView.getWidth() [53] --> 0

        ViewWidthParamAnimator frameWidthAnimator = new ViewWidthParamAnimator(mDetailLayoutFrame);
        ViewHeightParamAnimator frameHeightAnimator = new ViewHeightParamAnimator(mDetailLayoutFrame);
        ViewWidthParamAnimator spacerWidthAnimator = new ViewWidthParamAnimator(mDetailLeftSpacerView);
        ValueAnimator anim1 = frameWidthAnimator.build(doExpand ? mLayoutView.getWidth() : 0);
        ValueAnimator anim2 = frameHeightAnimator.build(doExpand ? mDetailLayoutView.getHeight() : 0);
        ValueAnimator anim3 = spacerWidthAnimator.build(doExpand ? 0 : mLeftSpacedWidth);
        anim1.start();
        anim2.start();
        anim3.start();
        AnimatorSet set = new AnimatorSet();
        set.play(anim1).with(anim2).with(anim3);
        set.start();
        // ref https://developer.android.com/guide/topics/graphics/prop-animation.html
    }

    /**
     * Utility for animating view property that requires setting the layout params.
     */
    public static abstract class ViewParamAnimator {

        protected View mView;

        abstract void onViewParamUpdate(ViewGroup.LayoutParams layoutParams, int val);

        public ViewParamAnimator(View view) {
            mView = view;
        }

        public ValueAnimator build(int initDimension, int finalDimension) {
            ValueAnimator anim = ValueAnimator.ofInt(initDimension, finalDimension);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
                    onViewParamUpdate(layoutParams, val);
                    mView.setLayoutParams(layoutParams);
                }
            });
            anim.setDuration(ANIM_DURATION);
            return anim;
        }
    }

    public static class ViewWidthParamAnimator extends ViewParamAnimator {
        public ViewWidthParamAnimator(View view) {
            super(view);
        }
        @Override
        void onViewParamUpdate(ViewGroup.LayoutParams layoutParams, int val) {
            layoutParams.width = val;
        }
        public ValueAnimator build(int finalDimension) {
            return build(mView.getMeasuredWidth(), finalDimension);
        }
    }

    public static class ViewHeightParamAnimator extends ViewParamAnimator {
        public ViewHeightParamAnimator(View view) {
            super(view);
        }
        @Override
        void onViewParamUpdate(ViewGroup.LayoutParams layoutParams, int val) {
            layoutParams.height = val;
        }
        public ValueAnimator build(int finalDimension) {
            return build(mView.getMeasuredHeight(), finalDimension);
        }
    }
}
