package com.example.insightanalytics;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class MyLibraryInitializer {
    public static void init(Application application) {
        FirebaseApp.initializeApp(application);
    }
}