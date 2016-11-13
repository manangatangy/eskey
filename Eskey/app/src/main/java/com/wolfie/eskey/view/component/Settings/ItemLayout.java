package com.wolfie.eskey.view.component.Settings;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.Log;
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

public abstract class ItemLayout extends LinearLayout {

    @BindView(R.id.settings_heading)
    LinearLayout mHeading;

    @BindView(R.id.settings_heading_text)
    TextView mHeadingText;

    @BindView(R.id.settings_heading_icon)
    ImageView mHeadingImageView;

    @BindView(R.id.settings_content)
    LinearLayout mContent;

    protected Unbinder unbinder;

    private GroupSetting mGroupSetting;

    public ItemLayout(Context context) {
        super(context);
        initView();
    }

    public ItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public ItemLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    public void setGroupSetting(GroupSetting groupSetting) {
        mGroupSetting = groupSetting;
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
                    Log.d("eskey", "ItemLayout.onClickHide(" + getHeadingText() + ")");
                    // Before proceeding with the hide, first check with the subclass.
                    if (onHide()) {
                        hide();     // Will notify GroupSetting
                    }
                } else {
                    Log.d("eskey", "ItemLayout.onClickShow(" + getHeadingText() + ")");
                    // Before proceeding with the show, first check with the group.
                    if (mGroupSetting.requestToHideCurrent()) {
                        show();     // Will notify GroupSetting
                    }
                }
            }
        });
    }

    /**
     * Should be overridden if the subclass needs to check something before allowing hide.
     * Return true if the hide is allowed to proceed.
     */
    public boolean onHide() {
        Log.d("eskey", "ItemLayout.onHide(" + getHeadingText() + ") default ==> YES");
        return true;        // Default allow hide to proceed.
    }

    public void hide() {
        Log.d("eskey", "ItemLayout.hide(" + getHeadingText() + ")");
        mContent.setVisibility(GONE);
        mHeadingImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.red_right_chevron, null));
        mGroupSetting.notifyItemIsHiding();
    }

    /**
     * Should be overridden if the subclass needs to initialise something on showing.
     */
    public void show() {
        Log.d("eskey", "ItemLayout.show(" + getHeadingText() + ")");
        mContent.setVisibility(VISIBLE);
        mHeadingImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.red_delete, null));
        mGroupSetting.notifyItemIsShowing(this);
    }

    private void initView() {
        // Add the new inflated layout into this group.
        View view = inflate(getContext(), R.layout.view_settings_item, null);
        addView(view, 0);
    }

    public abstract String getHeadingText();

}
