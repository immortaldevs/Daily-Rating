package com.iozxllc.movementcompetition;


/**
 * TODO: Insert description here. (generated by jmalin)
 */
public final class ActivityUtils {

    // Used to track what type of request is in process
    public enum REQUEST_TYPE {ADD, REMOVE}

    public static final String APPTAG = "ActivitySample";

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static final String ACTION_REFRESH_STATUS_LIST =
                    "com.iozxllc.movementcompetition.ACTION_REFRESH_STATUS_LIST";

    public static final String CATEGORY_LOCATION_SERVICES =
            "com.iozxllc.movementcompetition.CATEGORY_LOCATION_SERVICES";

    // Constants used to establish the activity update interval
    public static final int MILLISECONDS_PER_SECOND = 1000;

    public static final int DETECTION_INTERVAL_SECONDS = 20;

    public static final int DETECTION_INTERVAL_MILLISECONDS =
            MILLISECONDS_PER_SECOND * DETECTION_INTERVAL_SECONDS;

    // Shared Preferences repository name
    public static final String SHARED_PREFERENCES =
            "com.iozxllc.movementcompetition.SHARED_PREFERENCES";

    // Key in the repository for the previous activity
    public static final String KEY_PREVIOUS_ACTIVITY_TYPE =
            "com.iozxllc.movementcompetition.KEY_PREVIOUS_ACTIVITY_TYPE";
}