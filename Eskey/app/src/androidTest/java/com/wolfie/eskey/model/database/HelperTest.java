package com.wolfie.eskey.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Created by david on 11/10/16.
 */

@RunWith(AndroidJUnit4.class)
public class HelperTest {

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.wolfie.eskey", appContext.getPackageName());
        Helper mHelper = new Helper(appContext);
        SQLiteDatabase mDatabase = mHelper.getWritableDatabase();
        mHelper.dropTables(mDatabase);
        mHelper.onCreate(mDatabase);

    }

    /*
            mHelper = new Helper(context);
        mDatabase = mHelper.getWritableDatabase();

     */
}
