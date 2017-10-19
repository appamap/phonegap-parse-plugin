package org.apache.cordova.core;

import android.app.Application;
import android.util.Log;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("init","initialize parse with application");
        ParsePlugin.initializeParseWithApplication(this);
    }

}