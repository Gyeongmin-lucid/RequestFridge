package com.example.kgm13.requestfridge;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.kakao.auth.KakaoSDK;

/**
 * Created by KYS on 2017-04-07.
 */

public class GlobalApplication extends Application {
    private static volatile GlobalApplication obj = null;
    private static volatile Activity currentActivity = null;
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        obj = this;
        GlobalApplication.context = getApplicationContext();
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    public static GlobalApplication getGlobalApplicationContext() {
        return obj;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        GlobalApplication.currentActivity = currentActivity;
    }

    public static Context getAppContext() {
        return GlobalApplication.context;
    }
}
