package com.example.myapplication;


import android.app.Application;

import com.example.insightanalytics.MyLibraryInitializer;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MyLibraryInitializer.init(this);
    }
}
