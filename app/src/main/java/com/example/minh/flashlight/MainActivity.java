package com.example.minh.flashlight;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.minh.flashlight.model.GetAppIntalledAsync;
import com.example.minh.flashlight.myService.NotificationListener;
import com.example.minh.flashlight.myService.ServiceIncomingCall;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Switch mSwitchFlash, mSwitchMessage, mSwitchVibrate, mSwitchPhone;
    private TextView mTextOnOff;
    public static Switch mSwitchNotify;
    private LinearLayout mLinearPhone, mLinearMesage, mLinearNotify, mLinearVibrate;
    private boolean hasCameraFlash = false;
    public static boolean isShown = false;
    private Button mButtonSpeed;
    private Button mButtonChooseApp;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget();
        FlashLisghtStatus();
        permissionNotification();
//        permissionPhoneCallMessage();
        evenSwitch();
        addAutoStartup();
    }

    private void permissionNotification() {
        Intent intent = new Intent(this, NotificationListener.class);
        startService(intent);
    }

    private void addAutoStartup() {
        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e("exc", String.valueOf(e));
        }
    }

    private void FlashLisghtStatus() {
        hasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private void evenSwitch() {
        mSwitchFlash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("ON_OFF", b);
                editor.commit();
                if (b == false) {
                    mTextOnOff.setText(R.string.bat_tinh_nang);
                    offSwitch();
                } else {
                    mTextOnOff.setText(R.string.tat_tinh_nang);
                    onSwitch();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void flashLightTry(final int onTime, final int offTime, int runTime) {
        int ontime = onTime + 50;
        int offtime = offTime + 50;
        final int runtime = (runTime + 2) * 1000;
        MyCameraManager cameraManager = new MyCameraManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage(getString(R.string.flash_dang_hoat_dong));
            dialog.show();
            Thread thread = new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void run() {
//                    MainActivity.mSwitchFlash.setEnabled(false);
                    CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                    long startTime = System.currentTimeMillis();
                    while (System.currentTimeMillis() - startTime < runtime) {
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
                    dialog.dismiss();
                    ServiceIncomingCall.isFlash = false;
                }
            });
            thread.start();
        } else {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Wait! Flash is working");
//            dialog.setIndeterminate(true);
            dialog.show();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    long startTime = System.currentTimeMillis();
                    while (System.currentTimeMillis() - startTime < runtime) {
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
                    dialog.dismiss();
                    ServiceIncomingCall.isFlash = false;
                }
            });
            thread.start();
        }
    }

    private void initWidget() {
        ImageView iv = findViewById(R.id.iv_bg);
        Glide.with(this).load(R.drawable.bg).into(iv);
        Intent intentService = new Intent(MainActivity.this, ServiceIncomingCall.class);
        startService(intentService);
        mTextOnOff = findViewById(R.id.text_on_off);
        mSwitchFlash = findViewById(R.id.switch_flash);
        mSwitchPhone = findViewById(R.id.switch_phone);
        mSwitchPhone.setOnClickListener(this);
        mSwitchMessage = findViewById(R.id.switch_message);
        mSwitchMessage.setOnClickListener(this);
        mSwitchNotify = findViewById(R.id.switch_notifi);
        mSwitchNotify.setOnClickListener(this);
        mSwitchVibrate = findViewById(R.id.switch_vibrate);
        mSwitchVibrate.setOnClickListener(this);
        mButtonSpeed = findViewById(R.id.button_choose_speed);
        mButtonSpeed.setOnClickListener(this);
        mButtonChooseApp = findViewById(R.id.button_choose_app);
        mButtonChooseApp.setOnClickListener(this);

        mLinearPhone = findViewById(R.id.linear_phone);
        mLinearPhone.setOnClickListener(this);
        mLinearMesage = findViewById(R.id.linear_message);
        mLinearMesage.setOnClickListener(this);
        mLinearNotify = findViewById(R.id.linear_notify);
        mLinearNotify.setOnClickListener(this);
        mLinearVibrate = findViewById(R.id.linear_vibrate);
        mLinearVibrate.setOnClickListener(this);
        switchInit();
    }

    private void switchInit() {
        sharedPreferences = getSharedPreferences("FLASH_LIGHT", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        boolean isOnOFF = sharedPreferences.getBoolean("ON_OFF", false);
        mSwitchFlash.setChecked(isOnOFF);
        if (isOnOFF == true) {
            mTextOnOff.setText(R.string.tat_tinh_nang);
            onSwitch();
        } else {
            mTextOnOff.setText(R.string.bat_tinh_nang);
            offSwitch();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 20) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                mSwitchPhone.setChecked(false);
            } else {
                editor.putBoolean("IS_PHONE", true);
                editor.commit();
                mSwitchPhone.setChecked(true);
            }
        }

        if (requestCode == 22) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                mSwitchMessage.setChecked(false);
            } else {
                editor.putBoolean("IS_MESSAGE", true);
                editor.commit();
                mSwitchMessage.setChecked(true);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_choose_speed:
                if (isShown == false) {
                    isShown = true;
                    int onTime = sharedPreferences.getInt("ON_TIME", 50);
                    int offTime = sharedPreferences.getInt("OFF_TIME", 50);
                    int runTime = sharedPreferences.getInt("RUN_TIME", 2);
                    speedDialog(onTime, offTime, runTime);
                }
                break;
            case R.id.button_choose_app:
                if (isShown == false) {
                    isShown = true;
                    chooseAppDialog();
                }
                break;
            case R.id.linear_phone:
                phoneSwitchEven();
                break;
            case R.id.linear_message:
                messageSwitchEven();
                break;
            case R.id.linear_notify:
                notifySwitchEven();
                break;
            case R.id.linear_vibrate:
                vibarateSwitchEven();
                break;
            case R.id.switch_phone:
                phoneSwitchEven();
                break;
            case R.id.switch_message:
                messageSwitchEven();
                break;
            case R.id.switch_notifi:
                notifySwitchEven();
                break;
            case R.id.switch_vibrate:
                vibarateSwitchEven();
                break;
        }
    }

    private void chooseAppDialog() {
        GetAppIntalledAsync async = new GetAppIntalledAsync(this);
        async.execute();
    }

    public void speedDialog(int onTime, int offTime, int runTime) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_speed);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        final TextView textSpeedOn = dialog.findViewById(R.id.text_speed_on);
        final TextView textSpeedOff = dialog.findViewById(R.id.text_speed_off);
        final TextView textSpeedRun = dialog.findViewById(R.id.text_speed_run);
        final TextView textTry = dialog.findViewById(R.id.text_try);
        final TextView textCancel = dialog.findViewById(R.id.text_cancel);
        TextView textSave = dialog.findViewById(R.id.text_save);
        final SeekBar seekOn = dialog.findViewById(R.id.seek_bar_on);
        final SeekBar seekOff = dialog.findViewById(R.id.seek_bar_off);
        final SeekBar seekRun = dialog.findViewById(R.id.seek_bar_run);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        textSpeedOn.setText(onTime + " ms");
        textSpeedOff.setText(offTime + " ms");
        textSpeedRun.setText(runTime + " s");
        seekOn.setProgress(onTime - 50);
        seekOff.setProgress(offTime - 50);
        seekRun.setProgress(runTime - 2);
        textTry.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if (hasCameraFlash) {
                    if (ServiceIncomingCall.isFlash == false) {
                        ServiceIncomingCall.isFlash = true;
                        flashLightTry(seekOn.getProgress(), seekOff.getProgress(), seekRun.getProgress());
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Thiết bị không hỗ trợ flash", Toast.LENGTH_SHORT).show();
                }
            }
        });
        textSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putInt("ON_TIME", seekOn.getProgress() + 50);
                editor.putInt("OFF_TIME", seekOff.getProgress() + 50);
                editor.putInt("RUN_TIME", seekRun.getProgress() + 2);
                editor.commit();
                dialog.cancel();
                dialog.dismiss();
                isShown = false;
            }
        });

        textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                dialog.dismiss();
                isShown = false;
            }
        });
        seekOn.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textSpeedOn.setText((i + 50) + " ms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekOff.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textSpeedOff.setText((i + 50) + " ms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekRun.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textSpeedRun.setText((i + 2) + " s");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        dialog.show();
    }

    public void notifyDialogRequest() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_notify_request);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textDeny = dialog.findViewById(R.id.text_deny);
        TextView textAccept = dialog.findViewById(R.id.text_accept);
        textDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mSwitchNotify.setChecked(false);
            }
        });
        textAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mSwitchNotify.setChecked(false);
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
            }
        });
        dialog.show();
    }

    public void phoneSwitchEven() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        20);
            } else {
                boolean isPhone = sharedPreferences.getBoolean("IS_PHONE", false);
                if (isPhone == false) {
                    editor.putBoolean("IS_PHONE", true);
                    editor.commit();
                    mSwitchPhone.setChecked(true);
                } else {
                    editor.putBoolean("IS_PHONE", false);
                    editor.commit();
                    mSwitchPhone.setChecked(false);
                }
            }
        } else {
            boolean isPhone = sharedPreferences.getBoolean("IS_PHONE", false);
            if (isPhone == false) {
                editor.putBoolean("IS_PHONE", true);
                editor.commit();
                mSwitchPhone.setChecked(true);
            } else {
                editor.putBoolean("IS_PHONE", false);
                editor.commit();
                mSwitchPhone.setChecked(false);
            }
        }
    }

    public void messageSwitchEven() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECEIVE_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS},
                        22);
            } else {
                boolean isMessage = sharedPreferences.getBoolean("IS_MESSAGE", false);
                if (isMessage == false) {
                    editor.putBoolean("IS_MESSAGE", true);
                    editor.commit();
                    mSwitchMessage.setChecked(true);
                } else {
                    editor.putBoolean("IS_MESSAGE", false);
                    editor.commit();
                    mSwitchMessage.setChecked(false);
                }
            }
        } else {
            boolean isMessage = sharedPreferences.getBoolean("IS_MESSAGE", false);
            if (isMessage == false) {
                editor.putBoolean("IS_MESSAGE", true);
                editor.commit();
                mSwitchMessage.setChecked(true);
            } else {
                editor.putBoolean("IS_MESSAGE", false);
                editor.commit();
                mSwitchMessage.setChecked(false);
            }
        }
    }

    public void notifySwitchEven() {
        ComponentName cn = new ComponentName(MainActivity.this, NotificationListener.class);
        String flat = Settings.Secure.getString(MainActivity.this.getContentResolver(),
                "enabled_notification_listeners");
        final boolean enabled = flat != null && flat.contains(cn.flattenToString());
        if (enabled == false) {
            notifyDialogRequest();
        } else {
            boolean isNotify = sharedPreferences.getBoolean("IS_NOTIFY", false);
            if (isNotify == false) {
                editor.putBoolean("IS_NOTIFY", true);
                editor.commit();
                mSwitchNotify.setChecked(true);
            } else {
                editor.putBoolean("IS_NOTIFY", false);
                editor.commit();
                mSwitchNotify.setChecked(false);
            }
        }
    }

    public void vibarateSwitchEven() {
        boolean isVibrate = sharedPreferences.getBoolean("IS_VIBRATE", false);
        if (isVibrate == false) {
            editor.putBoolean("IS_VIBRATE", true);
            editor.commit();
            mSwitchVibrate.setChecked(true);
        } else {
            editor.putBoolean("IS_VIBRATE", false);
            editor.commit();
            mSwitchVibrate.setChecked(false);
        }
    }

    public void offSwitch() {
        mSwitchPhone.setChecked(false);
        mSwitchMessage.setChecked(false);
        mSwitchNotify.setChecked(false);
        mSwitchVibrate.setChecked(false);
        mSwitchPhone.setEnabled(false);
        mSwitchMessage.setEnabled(false);
        mSwitchNotify.setEnabled(false);
        mSwitchVibrate.setEnabled(false);
        mLinearPhone.setEnabled(false);
        mLinearMesage.setEnabled(false);
        mLinearNotify.setEnabled(false);
        mLinearVibrate.setEnabled(false);
    }

    public void onSwitch() {
        boolean isPhone = sharedPreferences.getBoolean("IS_PHONE", false);
        mSwitchPhone.setChecked(isPhone);
        boolean isMessage = sharedPreferences.getBoolean("IS_MESSAGE", false);
        mSwitchMessage.setChecked(isMessage);
        boolean isNotify = sharedPreferences.getBoolean("IS_NOTIFY", false);
        mSwitchNotify.setChecked(isNotify);
        boolean isVibarate = sharedPreferences.getBoolean("IS_VIBRATE", false);
        mSwitchVibrate.setChecked(isVibarate);
        mSwitchPhone.setEnabled(true);
        mSwitchMessage.setEnabled(true);
        mSwitchNotify.setEnabled(true);
        mSwitchVibrate.setEnabled(true);
        mLinearPhone.setEnabled(true);
        mLinearMesage.setEnabled(true);
        mLinearNotify.setEnabled(true);
        mLinearVibrate.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isNotify = sharedPreferences.getBoolean("IS_NOTIFY", false);
        mSwitchNotify.setChecked(isNotify);
    }
}
