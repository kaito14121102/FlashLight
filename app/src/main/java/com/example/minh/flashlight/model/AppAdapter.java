package com.example.minh.flashlight.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.minh.flashlight.R;

import java.util.ArrayList;

public class AppAdapter extends BaseAdapter {
    private ArrayList<App> mApps;
    private Context mContext;

    public AppAdapter(Context mContext) {
        this.mContext = mContext;
        mApps = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mApps.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void addData(ArrayList<App> apps) {
        mApps.addAll(apps);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_app, null);
            holder = new ViewHolder();
            holder.mTextName = (TextView) view.findViewById(R.id.text_name_app);
            holder.mImageApp = (ImageView) view.findViewById(R.id.image_app);
            holder.mCheck = (CheckBox) view.findViewById(R.id.check_isChooose);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final App app = mApps.get(i);
        holder.mTextName.setText(app.getName());
        holder.mImageApp.setImageDrawable(app.getImage());
        holder.mCheck.setChecked(app.getChoose());
        holder.mCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (app.getChoose() == false)
                    mApps.get(i).setChoose(true);
                else
                    mApps.get(i).setChoose(false);
            }
        });
        return view;
    }

    public class ViewHolder {
        ImageView mImageApp;
        TextView mTextName;
        CheckBox mCheck;

    }
}
