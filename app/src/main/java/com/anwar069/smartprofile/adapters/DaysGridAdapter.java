package com.anwar069.smartprofile.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.GridView;
import android.widget.TextView;

import com.anwar069.smartprofile.R;

import extensions.CheckableTextview;
import util.UiUtils;

/**
 * Created by Ahmed on 24/10/2015.
 */
public class DaysGridAdapter extends BaseAdapter {
    Context mContext;
    String[] days= {"SUN","MON","TUE","WED","THU","FRI","SAT"};
    String Selected;


    public DaysGridAdapter(Context context,String Selected) {
        mContext=context;
        this.Selected=Selected;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final CheckableTextview text;
        String day = days[position];

        if (convertView == null) {
            text = new CheckableTextview(mContext);
            text.setText(day);
            text.setGravity(Gravity.CENTER);
            text.setTextSize(UiUtils.getSIP(mContext, 7));
            text.setLayoutParams(new GridView.LayoutParams(UiUtils.getDIP(mContext, 70),
                    UiUtils.getDIP(mContext, 50)));
           text.setBackgroundResource(R.drawable.days_unchecked);
        } else {
            text = (CheckableTextview) convertView;
        }

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.toggle();
                Animation animation1 = AnimationUtils.loadAnimation(mContext, R.anim.blink);
                text.startAnimation(animation1);
            }
        });
//        ResolveInfo info = mApps.get(position);
//        text.setImageDrawable(info.activityInfo.loadIcon(getPackageManager()));

        if(Selected.contains(day))
            text.setChecked(true);
        return text;
    }


    public final int getCount() {
        return days.length;
    }

    public final Object getItem(int position) {
        return days[position];
    }

    public final long getItemId(int position) {
        return position;
    }



}

