package com.wolfie.eskey.zzzdeprecated.database;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.wolfie.eskey.zzzdeprecated.model.Entry;

import java.util.List;

/**
 * Created by david on 4/09/16.
 */

public class AccessManager implements LoaderManager.LoaderCallbacks<Cursor> {

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public interface Listener {
        void onRetrieve(List<Entry> entries);
        void onAdd();
        void onDelete();
        void onModify();
    }

    public void retrieve(final @NonNull Listener listener) {

//        new AsyncTask<Void, Void, List<Entry>>() {
//            @Override
//            protected List<Entry> doInBackground(Void... params) {
//
//                return null;
//            }
//            @Override
//            protected void onPostExecute(List<Entry> entries) {
//                listener.onRetrieve(entries);
//            }
//        }.execute();
    }

}


/*

LoaderManager.LoaderCallbacks<Cursor>

    private int doExport(Uri uri, String[] proj, File file) throws IOException, JSONException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        int c = 0;
        Cursor exportCursor = managedQuery(uri, proj, null, null, null);
        while (exportCursor.moveToNext()) {
            c++;
            JSONObject json = new JSONObject();
            for (String name : proj) {
                String value = getField(exportCursor, name);
                json.put(name, value);
            }
            writer.write(json.toString());
            writer.newLine();
        }
        writer.close();
        exportCursor.close();
        return c;
    }

    private String getField(Cursor cursor, String sourceName) {
        int sourceIndex = cursor.getColumnIndex(sourceName);
        String value = cursor.getString(sourceIndex);
        return value;
    }

 */