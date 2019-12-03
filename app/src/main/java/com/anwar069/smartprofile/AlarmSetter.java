package com.anwar069.smartprofile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Ahmed on 14/11/2015.
 */
public class AlarmSetter extends BroadcastReceiver {

//	private static final String TAG = "AlarmSetter";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, AlarmService.class);
        Toast.makeText(context,"Boot Completed",Toast.LENGTH_SHORT).show();
        service.setAction(AlarmService.CREATE);
        context.startService(service);
    }

}

