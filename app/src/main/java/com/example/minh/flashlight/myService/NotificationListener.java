package com.example.minh.flashlight.myService;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Vibrator;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.minh.flashlight.MyCameraManager;

@SuppressLint("OverrideAbstract")
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationListener extends NotificationListenerService {
    boolean hasCameraFlash;
    MyCameraManager cameraManager = new MyCameraManager();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TAGG", "start");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        SharedPreferences sharedPreferences = getSharedPreferences("FLASH_LIGHT", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("IS_NOTIFY", true);
        editor.commit();
        Log.d("TAGG", "connect");
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d("TAGG", "Service: " + ServiceIncomingCall.isFlash);
        hasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        SharedPreferences sharedPreferences = getSharedPreferences("FLASH_LIGHT", Context.MODE_PRIVATE);
        boolean isOnOff = sharedPreferences.getBoolean("ON_OFF", false);
        if (isOnOff == true) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            boolean isNotify = sharedPreferences.getBoolean("IS_NOTIFY", false);
            boolean isVibarate = sharedPreferences.getBoolean("IS_VIBRATE", false);
            if (isNotify == true) {
                int onTime = sharedPreferences.getInt("ON_TIME", 50);
                int offTime = sharedPreferences.getInt("OFF_TIME", 50);
                int runTime = sharedPreferences.getInt("RUN_TIME", 2);
//        String title = sbn.getNotification().extras.getString("android.title");
//        String text = sbn.getNotification().extras.getString("android.text");
                String package_name = sbn.getPackageName();
                boolean isChoose = sharedPreferences.getBoolean(package_name, false);
                if (isChoose == true) {
                    Log.d("TAGG", "ServiceIncomingCall: " + ServiceIncomingCall.isFlash);
                    if (isVibarate == false) {
                        if (ServiceIncomingCall.isFlash == false) {
                            ServiceIncomingCall.isFlash = true;
                            flashLightMode(onTime, offTime, runTime);
                        }
                    } else {
                        if (ServiceIncomingCall.isFlash == false) {
                            flashLightMode(onTime, offTime, runTime, v);
                            ServiceIncomingCall.isFlash = true;
                        }
                    }
                }
            }
            super.onNotificationPosted(sbn);
        }
    }

    private void flashLightMode(final int onTime, final int offTime, final int runTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraManager.initCameraFlash(this, runTime * 1000, onTime, offTime);
        } else {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    cameraManager.initCameraFlashM((runTime * 1000), onTime, offTime);
                }
            });
            thread.start();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void flashLightMode(final int onTime, final int offTime, final int runTime, final Vibrator v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraManager.initCameraFlash(this, runTime * 1000, onTime, offTime, v);
        } else {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    cameraManager.initCameraFlashM((runTime * 1000), onTime, offTime, v);
                }
            });
            thread.start();
        }
    }

}
