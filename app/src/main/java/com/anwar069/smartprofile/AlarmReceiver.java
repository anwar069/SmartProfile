package com.anwar069.smartprofile;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.anwar069.smartprofile.model.Alarm;

import java.util.Calendar;

/**
 * Created by Ahmed on 08/11/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long alarmId = intent.getLongExtra(Alarm.COL_ID, -1);
        AudioManager profileCheck;
        String[] days = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};

        Alarm alarm = new Alarm(alarmId);
        alarm.load(SmartProfile.db);
        profileCheck = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        String sound_mode = alarm.getSound_mode();
        String daysRepeat = alarm.getRepeat_on();

        Calendar calendar = Calendar.getInstance();
        String today = days[calendar.get(Calendar.DAY_OF_WEEK) - 1];

        Log.d("Receiver", "Sound Mode" + sound_mode + "Alarm ID: " + alarmId + "today is " + today);

        if(daysRepeat.equals(""))
        {
            ShowNotification(context, alarmId, profileCheck, alarm, sound_mode);
            SmartProfile.dbHelper.disableNotification(SmartProfile.db, alarmId);
            Intent disableThis = new Intent(context, AlarmService.class);
            disableThis.putExtra(Alarm.COL_ID, String.valueOf(alarmId));
            disableThis.setAction(AlarmService.CANCEL);
            context.startService(disableThis);
        }
        else if (daysRepeat.contains(today)) {
            ShowNotification(context, alarmId, profileCheck, alarm, sound_mode);

        } else {
            Log.d("Receiver", "Skipped" + "Repeat Days " + daysRepeat + "today is " + today);
        }
    }

    private void ShowNotification(Context context, long alarmId, AudioManager profileCheck, Alarm alarm, String sound_mode) {
        PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(), 0);

        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notify_change)
                        .setContentTitle("Smart Profile: " + alarm.getName())
                        .setContentText("Profile Changed to " + sound_mode);

        if (sound_mode.equals("General")) {
            profileCheck
                    .setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
        if (sound_mode.equals("Vibrate")) {
            profileCheck
                    .setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }
        if (sound_mode.equals("Silent")) {
            profileCheck
                    .setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }

        Log.d("Receiver ", " Received alarm Msg " + alarmId + " profile changed to " + sound_mode);

        mBuilder.setContentIntent(pi);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }
}
