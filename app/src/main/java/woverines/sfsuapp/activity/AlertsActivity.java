package woverines.sfsuapp.activity;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
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
import java.util.Locale;

import woverines.sfsuapp.R;
import woverines.sfsuapp.database.Alert;

/**
 * Alerts Activity is responsible for enabling users to view and edit a single alert.
 *
 * @author Gary Ng
 */
public class AlertsActivity extends AppCompatActivity implements OnItemSelectedListener {

    public static final String EXTRA_COURSE_ID = "course_id";
    public static final String EXTRA_ALERT = "alert";
    public static final int REQUEST_CODE_RINGTONE = 1;

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

    /**
     * Initializes the current view if an alert was passed in otherwise create an empty alert for
     * creation.
     */
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
            alert.courseId = courseId;
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

    /**
     * Takes the alert created, creates the alarm on the device, then saves it into the database.
     * The alert created will be returned once the the activity ends.
     */
    public void onSave() {
        if (alert.alertText == null || alert.alertText.isEmpty()) {
            Toast.makeText(this, R.string.alert_text_required, Toast.LENGTH_LONG).show();
            return;
        }

        AlertHelper.createAlarm(this, alert);

        if (alert.id > 0) {
            AlertHelper.updateAlarmDatabase(this, alert);
        } else {
            alert.id = AlertHelper.createAlarmDatabase(this, alert);
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_COURSE_ID, courseId);
        intent.putExtra(EXTRA_ALERT, alert);

        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Deletes the currently viewed alert from the database.
     */
    public void onDelete() {
        if (alert.id > 0) {
            AlertHelper.removeAlarmDatabase(this, alert.id);
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_COURSE_ID, courseId);
        intent.putExtra(EXTRA_ALERT, alert);

        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Used whenever the date option is selected by the user.
     *
     * @param milliseconds time defined by this alert
     */
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

    /**
     * Used whenever the time option is selected by the user.
     *
     * @param milliseconds time defined by this alert
     */
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

    /**
     * Displays the Android sound picker.
     */
    public void onAlertSoundClick() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Sound");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);

        startActivityForResult(intent, REQUEST_CODE_RINGTONE);
    }

    /**
     * Handles the sound returned from the Android sound picker.
     *
     * @param resultCode result code
     * @param data contains the sound
     */
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
}
