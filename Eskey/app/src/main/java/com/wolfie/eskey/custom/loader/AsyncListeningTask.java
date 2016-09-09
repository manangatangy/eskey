package com.wolfie.eskey.custom.loader;

import android.os.AsyncTask;

/**
 * Created by david on 6/09/16.
 */

public abstract class AsyncListeningTask<PARAMS, RESULT> extends AsyncTask<PARAMS, Void, RESULT> {

    private Listener<RESULT> mListener;

    public AsyncListeningTask(Listener<RESULT> listener) {
        super();
        mListener = listener;
    }

    public abstract RESULT runInBackground(PARAMS entry);

    @Override
    protected RESULT doInBackground(PARAMS... entries) {
        PARAMS entry = null;
        if (entries != null && entries.length > 0) {
            entry = entries[0];
        }
        return runInBackground(entry);
    }

    @Override
    protected void onPostExecute(RESULT result) {
        if (mListener != null) {
            mListener.onCompletion(result);
        }
    }

    public interface Listener<RESULT> {
        void onCompletion(RESULT result);
    }
}
