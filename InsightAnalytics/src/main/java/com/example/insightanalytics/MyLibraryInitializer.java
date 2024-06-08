package com.example.insightanalytics;


import android.content.Context;

import com.google.firebase.FirebaseApp;

public class MyLibraryInitializer {
    public static void init(Context context) {
        FirebaseApp.initializeApp(context);
    }
}
