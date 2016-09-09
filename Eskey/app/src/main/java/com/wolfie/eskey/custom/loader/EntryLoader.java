package com.wolfie.eskey.custom.loader;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.wolfie.eskey.custom.crypto.Crypter;
import com.wolfie.eskey.custom.database.Source;
import com.wolfie.eskey.custom.model.DataSet;
import com.wolfie.eskey.custom.model.Entry;

import java.util.List;

/**
 * Created by david on 4/09/16.
 */

public class EntryLoader {
    private Context mContext;
    private Source mDataSource;
    private Crypter mCrypter;

    public EntryLoader(Context context, Source dataSource, Crypter crypter) {
        mContext = context;
        mDataSource = dataSource;
        mCrypter = crypter;
    }

    public void read(AsyncListeningTask.Listener<DataSet> listener) {
        new ReadTask(listener).execute();
    }

    public void insert(Entry entry) {
        new InsertTask(new ToastListener("insert")).execute(entry);
    }

    public void update(Entry entry) {
        new UpdateTask(new ToastListener("modify")).execute(entry);
    }

    public void delete(Entry entry) {
        new DeleteTask(new ToastListener("delete")).execute(entry);
    }

    private class ReadTask extends AsyncListeningTask<Void, DataSet> {
        public ReadTask(@Nullable Listener<DataSet> listener) {
            super(listener);
        }
        @Override
        public DataSet runInBackground(Void v) {
            DataSet dataSet = mDataSource.read();
            List<Entry> entries = dataSet.getEntries();
            for (int i = 0; i < entries.size(); i++) {
                entries.get(i).decrypt(mCrypter);
            }
            return dataSet;
        }
    }

    private class InsertTask extends AsyncListeningTask<Entry, Boolean> {
        public InsertTask(@Nullable Listener<Boolean> listener) {
            super(listener);
        }
        @Override
        public Boolean runInBackground(Entry entry) {
            entry.encrypt(mCrypter);
            return mDataSource.insert(entry);
        }
    }

    private class UpdateTask extends AsyncListeningTask<Entry, Boolean> {
        public UpdateTask(@Nullable Listener<Boolean> listener) {
            super(listener);
        }
        @Override
        public Boolean runInBackground(Entry entry) {
            entry.encrypt(mCrypter);
            return mDataSource.update(entry);
        }
    }

    private class DeleteTask extends AsyncListeningTask<Entry, Boolean> {
        public DeleteTask(@Nullable Listener<Boolean> listener) {
            super(listener);
        }
        @Override
        public Boolean runInBackground(Entry entry) {
            entry.encrypt(mCrypter);
            return mDataSource.delete(entry);
        }
    }

    private class ToastListener implements AsyncListeningTask.Listener<Boolean> {
        private String mPrefix;
        public ToastListener(String prefix) {
            mPrefix = prefix;
        }
        @Override
        public void onCompletion(Boolean success) {
            String msg = mPrefix + (success ? " succeeded" : " failed");
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
