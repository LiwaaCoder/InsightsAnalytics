// MainActivity.java
package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.insightanalytics.StaticDataCollector;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize StaticDataCollector
        StaticDataCollector dataCollector = StaticDataCollector.getInstance(this);

        // Generate and save app token
        String appName = "MyApplication";
        String appToken = dataCollector.generateAndSaveAppToken(appName);

        // Use the token for further operations
        if (appToken != null) {
            dataCollector.sendDeviceData(this, appToken);
            dataCollector.updateAppVersionCount(appToken, "1.0.0");
            dataCollector.updateUserCount(this, appToken);

            // Retrieve and log device data
            dataCollector.getDeviceData(appToken);
        } else {
            Log.e("MainActivity", "Failed to generate app token.");
        }
    }
}
