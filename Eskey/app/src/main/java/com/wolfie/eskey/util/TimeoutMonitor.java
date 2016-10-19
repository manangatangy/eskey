package com.wolfie.eskey.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by david on 4/10/16.
 */

/**
 * Usage; register the UserInactivityTimeoutListener and then setDetection(true).
 * Ensure that onUserInteraction is called from Activity.onUserInteraction.
 * Once the timeout occurs, then isTimedOut returns false, until reset with a
 * new call to setDetection(true).
 */
public class TimeoutMonitor implements Runnable {

    private static final long DISCONNECT_TIMEOUT = 300000; // 5 min = 5 * 60 * 1000 ms  ==> 300000
    private UserInactivityTimeoutListener mInactivityListener;
    private boolean mDetectionEnabled = false;
    private long mStartTime;

    private static Handler disconnectHandler = new Handler() {
        public void handleMessage(Message msg) {
        }
    };

    public interface UserInactivityTimeoutListener {
        void onUserInactivityTimeout();
    }

    public void setUserInactivityTimeoutListener(UserInactivityTimeoutListener listener) {
        mInactivityListener = listener;
        Log.d("TimeoutMonitor", "UserInactivityListener is " + (listener == null ? "CLEARED" : "SET"));
    }

    /**
     * This method will mark the start time and set a timer callback, that will call
     * the UserInactivityTimeoutListener (if not null) upon user inactivity timeout.
     * Call stopTimer to remove the timer callback.
     */
    public void markTimeAndStart() {
        Log.d("TimeoutMonitor", "timer started");
        mDetectionEnabled = true;
        resetInactivityTimer();
    }

    /**
     * This method will stop the callback but will not change the start time.
     * Therefore the start time can later be checked with isTimedOut (even if
     * the UserInactivityTimeoutListener is null,
     */
    public void stopTimer() {
        Log.d("TimeoutMonitor", "timer stopped");
        mDetectionEnabled = false;
        stopInactivityTimer();
    }

    /**
     * Can still return true, even if stopTimer was called.
     */
    public boolean isTimedOut() {
        boolean timedOut =  (System.currentTimeMillis() - mStartTime >= DISCONNECT_TIMEOUT);
        Log.d("TimeoutMonitor", "isTimedOut called, returned " + timedOut);
        return timedOut;
    }

    @Override
    public void run() {
        if (mInactivityListener != null) {
            Log.d("TimeoutMonitor", "inactivity timeout occurred");
            mInactivityListener.onUserInactivityTimeout();
        }
    }

    private void stopInactivityTimer() {
        disconnectHandler.removeCallbacks(this);
    }

    private void resetInactivityTimer() {
        stopInactivityTimer();
        disconnectHandler.postDelayed(this, DISCONNECT_TIMEOUT);
        mStartTime = System.currentTimeMillis();
    }

    public void onUserInteraction() {
        if (mDetectionEnabled) {
            resetInactivityTimer();
        }
    }

}
