package com.example.insightanalytics;


import android.content.Context;
import android.os.Build;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StaticDataCollector {

    private static StaticDataCollector instance;
    private FirebaseFirestore firestore;

    private StaticDataCollector() {
        firestore = FirebaseFirestore.getInstance();
    }

    public static synchronized StaticDataCollector getInstance() {
        if (instance == null) {
            instance = new StaticDataCollector();
        }
        return instance;
    }

    // Send static device data
    public void sendDeviceData(Context context) {
        String deviceModel = Build.MODEL;
        String androidVersion = Build.VERSION.RELEASE;
        String country = Locale.getDefault().getCountry();

        Map<String, Object> deviceData = new HashMap<>();
        deviceData.put("deviceModel", deviceModel);
        deviceData.put("androidVersion", androidVersion);
        deviceData.put("country", country);
        deviceData.put("timestamp", FieldValue.serverTimestamp());

        firestore.collection("Devices")
                .add(deviceData)
                .addOnSuccessListener(documentReference -> {
                    // Log success or handle accordingly
                })
                .addOnFailureListener(e -> {
                    // Log failure or handle accordingly
                });
    }

    // Update app version count
    public void updateAppVersionCount(String appId, String appVersion) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put(appVersion, FieldValue.increment(1));
        updateData.put("timestamp", FieldValue.serverTimestamp());

        firestore.collection(appId).document("AppVersions")
                .set(updateData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    // Update user count
    public void updateUserCount(Context context, String appId) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("userCount", FieldValue.increment(1));
        updateData.put("timestamp", FieldValue.serverTimestamp());

        firestore.collection(appId).document("UserCounts")
                .set(updateData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
}
