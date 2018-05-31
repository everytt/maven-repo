package com.every.tt.sampleapp;

import android.support.multidex.MultiDexApplication;

import com.evevry.tt.library.GlobalT;

public class SampleApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        GlobalT.init(getApplicationContext());
    }
}
