/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iozxllc.movementcompetition;

import java.util.Calendar;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.iozxllc.movementcompetition.MovementHistory.Movement;

/**
 * Service that receives ActivityRecognition updates. It receives updates
 * in the background, even if the main Activity is not visible.
 */
public class ActivityRecognitionIntentService extends IntentService {
    // Store the app's shared preferences repository
    private SharedPreferences mPrefs;

    public ActivityRecognitionIntentService() {
        // Set the label for the service's background thread
        super("ActivityRecognitionIntentService");
    }

    /**
     * Called when a new activity detection update is available.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
    	System.out.println("Activity Recognition Service got Intent");
    	
        // Get a handle to the repository
        mPrefs = getApplicationContext().getSharedPreferences(
                ActivityUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        // If the intent contains an update
        if (ActivityRecognitionResult.hasResult(intent)) {

            // Get the update
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            // Log the update
            logActivityRecognitionResult(result);

            // Get the most probable activity from the list of activities in the update
            DetectedActivity mostProbableActivity = result.getMostProbableActivity();

            // Get the confidence percentage for the most probable activity
            int confidence = mostProbableActivity.getConfidence();

            // Get the type of activity
            int activityType = mostProbableActivity.getType();

            // Check to see if the repository contains a previous activity
            if (!mPrefs.contains(ActivityUtils.KEY_PREVIOUS_ACTIVITY_TYPE)) {

                // This is the first type an activity has been detected. Store the type
                Editor editor = mPrefs.edit();
                editor.putInt(ActivityUtils.KEY_PREVIOUS_ACTIVITY_TYPE, activityType);
                editor.commit();

            // If the repository contains a type
            }/* else if (
                       // If the current type is "moving"
                       isMoving(activityType)

                       &&

                       // The activity has changed from the previous activity
                       activityChanged(activityType)

                       // The confidence level for the current activity is > 50%
                       && (confidence >= 50)) {

                // Notify the user
                sendNotification();
            }*/
        }
    }

    /**
     * Post a notification to the user. The notification prompts the user to click it to
     * open the device's GPS settings
     */
    private void sendNotification() {

        // Create a notification builder that's compatible with platforms >= version 4
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext());

        // Set the title, text, and icon
        builder.setContentTitle(getString(R.string.app_name))
               .setContentText(getString(R.string.turn_on_GPS))
               .setSmallIcon(R.drawable.ic_launcher)

               // Get the Intent that starts the Location settings panel
               .setContentIntent(getContentIntent());

        // Get an instance of the Notification Manager
        NotificationManager notifyManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        // Build the notification and post it
        notifyManager.notify(0, builder.build());
    }
    /**
     * Get a content Intent for the notification
     *
     * @return A PendingIntent that starts the device's Location Settings panel.
     */
    private PendingIntent getContentIntent() {

        // Set the Intent action to open Location Settings
        Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

        // Create a PendingIntent to start an Activity
        return PendingIntent.getActivity(getApplicationContext(), 0, gpsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Tests to see if the activity has changed
     *
     * @param currentType The current activity type
     * @return true if the user's current activity is different from the previous most probable
     * activity; otherwise, false.
     */
    private boolean activityChanged(int currentType) {

        // Get the previous type, otherwise return the "unknown" type
        int previousType = mPrefs.getInt(ActivityUtils.KEY_PREVIOUS_ACTIVITY_TYPE,
                DetectedActivity.UNKNOWN);

        // If the previous type isn't the same as the current type, the activity has changed
        if (previousType != currentType) {
            return true;

        // Otherwise, it hasn't.
        } else {
            return false;
        }
    }

    /**
     * Determine if an activity means that the user is moving.
     *
     * @param type The type of activity the user is doing (see DetectedActivity constants)
     * @return true if the user seems to be moving from one location to another, otherwise false
     */
    private boolean isMoving(int type) {
        switch (type) {
            // These types mean that the user is probably not moving
            case DetectedActivity.STILL :
            case DetectedActivity.TILTING :
            case DetectedActivity.UNKNOWN :
                return false;
            default:
                return true;
        }
    }

    /**
     * Write the activity recognition update to the log file

     * @param result The result extracted from the incoming Intent
     */
    private void logActivityRecognitionResult(ActivityRecognitionResult result) {
        // Get all the probably activities from the updated result
    	DetectedActivity bestActivity = null;
        for (DetectedActivity detectedActivity : result.getProbableActivities()) {

            // Get the activity type, confidence level, and human-readable name
            //int activityType = detectedActivity.getType();
            //int confidence = detectedActivity.getConfidence();
            //String activityName = getNameFromType(activityType);
            
            if (bestActivity == null) {
            	bestActivity = detectedActivity;
            } else if (bestActivity.getConfidence() < detectedActivity.getConfidence()) {
            	bestActivity = detectedActivity;
            }

            //if (confidence >= 50 || !MovementHistory.historyHasEntry(getApplicationContext())) {
            //}
            // Make a timestamp
            /*String timeStamp = mDateFormat.format(new Date());

            // Get the current log file or create a new one, then log the activity
            LogFile.getInstance(getApplicationContext()).log(
                timeStamp +
                LOG_DELIMITER +
                getString(R.string.log_message, activityType, activityName, confidence)
            );*/
        }
        
        if (bestActivity != null) {

        	Movement movement = new Movement();
        	movement.movementTypeID = bestActivity.getType();
        	if (movement.movementTypeID == DetectedActivity.UNKNOWN) {
        		movement.movementTypeID = DetectedActivity.STILL;
        	}
        	movement.movementDate = Calendar.getInstance();
        	movement.confidence = bestActivity.getConfidence();
        	MovementHistory.addMovement(movement, getApplicationContext());
        }
        
        Intent intent = new Intent();
        intent.setAction(ActivityUtils.ACTION_REFRESH_STATUS_LIST);
        intent.addCategory(ActivityUtils.CATEGORY_LOCATION_SERVICES);
        LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(intent);
    }
}
