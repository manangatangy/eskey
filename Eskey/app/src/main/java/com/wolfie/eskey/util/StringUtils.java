package com.wolfie.eskey.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by david on 11/10/16.
 */

public class StringUtils {

    public static boolean isNull(@Nullable String str) {
        return (str == null);
    }

    public static boolean isEmpty(@NonNull String str) {
        return (str.length() == 0);
    }

    public static boolean isBlank(@Nullable String str) {
        return (isNull(str) || isEmpty(str));
    }

    public static boolean isNotBlank(@Nullable String str) {
        return (!isNull(str) && !isEmpty(str));
    }

}
