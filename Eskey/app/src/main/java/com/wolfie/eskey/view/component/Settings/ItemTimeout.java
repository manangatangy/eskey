package com.wolfie.eskey.view.component.Settings;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RadioGroup;

import com.wolfie.eskey.R;

import butterknife.BindView;

/**
 * Created by david on 24/10/16.
 */

public class ItemTimeout extends ItemLayout implements RadioGroup.OnCheckedChangeListener {

    public static final int TIMEOUT_NOT_A_VALUE = -1;

    @BindView(R.id.timeout_radio_group)
    RadioGroup mTimeoutGroup;

    private int mTimeoutInMillis = TIMEOUT_NOT_A_VALUE;

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
        switch (mTimeoutInMillis = timeoutInMillis) {
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
        switch (checkedId) {
            case R.id.timeout_30_secs:
                mTimeoutInMillis = 30 * 1000;
                break;
            case R.id.timeout_1_min:
                mTimeoutInMillis = 1 * 60 * 1000;
                break;
            case R.id.timeout_2_min:
                mTimeoutInMillis = 2 * 60 * 1000;
                break;
            case R.id.timeout_10_min:
                mTimeoutInMillis = 10 * 60 * 1000;
                break;
            default:
                mTimeoutInMillis = TIMEOUT_NOT_A_VALUE;
                break;
        }
        // So when Fragment.restoreViewState() is called, it will end up calling
        // RadioGroup.setCheckedId() which ends up here. The problem is that this
        // happens before Presenter.resume() is called, which means that we can't
        // call mListener.onTimeoutChanged() here because it's not restored right.
        // Instead, just keep the value and callback when the fragment is closed
        // (like the ItemImageSelector does).
    }

    @Override
    public void hide() {
        Log.d("eskey", "ItemTimeout.hide(" + getHeadingText() + ")");
        super.hide();
        if (mListener != null && mTimeoutInMillis != TIMEOUT_NOT_A_VALUE) {
            mListener.onTimeoutChanged(mTimeoutInMillis);
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
