package com.example.minh.flashlight.myService;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.minh.flashlight.IncomingCallReceiver;

public class ServiceIncomingCall extends Service {
    private IncomingCallReceiver receiverCall;
    public static boolean isFlash = false;
    public static Camera camera;
    public static Camera.Parameters p;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (camera == null && p == null) {
                camera = Camera.open();
                p = camera.getParameters();
            }
        }
        receiverCall = new IncomingCallReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filterCall = new IntentFilter("android.intent.action.PHONE_STATE");
        filterCall.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiverCall, filterCall);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
