package com.wolfie.eskey.model.database;

/**
 * Created by david on 4/09/16.
 */

public class MetaData {
    public static final String AUTHORITY = "com.wolfie.eskey";
    public static final String DATABASE_NAME = "eskey.db";
    public static final int DATABASE_VERSION = 1;

    public static final String MASTER_TABLE = "master";
    public static final String MASTER_SALT = "salt";
    public static final String MASTER_KEY = "master_key";
    public static final String[] MASTER_ALL_COLUMNS = {
            MASTER_SALT, MASTER_KEY
    };

    public static final String ENTRIES_TABLE = "entries";
    public static final String ENTRIES_ID = "_id";
    public static final String ENTRIES_GROUP = "group_name";
    public static final String ENTRIES_ENTRY = "entry_name";
    public static final String ENTRIES_CONTENT = "content";
    public static final String[] ENTRIES_ALL_COLUMNS = {
            ENTRIES_ID, ENTRIES_GROUP, ENTRIES_ENTRY, ENTRIES_CONTENT
    };

    public static final String DEFAULT_SORT_ORDER = "ENTRIES_ID ASC";
    public static final String QUERY_ORDER = "lower(group_name), group_name, lower(entry_name), entry_name";
}
