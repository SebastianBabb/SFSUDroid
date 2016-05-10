package woverines.sfsuapp.activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import woverines.sfsuapp.R;
import woverines.sfsuapp.database.ALERTS_TABLE;
import woverines.sfsuapp.database.Alerts;

public class AlertsActivity extends AppCompatActivity implements OnItemSelectedListener {

    public static final String EXTRA_COURSE_ID = "course_id";
    public static final String EXTRA_ALERT = "alert";
    public static final int REQUEST_CODE_RINGTONE = 1;

    public static final long MINUTE_MS = 60000L;
    public static final long DAY_MS = 86400000L;
    public static final long WEEK_MS = 7 * DAY_MS;

    private static final SimpleDateFormat DATE_FORMAT =
        new SimpleDateFormat("EEE, MMMM d, yyyy", Locale.getDefault());
    private static final SimpleDateFormat TIME_FORMAT =
        new SimpleDateFormat("h:mm a", Locale.getDefault());

    private static int[] REMINDERS = {0, 5, 10, 15, 30, 45, 60};

    private TextView text;
    private Button date;
    private Button time;
    private Spinner reminder;
    private TextView alertSound;
    private CheckedTextView vibrate;
    private CheckedTextView repeat;

    private int courseId;
    private Alert alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        text = (TextView) findViewById(R.id.text);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                alert.alertText = s.toString().trim();
            }
        });

        date = (Button) findViewById(R.id.date);
        date.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onDateClick(System.currentTimeMillis());
            }
        });

        time = (Button) findViewById(R.id.time);
        time.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onTimeClick(System.currentTimeMillis());
            }
        });

        reminder = (Spinner) findViewById(R.id.reminder);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.reminder_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reminder.setAdapter(adapter);

        alertSound = (TextView) findViewById(R.id.alert_sound);
        alertSound.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onAlertSoundClick();
            }
        });

        vibrate = (CheckedTextView) findViewById(R.id.vibrate);
        vibrate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrate.toggle();
                alert.vibrate = vibrate.isChecked();
            }
        });

        repeat = (CheckedTextView) findViewById(R.id.repeat);
        repeat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                repeat.toggle();
                alert.repeat = repeat.isChecked();
            }
        });

        initialize();
    }

    private void initialize() {
        Intent intent = getIntent();
        if (intent != null) {
            courseId = intent.getIntExtra(EXTRA_COURSE_ID, -1);
            alert = intent.getParcelableExtra(EXTRA_ALERT);
        }

        if (courseId == -1) {
            return;
        }

        if (alert == null) {
            alert = new Alert();
        }

        text.setText(alert.alertText);
        date.setText(DATE_FORMAT.format(alert.time));
        time.setText(TIME_FORMAT.format(alert.time));

        reminder.setSelection(0);

        if (alert.sound != null) {
            Ringtone ringtone = RingtoneManager.getRingtone(this, alert.sound);
            alertSound.setText(ringtone.getTitle(this));
        }

        vibrate.setChecked(alert.vibrate);
        repeat.setChecked(alert.repeat);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_RINGTONE:
                handleAlertSound(resultCode, data);
                break;
        }

        super.onActivityResult(resultCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alerts, menu);

        for (int i = 0; i < menu.size(); i++) {
            Drawable icon = menu.getItem(i).getIcon();
            if (icon != null) {
                icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            }
        }

        if (alert.id == 0) {
            menu.getItem(1).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                onSave();
                return true;
            case R.id.action_delete:
                onDelete();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSave() {
        if (alert.alertText == null || alert.alertText.isEmpty()) {
            Toast.makeText(this, R.string.alert_text_required, Toast.LENGTH_LONG).show();
            return;
        }

        AlertHelper.createAlarm(this, alert);

        if (alert.id > 0) {
            AlertHelper.updateAlarmDatabase(this, courseId, alert);
        } else {
            alert.id = AlertHelper.createAlarmDatabase(this, courseId, alert);
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_COURSE_ID, courseId);
        intent.putExtra(EXTRA_ALERT, alert);

        setResult(RESULT_OK, intent);
        finish();
    }

    public void onDelete() {
        if (alert.id > 0) {
            AlertHelper.removeAlarmDatabase(this, alert.id);
        }

        finish();
    }

    public void onDateClick(long milliseconds) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        DatePickerDialog dialog = new DatePickerDialog(this, new OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    calendar.set(year, monthOfYear, dayOfMonth);
                    date.setText(DATE_FORMAT.format(calendar.getTime()));

                    alert.time = calendar.getTimeInMillis();
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    public void onTimeClick(long milliseconds) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        TimePickerDialog dialog = new TimePickerDialog(this, new OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    time.setText(TIME_FORMAT.format(calendar.getTime()));

                    alert.time = calendar.getTimeInMillis();
                }
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        );

        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView parent, View view, int position, long id) {
        alert.reminder = REMINDERS[position];
    }

    @Override
    public void onNothingSelected(AdapterView parent) {
        alert.reminder = -1;
    }

    public void onAlertSoundClick() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Sound");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);

        startActivityForResult(intent, REQUEST_CODE_RINGTONE);
    }

    public void handleAlertSound(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            alert.sound = uri;

            if (uri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
                alertSound.setText(ringtone.getTitle(this));
            }
        }
    }

    public static class Alert implements Parcelable {

        public long id;
        public String alertText;
        public long time;
        public int reminder; // Minutes
        public Uri sound;
        public boolean vibrate;
        public boolean repeat;

        public Alert() {
            time = System.currentTimeMillis();
        }

        public Alert(Alerts alert) {
            id = alert.getAlertID();
            alertText = alert.getText();
            time = alert.getmTime();
            reminder = alert.getmReminder();
            sound = alert.getmSound() != null ? Uri.parse(alert.getmSound()) : null;
            vibrate = alert.getmVibrate() > 0;
            repeat = alert.getmReapeat() > 0;
        }

        protected Alert(Parcel in) {
            id = in.readLong();
            alertText = in.readString();
            time = in.readLong();
            reminder = in.readInt();
            sound = in.readParcelable(Uri.class.getClassLoader());
            vibrate = in.readByte() != 0;
            repeat = in.readByte() != 0;
        }

        public static final Creator<Alert> CREATOR = new Creator<Alert>() {
            @Override
            public Alert createFromParcel(Parcel in) {
                return new Alert(in);
            }

            @Override
            public Alert[] newArray(int size) {
                return new Alert[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(alertText);
            dest.writeLong(time);
            dest.writeInt(reminder);
            dest.writeParcelable(sound, flags);
            dest.writeByte((byte) (vibrate ? 1 : 0));
            dest.writeByte((byte) (repeat ? 1 : 0));
        }
    }

    public static class AlertNotificationReceiver extends BroadcastReceiver {

        public static final String EXTRA_NOTIFICATION = "notification";
        public static final String EXTRA_NOTIFICATION_ID = "notification_id";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action != null && action.equals("android.intent.action.BOOT_COMPLETED")) {
                List<Alerts> alerts = ALERTS_TABLE.getAlerts(context, System.currentTimeMillis());

                for (Alerts alert : alerts) {
                    AlertHelper.createAlarm(context, new Alert(alert));
                }
            } else {
                Notification notification = intent.getParcelableExtra(EXTRA_NOTIFICATION);
                if (notification == null) {
                    return;
                }

                int id = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0);

                NotificationManager manager =
                    (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                manager.notify(id, notification);

                ALERTS_TABLE.checkRepeats(context);
            }
        }
    }

    public static class AlertHelper {

        private AlertHelper() {}

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

        private static long createAlarmDatabase(Context context, int courseId, Alert alert) {
            Alerts instance = new Alerts(
                alert.id,
                courseId,
                alert.time,
                alert.alertText,
                alert.reminder,
                alert.sound != null ? alert.sound.toString() : null,
                alert.vibrate ? 1 : 0,
                alert.repeat ? 1 : 0
            );

            return ALERTS_TABLE.createAlert(context, instance);
        }

        private static int updateAlarmDatabase(Context context, int courseId, Alert alert) {
            Alerts instance = new Alerts(
                alert.id,
                courseId,
                alert.time,
                alert.alertText,
                alert.reminder,
                alert.sound != null ? alert.sound.toString() : null,
                alert.vibrate ? 1 : 0,
                alert.repeat ? 1 : 0
            );

            return ALERTS_TABLE.updateAlert(context, instance);
        }

        private static int removeAlarmDatabase(Context context, long alertId) {
            return ALERTS_TABLE.removeAlert(context, alertId);
        }

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
}
