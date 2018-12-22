package com.example.minh.flashlight.dialogCustom;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.minh.flashlight.model.App;
import com.example.minh.flashlight.model.AppAdapter;
import com.example.minh.flashlight.MainActivity;
import com.example.minh.flashlight.R;

import java.util.ArrayList;

public class ChooseAppDialog extends Dialog {
    private Context mContext;
    private ArrayList<App> mApps;
    private AppAdapter mAdapter;
    private ProgressBar mProgressBar;

    public ChooseAppDialog(Context context) {
        super(context);
        mContext = context;
        mApps = new ArrayList<>();
        mAdapter = new AppAdapter(mContext);
    }

    public void updateData(ArrayList<App> apps) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mApps.addAll(apps);
        mAdapter.addData(mApps);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_choose_app);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        ListView listViewApp = findViewById(R.id.list_app);
        TextView textOk = findViewById(R.id.text_ok);
        TextView textCancel = findViewById(R.id.text_cancel);
        mProgressBar = findViewById(R.id.progressBar);
        listViewApp.setAdapter(mAdapter);
        textOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = mContext.getSharedPreferences("FLASH_LIGHT", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                for (int i = 0; i < mApps.size(); i++) {
                    editor.putBoolean(mApps.get(i).getPakageName(), mApps.get(i).getChoose());
                }
                editor.commit();
                cancel();
                dismiss();
                MainActivity.isShown = false;
            }
        });
        textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
                dismiss();
                MainActivity.isShown = false;

            }
        });
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        super.onCreate(savedInstanceState);
    }
}
