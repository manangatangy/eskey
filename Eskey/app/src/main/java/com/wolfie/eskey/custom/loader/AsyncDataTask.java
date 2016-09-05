package com.wolfie.eskey.custom.loader;

import android.content.Loader;
import android.os.AsyncTask;

/**
 * Created by david on 4/09/16.
 */

public abstract class AsyncDataTask<PARAMS, PROGRESS, RESULT>
        extends AsyncTask<PARAMS, PROGRESS, RESULT> {

    private Loader<RESULT> mLoader = null;

    public AsyncDataTask(Loader<RESULT> loader) {
        this.mLoader = loader;
    }

    @Override
    protected void onPostExecute(RESULT result) {
        super.onPostExecute(result);
    }
}
