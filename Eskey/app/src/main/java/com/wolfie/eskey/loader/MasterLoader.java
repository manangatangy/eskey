package com.wolfie.eskey.loader;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.wolfie.eskey.crypto.Crypter;
import com.wolfie.eskey.database.Source;
import com.wolfie.eskey.model.MasterData;

/**
 * Created by david on 5/09/16.
 */

public class MasterLoader {

    private Source mDataSource;
    private Crypter mCrypter;
    private Listener mListener;

    public interface Listener {
        void onRetrieve(MasterData masterData);
    }

    public MasterLoader(Context context, Source dataSource, Crypter crypter) {
        mDataSource = dataSource;
        mCrypter = crypter;
    }

    public void store(MasterData masterData) {
        new StoreTask().execute(masterData);
    }

    public void retrieve(@NonNull Listener listener) {
        mListener = listener;
        new RetrieveTask().execute();
    }

    private class StoreTask extends AsyncTask<MasterData, Void, Boolean> {
        @Override
        protected Boolean doInBackground(MasterData... params) {
            MasterData masterData = params[0];
            return mDataSource.storeMaster(masterData);
        }
        @Override
        protected void onPostExecute(Boolean success) {
        }

    }

    private class RetrieveTask extends AsyncTask<Void, Void, MasterData> {
        @Override
        protected MasterData doInBackground(Void... params) {
            return mDataSource.readMaster();
        }
        @Override
        protected void onPostExecute(MasterData masterData) {
            mListener.onRetrieve(masterData);
        }
    }
}
