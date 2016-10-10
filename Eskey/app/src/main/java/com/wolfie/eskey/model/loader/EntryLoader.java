package com.wolfie.eskey.model.loader;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.wolfie.eskey.util.crypto.Crypter;
import com.wolfie.eskey.model.database.Source;
import com.wolfie.eskey.model.DataSet;
import com.wolfie.eskey.model.Entry;
import com.wolfie.eskey.model.loader.AsyncListeningTask.Listener;
import java.util.List;

/**
 * Created by david on 4/09/16.
 */

/**
 * Class that accesses data using a Source, and applies crypto operations to this data
 * using the Crypter. A null Crypter is allowed and it  will pass data through unchanged.
 */
public class EntryLoader {
    private Context mContext;           // TODO deprecate
    private Source mDataSource;
    private Crypter mCrypter;

    public EntryLoader(Context context, Source dataSource) {
        mContext = context;
        mDataSource = dataSource;
    }

    public void setCrypter(Crypter crypter) {
        mCrypter = crypter;
    }

    public void read(AsyncListeningTask.Listener<DataSet> listener) {
        new ReadTask(listener).execute();
    }

    public void insert(Entry entry, @Nullable Listener<Boolean> listener) {
        new InsertTask(listener).execute(entry);
    }

    public void update(Entry entry, @Nullable Listener<Boolean> listener) {
        new UpdateTask(listener).execute(entry);
    }

    public void delete(Entry entry, @Nullable Listener<Boolean> listener) {
        new DeleteTask(listener).execute(entry);
    }

    // TODO deprecate
    public void insert(Entry entry) {
        ToastListener toastListener = new ToastListener("insert " + entry.getEntryName());
        new InsertTask(toastListener).execute(entry);
    }

    // TODO deprecate
    public void update(Entry entry) {
        ToastListener toastListener = new ToastListener("modify " + entry.getEntryName());
        new UpdateTask(toastListener).execute(entry);
    }

    // TODO deprecate
    public void delete(Entry entry) {
        ToastListener toastListener = new ToastListener("delete " + entry.getEntryName());
        new DeleteTask(toastListener).execute(entry);
    }

    /**
     * Insert a new Entry ino the database. On completion a toast shows the
     * success or failure.  If successful, then a ReadTask is started for the
     * listener.
     * TODO deprecate
     */
    public void insertAndRead(Entry entry, AsyncListeningTask.Listener<DataSet> listener) {
        ToastListenerReader toastListenerReader
                = new ToastListenerReader("insert " + entry.getEntryName(), listener);
        new InsertTask(toastListenerReader).execute(entry);
    }

    // TODO deprecate
    public void updateAndRead(Entry entry, AsyncListeningTask.Listener<DataSet> listener) {
        ToastListenerReader toastListenerReader
                = new ToastListenerReader("modify " + entry.getEntryName(), listener);
        new UpdateTask(toastListenerReader).execute(entry);
    }

    // TODO deprecate
    public void deleteAndRead(Entry entry, AsyncListeningTask.Listener<DataSet> listener) {
        ToastListenerReader toastListenerReader
                = new ToastListenerReader("delete " + entry.getEntryName(), listener);
        new DeleteTask(toastListenerReader).execute(entry);
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

    // TODO deprecate
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

    /**
     * After the ToastListener completes, this class starts a ReadTask for the dataset
     * listener.
     */
    // TODO deprecate
    private class ToastListenerReader extends ToastListener {
        private AsyncListeningTask.Listener<DataSet> mDataSetReadListener;
        public ToastListenerReader(String prefix,
                                   AsyncListeningTask.Listener<DataSet> dataSetReadListener) {
            super(prefix);
            mDataSetReadListener = dataSetReadListener;
        }
        @Override
        public void onCompletion(Boolean success) {
            super.onCompletion(success);
            new ReadTask(mDataSetReadListener).execute();
        }
    }
}
