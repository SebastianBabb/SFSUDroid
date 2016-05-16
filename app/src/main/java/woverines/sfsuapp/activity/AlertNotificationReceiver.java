package woverines.sfsuapp.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import woverines.sfsuapp.database.ALERTS_TABLE;
import woverines.sfsuapp.database.Alert;

/**
 * Responsible for handle alert notifications when needed to be displayed. Also initializes all
 * existing alarms found in the database whenever device is rebooted.
 *
 * @author Gary Ng
 */
public class AlertNotificationReceiver extends BroadcastReceiver {

    public static final String EXTRA_NOTIFICATION = "notification";
    public static final String EXTRA_NOTIFICATION_ID = "notification_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action != null && action.equals("android.intent.action.BOOT_COMPLETED")) {
            List<Alert> alerts = ALERTS_TABLE.getAlerts(context, System.currentTimeMillis());

            for (Alert alert : alerts) {
                AlertHelper.createAlarm(context, alert);
            }
        } else {
            Notification notification = intent.getParcelableExtra(EXTRA_NOTIFICATION);
            if (notification == null) {
                return;
            }

            int id = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0);

            NotificationManager manager =
                (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
            manager.notify(id, notification);

            ALERTS_TABLE.checkRepeats(context);
        }
    }
}
