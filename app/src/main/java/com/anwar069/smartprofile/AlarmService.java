package com.anwar069.smartprofile;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.anwar069.smartprofile.model.Alarm;
import com.anwar069.smartprofile.model.AlarmTime;

import java.util.Calendar;

/**
 * Created by Ahmed on 11/11/2015.
 */
public class AlarmService extends IntentService {
    private static final String TAG = "AlarmService";

    public static final String POPULATE = "POPULATE";
    public static final String CREATE = "CREATE";
    public static final String CANCEL = "CANCEL";


    private IntentFilter matcher;

    public AlarmService() {
        super(TAG);
        matcher = new IntentFilter();
        matcher.addAction(POPULATE);
        matcher.addAction(CREATE);
        matcher.addAction(CANCEL);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String action = intent.getAction();
        String alarmId = intent.getStringExtra(Alarm.COL_ID);
        String startTime = intent.getStringExtra(Alarm.COL_FROMDATE);
        String endTime = intent.getStringExtra(Alarm.COL_TODATE);
        Log.d("Service", "Handling  :-)" + action + "Alarm ID: " + alarmId);
        if (matcher.matchAction(action)) {
            if (POPULATE.equals(action)) {
                execute(CREATE, alarmId);
            }

            if (CREATE.equals(action)) {
                execute(CREATE, alarmId, startTime, endTime);
            }

            if (CANCEL.equals(action)) {
                execute(CANCEL, alarmId, startTime, endTime);
                SmartProfile.dbHelper.shred(SmartProfile.db);
            }

        }
    }

    /**
     * @param action
     * @param args   {alarmId, alarmMsgId, startTime, endTime}
     */
    private void execute(String action, String... args) {
        Intent i;
        PendingIntent pi;
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Cursor c;

        Log.d("Service", "Executing  :-)" + action);
        String alarmId = (args != null && args.length > 0) ? args[0] : null;
        String startTime = (args != null && args.length > 1) ? args[1] : null;
        String endTime = (args != null && args.length > 2) ? args[2] : null;

        Log.d("Service", "Executing  :-)" + action + "Alarm ID: " + alarmId);

        String status = CANCEL.equals(action) ? Alarm.DISABLED : Alarm.ACTIVE;
        c = Alarm.list(SmartProfile.db, alarmId, startTime, endTime, status);


        Log.d("Service",c.getCount()+ " Alarm found by ID " + alarmId );

        if (c.moveToFirst()) {
            long now = System.currentTimeMillis();
            long time, diff;
            do {
                i = new Intent(this, AlarmReceiver.class);
                long alarmID = c.getLong(c.getColumnIndex(Alarm.COL_ID));
                i.putExtra(Alarm.COL_ID, alarmID);
//                i.setData(Uri.parse("timer:" + alarmId));
                Log.d("Service", "Intent " + alarmID);
                pi = PendingIntent.getBroadcast(this, (1101 +(int)alarmID), i, PendingIntent.FLAG_UPDATE_CURRENT);
//				pi = PendingIntent.getService(context, requestCode, intent, flags);

                Calendar cal = Calendar.getInstance();
                AlarmTime alarmTime = new AlarmTime();
                alarmTime.setId(alarmID);
                alarmTime.load(SmartProfile.db);

                String[] tokens = alarmTime.getAt().split(":"); //hh:mm

                cal.setTimeInMillis(System.currentTimeMillis());
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tokens[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(tokens[1]));
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);



                Log.d("Service", "Pending Intent Request code " + (alarmID + 1101));
                Log.d("Service", "Creating Alarm at " + cal.getTimeInMillis() + " Millisecs and Date " + cal.getTime().toString());
//                time = c.getLong(c.getColumnIndex(AlarmMsg.COL_DATETIME));
                time = cal.getTimeInMillis();
                diff = time - now + (long) Util.MIN;
                if (CREATE.equals(action)) {
                    Log.d("Service", "Time Difference of " + time + " and " + (now + (long) Util.MIN) + "= " + diff);
                    if (diff < 0)
                        time += AlarmManager.INTERVAL_DAY;
//                    am.set(AlarmManager.RTC_WAKEUP, time, pi);
                    am.setRepeating(AlarmManager.RTC_WAKEUP,time,AlarmManager.INTERVAL_DAY,pi);
                    Log.d("Service", "Alarm Scheduled at " + time + "Alarm ID: " + alarmID);
                    Log.d("Service", "Intent " + pi.getIntentSender().toString());
                } else if (CANCEL.equals(action)) {
                    am.cancel(pi);
                    pi.cancel();
                    Log.d("Service", "Alarm Cancelled " + time + "Alarm ID: " + alarmID);
                }
            } while (c.moveToNext());
        }
        c.close();
    }

}
