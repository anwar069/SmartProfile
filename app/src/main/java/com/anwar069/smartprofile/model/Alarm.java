package com.anwar069.smartprofile.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.anwar069.smartprofile.Util;

import java.util.List;

/**
 * Created by Ahmed on 11/11/2015.
 */
public class Alarm extends AbstractModel{
    public static final String TABLE_NAME = "alarm";
    public static final String COL_ID = AbstractModel.COL_ID;
    public static final String COL_CREATEDTIME = "created_time";
    public static final String COL_MODIFIEDTIME = "modified_time";
    public static final String COL_NAME = "name";
    public static final String COL_FROMDATE = "from_date";
    public static final String COL_TODATE = "to_date";
    public static final String COL_RULE = "rule";
    public static final String COL_INTERVAL = "interval";
    public static final String COL_REPEATON = "repeat_on";
    public static final String COL_AT = "at";
    public static final String COL_STATUS = "status";
    public static final String COL_SOUND_MODE = "sound_mode";

    public static final String HIGH = "H";
    public static final String MED = "M";
    public static final String LOW = "L";

    public static final String ACTIVE = "A";
    public static final String CANCELLED = "C";
    public static final String DISABLED = "D";

    static String getSql() {
        return Util.concat("CREATE TABLE ", TABLE_NAME, " (",
                AbstractModel.getSql(),
                COL_CREATEDTIME, " INTEGER, ",
                COL_MODIFIEDTIME, " INTEGER, ",
                COL_NAME, " TEXT, ",
                COL_FROMDATE, " DATE, ",
                COL_TODATE, " DATE, ",
                COL_REPEATON, " TEXT, ",
                COL_RULE, " TEXT, ",
                COL_INTERVAL, " TEXT, ",
                COL_AT, " TEXT, ",
                COL_STATUS, " TEXT,",
                COL_SOUND_MODE, " TEXT",
                ");");
    }
    /**
     * @param db
     * @param args {alarmId, startTime, endTime, status, orderBy}
     * @return cursor
     */
    public static Cursor list(SQLiteDatabase db, String... args) {
        String[] columns = {COL_ID, COL_STATUS};
        String selection = "1 = 1";
        selection += (args!=null && args.length>0 && args[0]!=null) ? " AND "+COL_ID+" = "+args[0] : "";
        selection += (args!=null && args.length>3 && args[3]!=null) ? " AND "+COL_STATUS+" = '"+args[3]+"'" : "";
        String orderBy = (args!=null && args.length>4) ? args[4] : COL_ID+" DESC";

        return db.query(TABLE_NAME, columns, selection, null, null, null, orderBy);
    }

    long save(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        long now = System.currentTimeMillis();
        cv.put(COL_CREATEDTIME, now);
        cv.put(COL_MODIFIEDTIME, now);
        cv.put(COL_NAME, name==null ? "" : name);
        cv.put(COL_FROMDATE, fromDate);
        cv.put(COL_TODATE, toDate);
        cv.put(COL_REPEATON, repeat_on);
        cv.put(COL_SOUND_MODE, sound_mode);
        cv.put(COL_AT, at);
        cv.put(COL_STATUS,status);
        return db.insert(TABLE_NAME, null, cv);
    }

    boolean update(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        super.update(cv);
        cv.put(COL_MODIFIEDTIME, System.currentTimeMillis());
        if (name != null)
            cv.put(COL_NAME, name);
        if (fromDate != null)
            cv.put(COL_FROMDATE, fromDate);
        if (toDate != null)
            cv.put(COL_TODATE, toDate);
        if (repeat_on != null)
            cv.put(COL_REPEATON, repeat_on);
        if (sound_mode != null)
            cv.put(COL_SOUND_MODE, sound_mode );
        if (sound_mode != null)
            cv.put(COL_AT, at);
        if (status != null)
            cv.put(COL_STATUS, status);
        return db.update(TABLE_NAME, cv, COL_ID + " = ?", new String[]{String.valueOf(id)})
                == 1;
    }

    public boolean load(SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_NAME, null, COL_ID+" = ?", new String[]{String.valueOf(id)}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                reset();
                super.load(cursor);
                createdTime = cursor.getLong(cursor.getColumnIndex(COL_CREATEDTIME));
                modifiedTime = cursor.getLong(cursor.getColumnIndex(COL_MODIFIEDTIME));
                name = cursor.getString(cursor.getColumnIndex(COL_NAME));
                fromDate = cursor.getString(cursor.getColumnIndex(COL_FROMDATE));
                toDate = cursor.getString(cursor.getColumnIndex(COL_TODATE));
                repeat_on = cursor.getString(cursor.getColumnIndex(COL_REPEATON));
                sound_mode = cursor.getString(cursor.getColumnIndex(COL_SOUND_MODE));
                status = cursor.getString(cursor.getColumnIndex(COL_STATUS));
                at=cursor.getString(cursor.getColumnIndex(COL_AT));
                return true;
            }
            return false;
        } finally {
            cursor.close();
        }
    }

    public static Cursor list(SQLiteDatabase db) {
        String[] columns = {COL_ID, COL_NAME};

        return db.query(TABLE_NAME, columns, null, null, null, null, COL_CREATEDTIME+" DESC");
    }

    public boolean delete(SQLiteDatabase db) {
        boolean status = false;
        String[] whereArgs = new String[]{String.valueOf(id)};

        db.beginTransaction();
        try {
            db.delete(AlarmTime.TABLE_NAME, AlarmTime.COL_ALARMID+" = ?", whereArgs);
            status = db.delete(TABLE_NAME, COL_ID + " = ?", whereArgs)
                    == 1;
            db.setTransactionSuccessful();
        } catch (Exception e) {
        } finally {
            db.endTransaction();
        }
        return status;
    }

    //--------------------------------------------------------------------------

    private long createdTime;
    private long modifiedTime;
    private String name;
    private String fromDate;
    private String toDate;
    private String rule;
    private String interval;
    private String repeat_on;
    private String sound_mode;
    private String at;
    private String status;
    private List<AlarmTime> occurrences;


    public void reset() {
        super.reset();
        createdTime = 0;
        modifiedTime = 0;
        name = null;
        fromDate = null;
        toDate = null;
        rule = null;
        repeat_on = null;
        sound_mode = null;
        at=null;
        status=null;
        occurrences = null;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public long getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
    public long getModifiedTime() {
        return modifiedTime;
    }
    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getFromDate() {
        return fromDate;
    }
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }
    public String getToDate() {
        return toDate;
    }
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
    public String getRule() {
        return rule;
    }
    public void setRule(String rule) {
        this.rule = rule;
    }
    public String getRepeat_on() {
        return repeat_on;
    }
    public void setRepeat_on(String repeat_on) {
        this.repeat_on = repeat_on;
    }
    public String getInterval() {
        return interval;
    }
    public void setInterval(String interval) {
        this.interval = interval;
    }
    public String getSound_mode() {
        return sound_mode;
    }
    public void setSound_mode(String sound_mode) {
        this.sound_mode = sound_mode;
    }
    public List<AlarmTime> getOccurrences() {
        return occurrences;
    }
    public void setOccurrences(List<AlarmTime> occurrences) {
        this.occurrences = occurrences;
    }

    public Alarm() {}

    public Alarm(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || !((obj == null) || (obj.getClass() != this.getClass())) && id == ((Alarm) obj).id;

    }

    @Override
    public int hashCode() {
        return 1;
    }
}
