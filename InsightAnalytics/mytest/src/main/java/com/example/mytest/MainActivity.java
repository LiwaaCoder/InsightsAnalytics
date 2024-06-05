package com.example.mytest;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.insightanalytics.StaticDataCollector;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the instance of StaticDataCollector
        StaticDataCollector dataCollector = StaticDataCollector.getInstance();

        // Send device data
        dataCollector.sendDeviceData(this);

        // Update app version count
        String appId = "your_app_id"; // Replace with your actual app ID
        String appVersion = "1.0.0";  // Replace with your actual app version
        dataCollector.updateAppVersionCount(appId, appVersion);

        // Update user count
        dataCollector.updateUserCount(this, appId);
    }
}
