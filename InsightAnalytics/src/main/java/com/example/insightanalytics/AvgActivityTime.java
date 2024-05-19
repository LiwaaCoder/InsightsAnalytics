package com.example.insightanalytics;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import java.util.HashMap;
import java.util.Map;

public class AvgActivityTime {

    private static AvgActivityTime instance;
    private  Map<String, Long> activityStartTimes;
    private  Map<String, Long> activityTotalTimes;
    private  Map<String, Integer> activitySessionCounts;

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

    // Start tracking activity
    public void startActivity(String activityName) {
        long startTime = System.currentTimeMillis();
        activityStartTimes.put(activityName, startTime);
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

    public void registerActivityLifecycleCallbacks(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                // No action needed on activity created
            }

            @Override
            public void onActivityStarted(Activity activity) {
                String activityName = activity.getClass().getSimpleName();
                startActivity(activityName);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                // No action needed on activity resumed
            }

            @Override
            public void onActivityPaused(Activity activity) {
                // No action needed on activity paused
            }

            @Override
            public void onActivityStopped(Activity activity) {
                String activityName = activity.getClass().getSimpleName();
                endActivity(activityName);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                // No action needed on activity save instance state
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                // No action needed on activity destroyed
            }
        });
    }
}
