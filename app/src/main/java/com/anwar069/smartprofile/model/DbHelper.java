package com.anwar069.smartprofile.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.anwar069.smartprofile.SmartProfile;
import com.anwar069.smartprofile.Util;

/**
 * Created by Ahmed on 11/11/2015.
 */
public class DbHelper extends SQLiteOpenHelper {

//	private static final String TAG = "DbHelper";

    public static final String DB_NAME = "smartprof.db";
    public static final int DB_VERSION = 1;


    public static final SimpleDateFormat sdf = new SimpleDateFormat(SmartProfile.DEFAULT_DATE_FORMAT);
    private static final String TAG = "DB HELPER";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Alarm.getSql());
        db.execSQL(AlarmTime.getSql());
//        db.execSQL(AlarmMsg.getSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Alarm.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AlarmTime.TABLE_NAME);
//        db.execSQL("DROP TABLE IF EXISTS " + AlarmMsg.TABLE_NAME);

        onCreate(db);
    }

    public void shred(SQLiteDatabase db) {
//        db.delete(Alarm.TABLE_NAME, Alarm.COL_ID + " IN (SELECT " + AlarmMsg.COL_ALARMID + " FROM " + AlarmMsg.TABLE_NAME + " WHERE " + AlarmMsg.COL_STATUS + " = '"
//                + AlarmMsg.CANCELLED + "')", null);
        db.delete(Alarm.TABLE_NAME, Alarm.COL_STATUS + " = ?", new String[]{Alarm.CANCELLED});
    }

    private final String populateSQL = Util.concat("SELECT ",
            "a."+Alarm.COL_FROMDATE+", ",
            "a."+Alarm.COL_TODATE+", ",
            "a."+Alarm.COL_RULE+", ",
            "a."+Alarm.COL_INTERVAL+", ",
            "at."+AlarmTime.COL_AT+", ",
            "a." + Alarm.COL_REPEATON ,
            " FROM " + Alarm.TABLE_NAME + " AS a",
            " JOIN " + AlarmTime.TABLE_NAME + " AS at",
            " ON a." + Alarm.COL_ID + " = at." + AlarmTime.COL_ALARMID,
            " WHERE a." + Alarm.COL_ID + " = ?");

//    public void populate(long alarmId, SQLiteDatabase db) {
//
//        String[] selectionArgs = {String.valueOf(alarmId)};
//        Cursor c = db.rawQuery(populateSQL, selectionArgs);
//
//        if (c.moveToFirst()) {
//            Calendar cal = Calendar.getInstance();
//            AlarmMsg alarmMsg = new AlarmMsg();
//            long now = System.currentTimeMillis();
//            db.beginTransaction();
//            try {
//                do {
//                    Date fromDate = sdf.parse(c.getString(0)); //yyyy-M-d
//                    cal.setTime(fromDate);
//
//                    //at
//                    String[] tokens = c.getString(4).split(":"); //hh:mm
//                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tokens[0]));
//                    cal.set(Calendar.MINUTE, Integer.parseInt(tokens[1]));
//                    cal.set(Calendar.SECOND, 0);
//                    cal.set(Calendar.MILLISECOND, 0);
//
//                    String rule = c.getString(2); //every day month
//                    String interval = c.getString(3); //mm hh dd MM yy
//
//                    if (rule == null && interval == null) {//one time
//                        alarmMsg.reset();
//                        alarmMsg.setAlarmId(alarmId);
//                        alarmMsg.setDateTime(cal.getTimeInMillis());
////                        if (alarmMsg.getDateTime() < now-Util.MIN)
////                            alarmMsg.setStatus(AlarmMsg.EXPIRED);
//                        alarmMsg.setStatus(AlarmMsg.ACTIVE);
//                        alarmMsg.save(db);
//
//                    } else {//repeating
//                        if (rule != null) {
//                            tokens = rule.split(" ");
//                            interval = "0 0 1 0 0"; //date++;
//
//                            //Day
//                            if (!"0".equals(tokens[1])) {
//                                cal.set(Calendar.DAY_OF_WEEK, Integer.parseInt(tokens[1]));
//                                interval = "0 0 7 0 0"; //week++;
//                            }
//                            //Every
//                            if (!"0".equals(tokens[0]) && "0".equals(tokens[1])) {
//                                cal.set(Calendar.DATE, Integer.parseInt(tokens[0]));
//                                interval = "0 0 0 1 0"; //month++;
//                            }
//                            //Month
//                            if (!"0".equals(tokens[2])) {
//                                cal.set(Calendar.MONTH, Integer.parseInt(tokens[2])-1);
//                                interval = "0 0 0 0 1"; //year++;
//                            }
//
//                            while(cal.getTime().before(fromDate)) {
//                                nextDate(cal, interval);
//                            }
//                        }
//
//                        Date toDate = sdf.parse(c.getString(1));
//                        toDate.setHours(0);
//                        toDate.setMinutes(0);
//                        toDate.setSeconds(0);
//                        toDate.setDate(toDate.getDate()+1);
////                        while(cal.getTime().before(toDate)) {
////                            alarmMsg.reset();
////                            alarmMsg.setAlarmId(alarmId);
////                            alarmMsg.setDateTime(cal.getTimeInMillis());
////                            if (alarmMsg.getDateTime() < now-Util.MIN)
////                                alarmMsg.setStatus(AlarmMsg.EXPIRED);
////                            alarmMsg.save(db);
////                            nextDate(cal, interval);
////                        }
//                    }
//
//                } while(c.moveToNext());
//
//                db.setTransactionSuccessful();
//            } catch (Exception e) {
//		    	Log.e(TAG, e.getMessage(), e);
//            } finally {
//                db.endTransaction();
//            }
//        }
//        c.close();
//    }

    private void nextDate(Calendar cal, String interval) {
        String[] tokens = interval.split(" ");
        cal.add(Calendar.MINUTE, Integer.parseInt(tokens[0]));
        cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(tokens[1]));
        cal.add(Calendar.DATE, Integer.parseInt(tokens[2]));
        cal.add(Calendar.MONTH, Integer.parseInt(tokens[3]));
        cal.add(Calendar.YEAR, Integer.parseInt(tokens[4]));
    }

    private final String listSQL = Util.concat("SELECT ",
            "a."+Alarm.COL_NAME+", ",
            "a."+Alarm.COL_ID+", ",
            "a."+Alarm.COL_REPEATON+", ",
            "a."+Alarm.COL_AT+", ",
            "a."+Alarm.COL_STATUS+", ",
            "a."+Alarm.COL_SOUND_MODE,
            " FROM "+Alarm.TABLE_NAME+" AS a",
//            " JOIN "+AlarmMsg.TABLE_NAME+" AS am",
//            " ON a."+Alarm.COL_ID+" = am."+AlarmMsg.COL_ALARMID,
            " JOIN "+AlarmTime.TABLE_NAME+" AS at",
            " ON a."+Alarm.COL_ID+" = at."+AlarmTime.COL_ALARMID);

    /**
     * @param db
     * @param args {startTime, endTime}
     * @return cursor
     */
    public Cursor listNotifications(SQLiteDatabase db, String... args) {
        String selection = "a."+Alarm.COL_STATUS+" != '"+Alarm.CANCELLED+"'";
//        selection += (args!=null && args.length>0 && args[0]!=null) ? " AND am."+AlarmMsg.COL_DATETIME+" >= "+args[0] : "";
//        selection += (args!=null && args.length>1 && args[1]!=null) ? " AND am."+AlarmMsg.COL_DATETIME+" <= "+args[1] : "";

        String sql = Util.concat(listSQL,
                " WHERE "+selection,
                " ORDER BY a."+Alarm.COL_CREATEDTIME+" DESC");

        return db.rawQuery(sql, null);
    }

    public ArrayList<String> getAllNames(SQLiteDatabase db) {
        String[] aColumn= {Alarm.COL_NAME};
        ArrayList<String> list = new ArrayList<String>();
        Cursor cursor = db.query(Alarm.TABLE_NAME, aColumn, null, null, null, null, null);
        if (cursor.moveToFirst())
        {
            do
            {
                list.add(cursor.getString(0));
            }
            while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed())
        {
            cursor.close();
        }

        return list;
    }
    public int cancelNotification(SQLiteDatabase db, long id) {
        ContentValues cv = new ContentValues();
        cv.put(Alarm.COL_STATUS, Alarm.CANCELLED);
        return db.update(Alarm.TABLE_NAME,
                cv,
                (false ? Alarm.COL_ID: Alarm.COL_ID) + " = ?",
                new String[]{String.valueOf(id)});
    }
    public int disableNotification(SQLiteDatabase db, long id) {
        ContentValues cv = new ContentValues();
        cv.put(Alarm.COL_STATUS, Alarm.DISABLED);
        return db.update(Alarm.TABLE_NAME,
                cv,
                (false ? Alarm.COL_ID: Alarm.COL_ID)+" = ?",
                new String[]{String.valueOf(id)});
    }
    public int enableNotification(SQLiteDatabase db, long id) {
        ContentValues cv = new ContentValues();
        cv.put(Alarm.COL_STATUS, Alarm.ACTIVE);
        return db.update(Alarm.TABLE_NAME,
                cv,
                (false ? Alarm.COL_ID: Alarm.COL_ID)+" = ?",
                new String[]{String.valueOf(id)});
    }
//    public int cancelNotification(SQLiteDatabase db, String startTime, String endTime) {
//        ContentValues cv = new ContentValues();
//        cv.put(AlarmMsg.COL_STATUS, AlarmMsg.CANCELLED);
//        return db.update(AlarmMsg.TABLE_NAME,
//                cv,
//                AlarmMsg.COL_DATETIME+" >= ? AND "+AlarmMsg.COL_DATETIME+" <= ?",
//                new String[]{startTime, endTime});
//    }

    public static final String getDateStr(int year, int month, int date) {
        return Util.concat(year, "-", month, "-", date);
    }

    public static final String getTimeStr(int hour, int minute) {
        return Util.concat(hour, ":", minute>9 ? "":"0", minute);
    }

}
