package com.securedemo.myapplication.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import android.util.Log;

import androidx.annotation.NonNull;

import unified.vpn.sdk.CompletableCallback;
import unified.vpn.sdk.TrackingConstants;
import unified.vpn.sdk.UnifiedSdk;
import unified.vpn.sdk.VpnException;

public class MyApp_Controller extends Application {
    private static MyApp_Controller ourInstance = new MyApp_Controller();


    public static MyApp_Controller getInstance() {
        return ourInstance;
    }

    private int numStarted = 0;
    public static boolean fast_start = false;

    @Override
    public void onCreate() {
        super.onCreate();
        ourInstance = this;
        SetAppOpenads();
    }

    private void SetAppOpenads() {

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                Log.d("App_Controller", "onActivityCreated");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.d("App_Controller", "onActivityStarted");
                if (numStarted == 0) {

                }
                numStarted++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.d("App_Controller", "onActivityResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.d("App_Controller", "onActivityPaused");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.d("App_Controller", "onActivityStopped");
                numStarted--;
                if (numStarted == 0) {
                    Log.d("App_Controller", "background");
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                Log.d("App_Controller", "onActivitySaveInstanceState");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.d("App_Controller", "onActivityDestroyed");
                if (numStarted == 0) {
                    Log.d("App_Controller", "onActivityDestroyed 1 1 =");
                    if (Prefrences.getisServerConnect()) {
                        disconnectFromVnp();
                    }
                }
            }
        });



    }

    public void disconnectFromVnp() {
        UnifiedSdk.getInstance().getVpn().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
            @Override
            public void complete() {
                Utils.server_Start = false;
                Prefrences.setisServerConnect(false);
            }

            @Override
            public void error(@NonNull VpnException e) {
                Utils.server_Start = false;
                Prefrences.setisServerConnect(false);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }

    public static MyApp_Controller getApp() {
        if (ourInstance == null) {
            ourInstance = new MyApp_Controller();
        }
        return ourInstance;
    }


}