package com.wolfie.eskey.view.component.Settings;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioGroup;

import com.wolfie.eskey.R;

import butterknife.BindView;

/**
 * Created by david on 24/10/16.
 */

public class ItemTimeout extends ItemLayout implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.timeout_radio_group)
    RadioGroup mTimeoutGroup;

    private OnTimeoutSelectedListener mListener;

    public ItemTimeout(Context context) {
        super(context);
    }

    public ItemTimeout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemTimeout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ItemTimeout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTimeoutGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public String getHeadingText() {
        return "Session Timeout";
    }

    public void setTimeout(int timeoutInMillis) {
        int viewId = -1;        // Clears all selections.
        switch (timeoutInMillis) {
            case 30 * 1000:
                viewId = R.id.timeout_30_secs;
                break;
            case 1 * 60 * 1000:
                viewId = R.id.timeout_1_min;
                break;
            case 2 * 60 * 1000:
                viewId = R.id.timeout_2_min;
                break;
            case 10 * 60 * 1000:
                viewId = R.id.timeout_10_min;
                break;
        }
        mTimeoutGroup.check(viewId);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int timeoutInMillis = -1;
        switch (checkedId) {
            case R.id.timeout_30_secs:
                timeoutInMillis = 30 * 1000;
                break;
            case R.id.timeout_1_min:
                timeoutInMillis = 1 * 60 * 1000;
                break;
            case R.id.timeout_2_min:
                timeoutInMillis = 2 * 60 * 1000;
                break;
            case R.id.timeout_10_min:
                timeoutInMillis = 10 * 60 * 1000;
                break;
        }
        if (mListener != null) {
            mListener.onTimeoutChanged(timeoutInMillis);
        }
    }

    public void setOnTimeoutSelectedListener(final OnTimeoutSelectedListener listener) {
        mListener = listener;
    }

    public interface OnTimeoutSelectedListener {
        /**
         * DrawId may be -1 if selection is cleared.
         */
        void onTimeoutChanged(int timeoutInMillis);
    }

}
