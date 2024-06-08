// StaticDataCollector.java
package com.example.insightanalytics;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StaticDataCollector {
    private static final String TAG = "StaticDataCollector";
    private static StaticDataCollector instance;
    private FirebaseFirestore firestore;

    private StaticDataCollector(Context context) {
        FirebaseApp.initializeApp(context);
        firestore = FirebaseFirestore.getInstance();
        if (firestore == null) {
            Log.e(TAG, "Firestore instance is null!");
        } else {
            Log.d(TAG, "Firestore instance initialized successfully.");
        }
    }

    public static synchronized StaticDataCollector getInstance(Context context) {
        if (instance == null) {
            instance = new StaticDataCollector(context);
        }
        return instance;
    }

    public void sendDeviceData(Context context, String appToken) {
        if (firestore == null) {
            Log.e(TAG, "Firestore instance is null. Cannot send device data.");
            return;
        }

        String deviceModel = Build.MODEL;
        String androidVersion = Build.VERSION.RELEASE;
        String country = Locale.getDefault().getCountry();

        Map<String, Object> deviceData = new HashMap<>();
        deviceData.put("deviceModel", deviceModel);
        deviceData.put("androidVersion", androidVersion);
        deviceData.put("country", country);
        deviceData.put("timestamp", FieldValue.serverTimestamp());

        firestore.collection("devices")
                .document(appToken)
                .set(deviceData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Device data successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing device data", e));
    }

    public void updateAppVersionCount(String appId, String appVersion) {
        if (firestore == null) {
            Log.e(TAG, "Firestore instance is null. Cannot update app version count.");
            return;
        }

        Map<String, Object> updateData = new HashMap<>();
        updateData.put(appVersion, FieldValue.increment(1));
        updateData.put("timestamp", FieldValue.serverTimestamp());

        firestore.collection(appId).document("AppVersions")
                .set(updateData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "App version count successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating app version count", e));
    }

    public void updateUserCount(Context context, String appId) {
        if (firestore == null) {
            Log.e(TAG, "Firestore instance is null. Cannot update user count.");
            return;
        }

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("userCount", FieldValue.increment(1));
        updateData.put("timestamp", FieldValue.serverTimestamp());

        firestore.collection(appId).document("UserCounts")
                .set(updateData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User count successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating user count", e));
    }

    public String generateAndSaveAppToken(String appName) {
        if (firestore == null) {
            Log.e(TAG, "Firestore instance is null. Cannot generate and save app token.");
            return null;
        }

        String token = UniqueTokenGenerator.generateToken();

        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("appName", appName);
        tokenData.put("token", token);
        tokenData.put("timestamp", FieldValue.serverTimestamp());

        firestore.collection("AppTokens")
                .document(token)
                .set(tokenData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "App token successfully generated and saved!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error saving app token", e));

        return token;
    }

    public void getDeviceData(String appToken) {
        if (firestore == null) {
            Log.e(TAG, "Firestore instance is null. Cannot get device data.");
            return;
        }

        DocumentReference documentReference = firestore.collection("devices").document(appToken);
        documentReference.get().addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Log.d(TAG, "Device Model: " + documentSnapshot.getString("deviceModel"));
                    Log.d(TAG, "Android Version: " + documentSnapshot.getString("androidVersion"));
                    Log.d(TAG, "Country: " + documentSnapshot.getString("country"));
                } else {
                    Log.d(TAG, "No such document");
                }
            }
        }).addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error getting document", e);
            }
        });
    }
}
