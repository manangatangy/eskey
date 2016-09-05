package com.wolfie.eskey.zzzdeprecated.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by david on 3/09/16.
 */

public class TableData {

    public static final class Entry implements BaseColumns {

        public static final String TABLE = "entries";
        public static final Uri    CONTENT_URI = Uri.parse("content://" + MetaData.AUTHORITY + "/" + TABLE);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.eskey.entries";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.eskey.entries";
        public static final String DEFAULT_SORT_ORDER = "ENTRIES_ID ASC";
        public static final String QUERY_ORDER = "group_name ASC, entry_name ASC";
        public static final String GROUP = "group_name";
        public static final String ENTRY = "entry_name";
        public static final String CONTENT = "content";

    }

}
