package com.wolfie.eskey.nestedlist;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * Created by david on 31/08/16.
 */

public class NestedListView extends ExpandableListView {
    public NestedListView(Context context) {
        super(context);
    }

    public NestedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NestedListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        onMeasureOK(widthMeasureSpec, heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        String text;
        if (heightMode == MeasureSpec.EXACTLY) {
            text = "EXACTLY";
        } else if (heightMode == MeasureSpec.AT_MOST) {
            text = "AT_MOST";
        } else {
            text = "UNSPECIFIED";
        }
        System.out.println(text + ":" + heightSize);
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(500, MeasureSpec.AT_MOST);
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(1500, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onMeasureOK(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 600;
        int desiredHeight = 1500;
        setMeasuredDimension(
                spec(widthMeasureSpec, desiredHeight),
                spec(heightMeasureSpec, desiredWidth));
    }

    int spec(int measureSpec, int desired) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int requested;
        if (mode == MeasureSpec.EXACTLY) {              //Must be this size
            requested = size;
        } else if (mode == MeasureSpec.AT_MOST) {       //Can't be bigger than...
            requested = Math.min(desired, size);
        } else {                                        //Be whatever you want
            requested = desired;
        }
        return requested;
    }
}
