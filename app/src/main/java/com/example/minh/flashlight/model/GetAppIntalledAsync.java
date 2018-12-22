package com.example.minh.flashlight.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.example.minh.flashlight.dialogCustom.ChooseAppDialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GetAppIntalledAsync extends AsyncTask<Void, Void, ArrayList<App>> {
    private Context mContext;
    private ChooseAppDialog dialog;

    public GetAppIntalledAsync(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ChooseAppDialog(mContext);
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected ArrayList<App> doInBackground(Void... voids) {
        return getAllApp();
    }

    @Override
    protected void onPostExecute(ArrayList<App> apps) {
        super.onPostExecute(apps);
        dialog.updateData(apps);
    }

    private ArrayList<App> getAllApp() {
        ArrayList<App> apps = new ArrayList<>();
        SharedPreferences sharedPreferences = null;
        sharedPreferences = mContext.getSharedPreferences("FLASH_LIGHT", Context.MODE_PRIVATE);
        PackageManager packageManager = mContext.getPackageManager();
        List<ApplicationInfo> applist = packageManager.getInstalledApplications(PackageManager.PERMISSION_GRANTED);
        Iterator<ApplicationInfo> it = applist.iterator();
        while (it.hasNext()) {

            ApplicationInfo pk = (ApplicationInfo) it.next();

            if ((pk.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1) {
                // updated system apps

            } else if ((pk.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                // system apps

            } else {
                // user installed apps
                String appname = packageManager.getApplicationLabel(pk).toString();
                String pkg = pk.packageName;//your package name
                Drawable icon = null;
                try {
                    icon = mContext.getPackageManager().getApplicationIcon(pkg);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    Log.d("TAGG", "oki");
                }
                boolean isChoose = sharedPreferences.getBoolean(pkg, false);
                App app = new App(appname, icon, isChoose, pkg);
                apps.add(app);
            }
        }
        return apps;
    }
}
