package com.ice.tiptext;


import android.app.Application;

import com.ice.tiptext.manager.ActivityManager;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActivityManager.getInstance().init(this);
    }
}
