package com.busi.adi.aarogyam;

import android.app.Application;

import com.busi.adi.aarogyam.Model.AccessToken;
import com.busi.adi.aarogyam.Model.ProfileDetails;


public class App extends Application {

    public static String ANONYMOUS = "GUEST";
    public static String USER_NAME = ANONYMOUS;
    private static final String TAG = "Application";

    public static AccessToken accessToken = null;
    public static final int BODY_LOCATION_SELECTOR = 1;
    public static final int BODY_SUB_LOCATION_SELECTOR = 2;
    public static final int HEALTH_SYMPTOM_SELECTOR = 3;
    public static final int LOAD_DIAGNOSIS_DATA = 4;
    public static final int HEALTH_ISSUE_INFO = 6;

    public static String YOB = "1998";
    public static String GENDER = "male";
    public static int GROUP = 0;

    public static ProfileDetails profile;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
