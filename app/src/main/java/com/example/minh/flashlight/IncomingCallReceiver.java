package com.example.minh.flashlight;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.minh.flashlight.myService.ServiceIncomingCall;

public class IncomingCallReceiver extends BroadcastReceiver {
    MyCameraManager cameraManager = new MyCameraManager();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("TAGG", "phone-message");
        SharedPreferences sharedPreferences = context.getSharedPreferences("FLASH_LIGHT", Context.MODE_PRIVATE);
        boolean isOnOff = sharedPreferences.getBoolean("ON_OFF", false);
        if (isOnOff == true) {
            boolean isPhone = sharedPreferences.getBoolean("IS_PHONE", false);
            boolean isMessage = sharedPreferences.getBoolean("IS_MESSAGE", false);
            boolean isVibrate = sharedPreferences.getBoolean("IS_VIBRATE", false);
            int onTime = sharedPreferences.getInt("ON_TIME", 50);
            int offTime = sharedPreferences.getInt("OFF_TIME", 50);
            int runTime = sharedPreferences.getInt("RUN_TIME", 2);
            if (intent.getAction().equals("android.intent.action.PHONE_STATE") && isPhone == true) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    String state = extras.getString(TelephonyManager.EXTRA_STATE);
                    if (state.equals(TelephonyManager.EXTRA_STATE_RINGING) && ServiceIncomingCall.isFlash == false) {
                        ServiceIncomingCall.isFlash = true;
//                    String phoneNumber = extras
//                            .getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                        if (isVibrate == false) {
                            Log.d("TAGG", "ANdroid MM");
                            flashLightModePhone(context, onTime, offTime);
                        } else {
                            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                            flashLightModePhone(context, onTime, offTime, v);
                        }
                    }

                    if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        ServiceIncomingCall.isFlash = false;
                    }

                    if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                        ServiceIncomingCall.isFlash = false;
                    }
                }
            }

            if (intent.getAction().
                    equals("android.provider.Telephony.SMS_RECEIVED") && isMessage == true
                    && ServiceIncomingCall.isFlash == false) {
                ServiceIncomingCall.isFlash = true;
                if (isVibrate == false) {
                    Log.d("TAGGG", "message");
                    flashLightMode(context, onTime, offTime, runTime);
                } else {
                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    flashLightMode(context, onTime, offTime, runTime, v);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void flashLightModePhone(final Context context, final int onTime, final int offTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                    while (ServiceIncomingCall.isFlash == true) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                String cameraId = cameraManager.getCameraIdList()[0];
                                cameraManager.setTorchMode(cameraId, true);
                                Thread.sleep(onTime);
                                cameraManager.setTorchMode(cameraId, false);
                                Thread.sleep(offTime);
                            }
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        } else {
            Log.d("TAGG", "ANdroid M");
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (ServiceIncomingCall.isFlash == true) {
                        try {
                            ServiceIncomingCall.p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            ServiceIncomingCall.camera.setParameters(ServiceIncomingCall.p);
                            ServiceIncomingCall.camera.startPreview();
                            Thread.sleep(onTime);
                            ServiceIncomingCall.p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            ServiceIncomingCall.camera.setParameters(ServiceIncomingCall.p);
                            ServiceIncomingCall.camera.stopPreview();
                            Thread.sleep(offTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void flashLightModePhone(final Context context, final int onTime, final int offTime, final Vibrator v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                    while (ServiceIncomingCall.isFlash == true) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                String cameraId = cameraManager.getCameraIdList()[0];
                                cameraManager.setTorchMode(cameraId, true);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                                } else {
                                    v.vibrate(200);
                                }
                                Thread.sleep(onTime);
                                cameraManager.setTorchMode(cameraId, false);
                                Thread.sleep(offTime);
                            }
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        } else {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (ServiceIncomingCall.isFlash == true) {
                        try {
                            ServiceIncomingCall.p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            ServiceIncomingCall.camera.setParameters(ServiceIncomingCall.p);
                            ServiceIncomingCall.camera.startPreview();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                v.vibrate(200);
                            }
                            Thread.sleep(onTime);
                            ServiceIncomingCall.p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            ServiceIncomingCall.camera.setParameters(ServiceIncomingCall.p);
                            ServiceIncomingCall.camera.stopPreview();
                            Thread.sleep(offTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void flashLightMode(Context context, final int onTime, final int offTime,
                                final int runTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("TAGG", "message2");
            cameraManager.initCameraFlash(context, runTime * 1000, onTime, offTime);
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
    private void flashLightMode(Context context, final int onTime, final int offTime,
                                final int runTime, final Vibrator v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraManager.initCameraFlash(context, runTime * 1000, onTime, offTime, v);
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
