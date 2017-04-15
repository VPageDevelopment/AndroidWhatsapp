package com.vpage.shareInfo.service;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by admin on 2/4/16.
 */
public class MyLifeCycleHandler implements Application.ActivityLifecycleCallbacks {
    public static int startedActivities;
    public static int stoppedActivities;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++startedActivities;
    }

    @Override
    public void onActivityStopped(Activity activity) {
            ++stoppedActivities;
    }
}
