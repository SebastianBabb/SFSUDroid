package woverines.sfsuapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Alerts Table contains instructions to create this particular schema. Contains many queries to
 * perform calls to database to insert, update, delete, etc.
 *
 * @author Gary Ng
 */
public class ALERTS_TABLE {

    public static final String TABLE_ALERTS = "alerts";
    public static final String COLUMN_ALERTS_ID = "_alerts_Id";
    public static final String COLUMN_COURSE_ID = "_course_Id";
    public static final String COLUMN_TEXT = "_text";
    public static final String COLUMN_TIME = "_time";
    public static final String COLUMN_REMINDER = "_reminder";
    public static final String COLUMN_SOUND = "_sound";
    public static final String COLUMN_VIBRATE = "_vibrate";
    public static final String COLUMN_REPEAT = "_repeat";

    public static final String CREATE_QUERY = "CREATE TABLE " +
        TABLE_ALERTS +
        "(" +
        COLUMN_ALERTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COLUMN_COURSE_ID + " INTEGER, " +
        COLUMN_TEXT + " TEXT, " +
        COLUMN_TIME + " INTEGER, " +
        COLUMN_REMINDER + " INTEGER, " +
        COLUMN_SOUND + " TEXT, " +
        COLUMN_VIBRATE + " INTEGER, " +
        COLUMN_REPEAT + " INTEGER" +
        ");";

    public static final String[] PROJECTION = new String[]{
        COLUMN_ALERTS_ID,
        COLUMN_COURSE_ID,
        COLUMN_TEXT,
        COLUMN_TIME,
        COLUMN_REMINDER,
        COLUMN_SOUND,
        COLUMN_VIBRATE,
        COLUMN_REPEAT
    };

    public static final int INDEX_ALERTS_ID = 0;
    public static final int INDEX_COURSE_ID = 1;
    public static final int INDEX_TEXT = 2;
    public static final int INDEX_TIME = 3;
    public static final int INDEX_REMINDER = 4;
    public static final int INDEX_SOUND = 5;
    public static final int INDEX_VIBRATE = 6;
    public static final int INDEX_REPEAT = 7;

    /**
     * Insert an alert into database
     *
     * @param context contains Android context
     * @param alert contains Alert values
     * @return id
     */
    public static long createAlert(Context context, Alert alert) {
        long id;

        SQLiteDatabase database = new DBHandler(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_COURSE_ID, alert.courseId);
        values.put(COLUMN_TEXT, alert.alertText);
        values.put(COLUMN_TIME, alert.time);
        values.put(COLUMN_REMINDER, alert.reminder);
        values.put(COLUMN_SOUND, alert.sound != null ? alert.sound.toString() : null);
        values.put(COLUMN_VIBRATE, alert.vibrate ? 1 : 0);
        values.put(COLUMN_REPEAT, alert.repeat ? 1 : 0);

        id = database.insert(TABLE_ALERTS, null, values);

        database.close();

        return id;
    }

    /**
     * Retrieve a single alert by ID
     *
     * @param context contains Android context
     * @param id contains alert ID
     * @return alert
     */
    public static Alert getAlert(Context context, long id) {
        Alert alert = null;

        SQLiteDatabase database = new DBHandler(context).getWritableDatabase();

        Cursor cursor = database.query(
            TABLE_ALERTS,
            PROJECTION,
            COLUMN_ALERTS_ID + " = ?",
            new String[]{
                String.valueOf(id)
            },
            null,
            null,
            null
        );

        if (cursor.moveToNext()) {
            alert = cursorToAlert(cursor);
        }

        cursor.close();
        database.close();

        return alert;
    }

    /**
     * Retrieve alerts after a specified time
     *
     * @param context contains Android context
     * @param startTime contains time in milliseconds
     * @return list of alerts
     */
    public static List<Alert> getAlerts(Context context, long startTime) {
        checkRepeats(context);

        List<Alert> alerts = new ArrayList<>();

        SQLiteDatabase database = new DBHandler(context).getWritableDatabase();

        Cursor cursor = database.query(
            TABLE_ALERTS,
            PROJECTION,
            COLUMN_TIME + " >= ?",
            new String[]{
                String.valueOf(startTime)
            },
            null,
            null,
            COLUMN_TIME + " DESC"
        );

        while (cursor.moveToNext()) {
            Alert alert = cursorToAlert(cursor);
            alerts.add(alert);
        }

        cursor.close();
        database.close();

        return alerts;
    }

    /**
     * Responsible for adjusting the time of previous alerts set on repeat to next week
     *
     * @param context contains Android context
     */
    public static void checkRepeats(Context context) {
        SQLiteDatabase database = new DBHandler(context).getWritableDatabase();

        Cursor cursor = database.query(
            TABLE_ALERTS,
            PROJECTION,
            COLUMN_TIME + " < STRFTIME('%s', 'NOW') * 1000 AND " + COLUMN_REPEAT + " > 0",
            null,
            null,
            null,
            COLUMN_TIME + " DESC"
        );

        Calendar calendar = Calendar.getInstance();

        while (cursor.moveToNext()) {
            Alert alert = cursorToAlert(cursor);

            calendar.setTimeInMillis(alert.time);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);

            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, second);

            ContentValues values = new ContentValues();
            values.put(COLUMN_TIME, calendar.getTimeInMillis());

            database.update(
                TABLE_ALERTS,
                values,
                COLUMN_ALERTS_ID + " = ?",
                new String[]{
                    String.valueOf(alert.id)
                }
            );
        }

        cursor.close();
        database.close();
    }

    /**
     * Retrieve alerts after a specified time for a specific course
     *
     * @param context contains Android context
     * @param courseId contains course ID
     * @param startTime contains time in milliseconds
     * @return list of alerts
     */
    public static List<Alert> getAlerts(Context context, int courseId, long startTime) {
        checkRepeats(context);

        List<Alert> alerts = new ArrayList<>();

        SQLiteDatabase database = new DBHandler(context).getWritableDatabase();

        Cursor cursor = database.query(
            TABLE_ALERTS,
            PROJECTION,
            COLUMN_COURSE_ID + " = ? AND " + COLUMN_TIME + " >= ?",
            new String[]{
                String.valueOf(courseId),
                String.valueOf(startTime)
            },
            null,
            null,
            COLUMN_TIME + " DESC"
        );

        while (cursor.moveToNext()) {
            Alert alert = cursorToAlert(cursor);
            alerts.add(alert);
        }

        cursor.close();
        database.close();

        return alerts;
    }

    /**
     * Update a single alert
     *
     * @param context contains Android context
     * @param alert contains Alert values
     * @return number of rows affected
     */
    public static int updateAlert(Context context, Alert alert) {
        int result;

        SQLiteDatabase database = new DBHandler(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_COURSE_ID, alert.courseId);
        values.put(COLUMN_TEXT, alert.alertText);
        values.put(COLUMN_TIME, alert.time);
        values.put(COLUMN_REMINDER, alert.reminder);
        values.put(COLUMN_SOUND, alert.sound != null ? alert.sound.toString() : null);
        values.put(COLUMN_VIBRATE, alert.vibrate ? 1 : 0);
        values.put(COLUMN_REPEAT, alert.repeat ? 1 : 0);

        result = database.update(
            TABLE_ALERTS,
            values,
            COLUMN_ALERTS_ID + " = ?",
            new String[]{
                String.valueOf(alert.id)
            }
        );

        database.close();

        return result;
    }

    /**
     * Remove a single alert
     *
     * @param context contains Android context
     * @param id contains alert ID
     * @return number of rows affected
     */
    public static int removeAlert(Context context, long id) {
        int result;

        SQLiteDatabase database = new DBHandler(context).getWritableDatabase();

        result = database.delete(
            TABLE_ALERTS,
            COLUMN_ALERTS_ID + " = ?",
            new String[]{
                String.valueOf(id)
            }
        );

        database.close();

        return result;
    }

    /**
     * Instantiates an alert from the given cursor generated by a specific projection
     *
     * @param cursor contains the cursor to the results
     * @return alert
     */
    private static Alert cursorToAlert(Cursor cursor) {
        Alert alert = new Alert(cursor.getLong(INDEX_ALERTS_ID));
        alert.courseId = cursor.getInt(INDEX_COURSE_ID);
        alert.time = cursor.getLong(INDEX_TIME);
        alert.alertText = cursor.getString(INDEX_TEXT);
        alert.reminder = cursor.getInt(INDEX_REMINDER);

        String sound = cursor.getString(INDEX_SOUND);
        alert.sound = sound != null ? Uri.parse(sound) : null;

        alert.vibrate = cursor.getInt(INDEX_VIBRATE) > 0;
        alert.repeat = cursor.getInt(INDEX_REPEAT) > 0;

        return alert;
    }
}
