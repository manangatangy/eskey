package com.wolfie.eskey.model.loader;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.wolfie.eskey.model.DataSet;
import com.wolfie.eskey.model.Entry;
import com.wolfie.eskey.util.crypto.Crypter;
import com.wolfie.eskey.model.database.Source;
import com.wolfie.eskey.model.MasterData;

import java.util.List;

public class MasterLoader {

    private Source mDataSource;
    private Crypter mCrypter;

    public MasterLoader(Source dataSource, Crypter crypter) {
        mDataSource = dataSource;
        mCrypter = crypter;
    }

    public void read(AsyncListeningTask.Listener<MasterData> listener) {
        new MasterLoader.ReadTask(listener).execute();
    }

    /**
     * store == delete then insert
     */
    public void store(MasterData masterData, @Nullable AsyncListeningTask.Listener<Boolean> listener) {
        new MasterLoader.StoreTask(listener).execute(masterData);
    }

    private class ReadTask extends AsyncListeningTask<Void, MasterData> {
        public ReadTask(@Nullable Listener<MasterData> listener) {
            super(listener);
        }

        @Override
        public MasterData runInBackground(Void v) {
            return mDataSource.readMaster();
        }
    }

    private class StoreTask extends AsyncListeningTask<MasterData, Boolean> {
        public StoreTask(@Nullable Listener<Boolean> listener) {
            super(listener);
        }

        @Override
        public Boolean runInBackground(MasterData masterData) {
            return mDataSource.storeMaster(masterData);
        }
    }

}