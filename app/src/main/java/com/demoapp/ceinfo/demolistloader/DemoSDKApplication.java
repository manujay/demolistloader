package com.demoapp.ceinfo.demolistloader;

import android.app.Application;

import com.mmi.LicenceManager;

/**
 * Created by ceinfo on 27-01-2017.
 */

public class DemoSDKApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LicenceManager.getInstance().setRestAPIKey("82vfhkac2sy7gs3gltlncflrt27gg7mn");
        LicenceManager.getInstance().setMapSDKKey("872p84pjvijbtbtv1hft3iim4fh3a21u");
    }
}
