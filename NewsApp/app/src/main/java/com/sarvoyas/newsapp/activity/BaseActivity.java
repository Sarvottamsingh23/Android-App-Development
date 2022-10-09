package com.sarvoyas.newsapp.activity;

import android.app.Application;

import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;

public class BaseActivity  extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AudienceNetworkAds.initialize(this);
        MobileAds.initialize(this);

    }
}
