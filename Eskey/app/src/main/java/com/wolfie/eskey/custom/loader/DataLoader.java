package com.wolfie.eskey.custom.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by david on 4/09/16.
 */

public abstract class DataLoader<L extends DataLoader.Loadable> extends AsyncTaskLoader<L> {

    public interface Loadable {
        boolean isEmpty();      // True if there is no user data available.
        void clear();           // Removes user data.
    }

    protected L mLastLoadable = null;

    protected abstract L buildLoadable();

    public DataLoader(Context context) {
        super(context);
    }

    /** Called in a worker thread. */
    @Override
    public L loadInBackground() {
        return buildLoadable();
    }

    @Override
    public void deliverResult(L loadable) {
        if (isReset()) {        // An async query came in while the loader is stopped
            emptyLoadable(loadable);
            return;
        }
        L oldLoadable = mLastLoadable;
        mLastLoadable = loadable;
        if (isStarted()) {
            super.deliverResult(loadable);
        }
        if (oldLoadable != loadable) {
            emptyLoadable(oldLoadable);
        }
    }

    /**
     * Starts an asynchronous load of the list data. When the result is ready
     * the callbacks will be called on the UI thread. If a previous load has
     * been completed and is still valid the result may be passed to the
     * callbacks immediately.
     */
    @Override
    protected void onStartLoading() {
        if (mLastLoadable != null) {
            deliverResult(mLastLoadable);
        }
        if (takeContentChanged() || mLastLoadable == null || mLastLoadable.isEmpty()) {
            forceLoad();
        }
    }

    /**
     * Must be called from the UI thread, triggered by a call to stopLoading().
     */
    @Override
    protected void onStopLoading() {
        cancelLoad();       // Attempt to cancel the current load task if possible.
    }

    /**
     * Must be called from the UI thread, triggered by a call to cancel(). Here,
     * we make sure our Cursor is closed, if it still exists and is not already
     * closed.
     */
    @Override
    public void onCanceled(L loadable) {
        if (loadable != null && !loadable.isEmpty()) {
            emptyLoadable(loadable);
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();            // Ensure the loader is stopped
        emptyLoadable(mLastLoadable);
        mLastLoadable = null;
    }

    protected void emptyLoadable(L loadable) {
        if (loadable != null && !loadable.isEmpty()) {
            loadable.clear();
        }
    }

}

