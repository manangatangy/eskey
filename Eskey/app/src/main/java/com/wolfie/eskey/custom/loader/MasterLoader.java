package com.wolfie.eskey.custom.loader;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.wolfie.eskey.custom.crypto.Crypter;
import com.wolfie.eskey.custom.database.Source;
import com.wolfie.eskey.custom.model.MasterData;

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

    private class StoreTask extends AsyncTask<MasterData, Void, Void> {
        @Override
        protected Void doInBackground(MasterData... params) {
            MasterData masterData = params[0];
            mDataSource.storeMaster(masterData);
            return null;
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
