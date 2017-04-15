package com.vpage.shareInfo.tools;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import com.vpage.shareInfo.service.MyLifeCycleHandler;

public class ShareApplication extends MultiDexApplication {

    private static final String TAG = ShareApplication.class.getName();


    private static Context mContext;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);    //To change body of overridden methods use File | Settings | File Templates.
        //MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        registerActivityLifecycleCallbacks(new MyLifeCycleHandler());

    }

    public static Context getContext() {
        return mContext;
    }


}
