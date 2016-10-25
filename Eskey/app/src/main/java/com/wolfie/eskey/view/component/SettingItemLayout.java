package com.wolfie.eskey.view.component;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wolfie.eskey.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettingItemLayout extends LinearLayout {

    @BindView(R.id.settings_heading)
    LinearLayout mHeading;

    @BindView(R.id.settings_heading_text)
    TextView mHeadingText;

    @BindView(R.id.settings_heading_icon)
    ImageView mHeadingImageView;

    @BindView(R.id.settings_content)
    LinearLayout mContent;

    protected Unbinder unbinder;

    public SettingItemLayout(Context context) {
        super(context);
        initView();
    }

    public SettingItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SettingItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public SettingItemLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    /**
     * If a Subclass has view members, they should be annotated @BindView and they will be bound
     * after inflation has completed.
     */
    @Override
    @CallSuper
    protected void onFinishInflate() {
        super.onFinishInflate();

        // All the children specified in the layout are re-located to be children of mContent.
        LinearLayout content = (LinearLayout) getChildAt(0).findViewById(R.id.settings_content);
        List<View> children = new ArrayList<View>();
        for (int v = 1, count = getChildCount(); v < count; v++) {
            View child = getChildAt(1);
            removeView(child);
            content.addView(child);
        }

        unbinder = ButterKnife.bind(this);
        mHeadingText.setText(getHeadingText());
        mHeading.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isShowing = (mContent.getVisibility() == VISIBLE);
                if (isShowing) {
                    onHide();
                } else {
                    onShow();
                }
            }
        });
    }

    private void initView() {
        // Add the new inflated layout into this group.
        View view = inflate(getContext(), R.layout.view_settings_item, null);
        addView(view, 0);
    }

    public String getHeadingText() {
        return "some setting";
    }

    public void show() {
        mContent.setVisibility(VISIBLE);
        mHeadingImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.red_delete, null));
    }
    public void hide() {
        mContent.setVisibility(GONE);
        mHeadingImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.red_right_chevron, null));
    }

    public boolean onShow() {
        show();
        return true;        // Default allow show to proceed.
    }

    /**
     * The component notifies when user has clicked the heading to close.
     * Subclasses that wish to perform some check before closing should override this
     * method. They should then return false if the close was inhibited, or if the
     * close is allowed, then simply return super.onClickHide(), which will also do
     * the close.  This method may also be called from the controller to check/close
     * the SettingsItem.
     */
    public boolean onHide() {
        hide();
        return true;        // Default allow hide to proceed.
    }

}
