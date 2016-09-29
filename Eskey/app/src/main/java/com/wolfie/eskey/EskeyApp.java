package com.wolfie.eskey;

import android.app.Application;

/**
 * Created by david on 27/09/16.
 */

public class EskeyApp extends Application {

    public static final String TAG = EskeyApp.class.getSimpleName();

//    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

//        initAppComponent();
    }

//    private void initAppComponent() {
//        appComponent = DaggerFactory.getAppComponent(this);
//    }

//    public AppComponent getAppComponent() {
//        return appComponent;
//    }
}
