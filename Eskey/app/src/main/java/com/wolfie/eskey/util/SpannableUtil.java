package com.wolfie.eskey.util;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;

import com.wolfie.eskey.R;

/**
 * Created by david on 3/11/16.
 */

public class SpannableUtil {

    public static Spannable highlightSearchedText(Context context, String searchPattern, String textToHighlight) {
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(textToHighlight);
        return highlightSearchedText(context, searchPattern, spannable);
    }

    public static Spannable highlightSearchedText(Context context, String searchPattern, Spannable spanToHighlight) {
        if (TextUtils.isEmpty(searchPattern) ||
                !spanToHighlight.toString().toLowerCase().contains(searchPattern.toLowerCase())) {
            return spanToHighlight;
        } else {
            int start = spanToHighlight.toString().toLowerCase().indexOf(searchPattern.toLowerCase());
            int end = start + searchPattern.length();
            spanToHighlight.setSpan(
                    new BackgroundColorSpan(ContextCompat.getColor(context, R.color.search_highlight)),
                    start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanToHighlight.setSpan(
                    new ForegroundColorSpan(Color.WHITE),
                    start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spanToHighlight;
        }
    }
}
