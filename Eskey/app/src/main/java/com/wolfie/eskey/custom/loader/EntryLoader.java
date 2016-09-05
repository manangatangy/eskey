package com.wolfie.eskey.custom.loader;

import android.content.Context;
import android.support.annotation.Nullable;

import com.wolfie.eskey.custom.crypto.Crypter;
import com.wolfie.eskey.custom.database.Source;
import com.wolfie.eskey.custom.model.DataSet;
import com.wolfie.eskey.custom.model.Entry;

import java.util.List;

/**
 * Created by david on 4/09/16.
 */

public class EntryLoader extends DataLoader<DataSet> {
    private Source mDataSource;
    private Crypter mCrypter;

    public EntryLoader(Context context, Source dataSource, Crypter crypter) {
        super(context);
        mDataSource = dataSource;
        mCrypter = crypter;
    }

    public void insert(Entry entry) {
        new InsertTask(this).execute(entry);
    }

    public void update(Entry entry) {
        new UpdateTask(this).execute(entry);
    }

    public void delete(Entry entry) {
        new DeleteTask(this).execute(entry);
    }

    @Override
    protected DataSet buildLoadable() {
        DataSet dataSet = mDataSource.read();
        if (!dataSet.isEmpty()) {
            List<Entry> entries = dataSet.getEntries();
            for (int i = 0; i < entries.size(); i++) {
                entries.get(i).encrypt(mCrypter);
            }
        }
        return dataSet;
    }

    private class InsertTask extends AsyncDataTask<Entry, Void, DataSet> {
        InsertTask(EntryLoader loader) {
            super(loader);
        }
        @Override
        protected DataSet doInBackground(Entry... entries) {
            Entry entry = entries[0];
            entry.encrypt(mCrypter);
            mDataSource.insert(entry);
            return null;
        }
    }

    private class UpdateTask extends AsyncDataTask<Entry, Void, DataSet> {
        UpdateTask(EntryLoader loader) {
            super(loader);
        }
        @Override
        protected DataSet doInBackground(Entry... entries) {
            Entry entry = entries[0];
            entry.encrypt(mCrypter);
            mDataSource.update(entry);
            return null;
        }
    }

    private class DeleteTask extends AsyncDataTask<Entry, Void, DataSet> {
        DeleteTask(EntryLoader loader) {
            super(loader);
        }
        @Override
        protected DataSet doInBackground(Entry... entries) {
            Entry entry = entries[0];
            entry.encrypt(mCrypter);
            mDataSource.delete(entry);
            return null;
        }
    }
}
