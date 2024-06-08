package com.example.insightanalytics;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.*;
import java.util.HashMap;
import java.util.Map;

public class AvgActivityTime {

    private static AvgActivityTime instance;
    private final Map<String, Long> activityStartTimes;
    private final Map<String, Long> activityTotalTimes;
    private final Map<String, Integer> activitySessionCounts;
    private FirebaseFirestore firestore;
    private String appId;

    private AvgActivityTime() {
        activityStartTimes = new HashMap<>();
        activityTotalTimes = new HashMap<>();
        activitySessionCounts = new HashMap<>();
    }

    public static synchronized AvgActivityTime getInstance() {
        if (instance == null) {
            instance = new AvgActivityTime();
        }
        return instance;
    }

    // Initialize Firebase
    public void initializeFirebase(@NonNull Context context, @NonNull String appId, @NonNull String appVersion) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context);
        }
        firestore = FirebaseFirestore.getInstance();
        this.appId = appId;

        // Send static device data
        StaticDataCollector.getInstance(context).sendDeviceData(context, appVersion);

        // Update app version count
        StaticDataCollector.getInstance(context).updateAppVersionCount(appId, appVersion);

        // Update user count
        StaticDataCollector.getInstance(context).updateUserCount(context, appId);
    }

    // Start tracking activity
    public void startActivity(String activityName) {
        long startTime = System.currentTimeMillis();
        activityStartTimes.put(activityName, startTime);
        incrementActivityStartCount(activityName);
    }

    // End tracking activity
    public void endActivity(String activityName) {
        Long startTime = activityStartTimes.get(activityName);
        if (startTime != null) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            activityStartTimes.remove(activityName);

            activityTotalTimes.put(activityName, activityTotalTimes.getOrDefault(activityName, 0L) + duration);
            activitySessionCounts.put(activityName, activitySessionCounts.getOrDefault(activityName, 0) + 1);
        }
    }

    // Get the average activity time
    public long getAverageActivityTime(String activityName) {
        Long totalTime = activityTotalTimes.get(activityName);
        Integer sessionCount = activitySessionCounts.get(activityName);

        if (totalTime != null && sessionCount != null && sessionCount > 0) {
            return totalTime / sessionCount;
        }
        return 0;
    }

    // Increment activity start count
    private void incrementActivityStartCount(String activityName) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put(activityName, FieldValue.increment(1));
        updateData.put("timestamp", FieldValue.serverTimestamp());

        firestore.collection(appId).document("ActivityStartCounts")
                .set(updateData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Map<String, Object> initialData = new HashMap<>();
                    initialData.put(activityName, 1);
                    initialData.put("timestamp", FieldValue.serverTimestamp());
                    firestore.collection(appId).document("ActivityStartCounts")
                            .set(initialData, SetOptions.merge());
                });
    }

    // Send average times to Firestore
    public void sendAverageTimesToFirestore() {
        if (firestore == null) {
            throw new IllegalStateException("Firestore not initialized. Call initializeFirebase(context, appId, appVersion) first.");
        }

        Map<String, Object> avgTimes = new HashMap<>();
        for (String activityName : activityTotalTimes.keySet()) {
            long avgTime = getAverageActivityTime(activityName);
            avgTimes.put(activityName, avgTime);
        }
        avgTimes.put("timestamp", FieldValue.serverTimestamp());

        firestore.collection(appId).document("ActivityTimes")
                .set(avgTimes, SetOptions.merge())
                .addOnSuccessListener(documentReference -> {
                    // Log success or handle accordingly
                })
                .addOnFailureListener(e -> {
                    // Log failure or handle accordingly
                });
    }

    // Retrieve top 10 activities based on average activity time
    public void getTop10ActivitiesByAvgTime() {
        firestore.collection(appId).document("ActivityTimes")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> data = documentSnapshot.getData();
                        if (data != null) {
                            data.entrySet().stream()
                                    .filter(entry -> !entry.getKey().equals("timestamp"))
                                    .sorted((entry1, entry2) -> Long.compare((Long) entry2.getValue(), (Long) entry1.getValue()))
                                    .limit(10)
                                    .forEach(entry -> {
                                        String activityName = entry.getKey();
                                        Long avgTime = (Long) entry.getValue();
                                        // Display or handle the top 10 activities based on average time
                                        System.out.println("Activity: " + activityName + ", Avg Time: " + avgTime);
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    // Retrieve top 10 activities based on start counts
    public void getTop10ActivitiesByStartCount() {
        firestore.collection(appId).document("ActivityStartCounts")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> data = documentSnapshot.getData();
                        if (data != null) {
                            data.entrySet().stream()
                                    .filter(entry -> !entry.getKey().equals("timestamp"))
                                    .sorted((entry1, entry2) -> Long.compare((Long) entry2.getValue(), (Long) entry1.getValue()))
                                    .limit(10)
                                    .forEach(entry -> {
                                        String activityName = entry.getKey();
                                        Long startCount = (Long) entry.getValue();
                                        // Display or handle the top 10 activities based on start count
                                        System.out.println("Activity: " + activityName + ", Start Count: " + startCount);
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    // Retrieve and sort data by timestamp
    public void getActivityDataSortedByDate() {
        firestore.collection(appId).orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Map<String, Object> data = documentSnapshot.getData();
                        if (data != null) {
                            String documentId = documentSnapshot.getId();
                            Long timestamp = documentSnapshot.getLong("timestamp");
                            // Display or handle the data sorted by date
                            System.out.println("Document ID: " + documentId + ", Timestamp: " + timestamp + ", Data: " + data);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
}
