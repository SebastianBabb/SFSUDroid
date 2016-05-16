package woverines.sfsuapp.activity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import woverines.sfsuapp.R;
import woverines.sfsuapp.database.ALERTS_TABLE;
import woverines.sfsuapp.database.Alert;

/**
 * Alert Helper contains necessary information to create an alert as an alarm on the device as
 * well as serving methods to perform actions to database.
 *
 * @author Gary Ng
 */
public class AlertHelper {

    public static final long MINUTE_MS = 60000L;
    public static final long DAY_MS = 86400000L;
    public static final long WEEK_MS = 7 * DAY_MS;

    private static final SimpleDateFormat TIME_FORMAT =
        new SimpleDateFormat("h:mm a", Locale.getDefault());

    private AlertHelper() {}

    /**
     * Creates an alert as an alarm on the device.
     *
     * @param context
     * @param alert
     */
    public static void createAlarm(Context context, Alert alert) {
        long time = alert.time - (alert.reminder * MINUTE_MS);
        boolean repeat = alert.repeat;
        long interval = WEEK_MS;

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlertNotificationReceiver.class);
        intent.putExtra(AlertNotificationReceiver.EXTRA_NOTIFICATION,
            createNotification(context, alert));

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT);

        if (repeat) {
            manager.setExact(AlarmManager.RTC_WAKEUP, time, alarmIntent);
        } else {
            manager.setRepeating(AlarmManager.RTC_WAKEUP, time, interval, alarmIntent);
        }
    }

    /**
     * Inserts the alert into database.
     *
     * @param context
     * @param alert
     * @return id of alert
     */
    public static long createAlarmDatabase(Context context, Alert alert) {
        return ALERTS_TABLE.createAlert(context, alert);
    }

    /**
     * Updates the alert in the database.
     *
     * @param context
     * @param alert
     * @return number of rows affected
     */
    public static int updateAlarmDatabase(Context context, Alert alert) {
        return ALERTS_TABLE.updateAlert(context, alert);
    }

    /**
     * Deletes an existing alert from the database.
     *
     * @param context
     * @param alertId
     * @return number of rows affected
     */
    public static int removeAlarmDatabase(Context context, long alertId) {
        return ALERTS_TABLE.removeAlert(context, alertId);
    }

    /**
     * Creates a notification to be used when an alarm is set off. This notifications carries the
     * message to be displayed as well as the sound or vibration needed to be played.
     *
     * @param context
     * @param alert
     * @return instance of the notification
     */
    public static Notification createNotification(Context context, Alert alert) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(alert.alertText);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(context.getResources().getColor(R.color.colorPrimary));
        }

        if (alert.vibrate) {
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(alert.time);

        builder.setContentText(TIME_FORMAT.format(calendar.getTime()));

        Notification notification = builder.build();
        if (alert.sound == null) {
            notification.sound =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        } else {
            notification.sound = alert.sound;
        }

        return notification;
    }
}
