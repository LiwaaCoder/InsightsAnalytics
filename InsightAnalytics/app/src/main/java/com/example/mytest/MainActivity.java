package com.example.mytest;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import java.util.HashMap;
import java.util.Map;

import com.example.insightanalytics.AvgActivityTime;



public class MainActivity extends AppCompatActivity {

    private static final String APP_ID = "your_app_id"; // Replace with your actual app ID
    private static final String APP_VERSION = "1.0.0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase and SDK
        FirebaseApp.initializeApp(this);
        StaticDataCollector.getInstance().sendDeviceData(this);
        StaticDataCollector.getInstance().updateAppVersionCount(APP_ID, APP_VERSION);
        StaticDataCollector.getInstance().updateUserCount(this, APP_ID);
        AvgActivityTime.getInstance().initializeFirebase(this, APP_ID, APP_VERSION);
        Events.getInstance().initializeFirebase(this, APP_ID);

        // Track activity start
        AvgActivityTime.getInstance().startActivity("MainActivity");

        // Log a custom event
        Map<String, Object> eventParams = new HashMap<>();
        eventParams.put("button_clicked", "start_button");
        Events.getInstance().logEvent("ButtonClick", eventParams);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Track activity end
        AvgActivityTime.getInstance().endActivity("MainActivity");

        // Send average activity times to Firestore
        AvgActivityTime.getInstance().sendAverageTimesToFirestore();
    }
}
