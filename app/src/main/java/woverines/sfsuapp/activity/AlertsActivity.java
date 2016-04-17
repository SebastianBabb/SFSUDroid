package woverines.sfsuapp.activity;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import woverines.sfsuapp.R;

public class AlertsActivity extends AppCompatActivity implements OnItemSelectedListener {

    public static final String EXTRA_ALERT = "alert";
    public static final int REQUEST_CODE_RINGTONE = 1;

    private static final SimpleDateFormat DATE_FORMAT =
        new SimpleDateFormat("EEE, MMMM d, yyyy", Locale.getDefault());
    private static final SimpleDateFormat TIME_FORMAT =
        new SimpleDateFormat("h:mm a", Locale.getDefault());

    private static int[] REMINDERS = {5, 10, 15, 30, 45, 60};

    private Button date;
    private Button time;
    private Spinner reminder;
    private TextView alertSound;
    private CheckedTextView vibrate;
    private CheckedTextView repeat;

    private Alert alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Calendar calendar = Calendar.getInstance();

        date = (Button) findViewById(R.id.date);
        date.setText(DATE_FORMAT.format(calendar.getTime()));
        date.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onDateClick(System.currentTimeMillis());
            }
        });

        time = (Button) findViewById(R.id.time);
        time.setText(TIME_FORMAT.format(calendar.getTime()));
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
            }
        });

        repeat = (CheckedTextView) findViewById(R.id.repeat);
        repeat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                repeat.toggle();
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            alert = intent.getParcelableExtra(EXTRA_ALERT);
        }

        if (alert == null) {
            alert = new Alert();
        }
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onDateClick(long milliseconds) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        DatePickerDialog dialog = new DatePickerDialog(this, new OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    calendar.set(year, monthOfYear, dayOfMonth);
                    date.setText(DATE_FORMAT.format(calendar.getTime()));
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
            alert.sound = uri != null ? uri.toString() : null;

            if (uri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
                alertSound.setText(ringtone.getTitle(this));
            }
        }
    }

    public static class Alert implements Parcelable {

        public String alertText;
        public long time;
        public int reminder; // Minutes
        public String sound;
        public boolean vibrate;
        public boolean repeat;

        public Alert() {
            reminder = -1;
        }

        protected Alert(Parcel in) {
            alertText = in.readString();
            time = in.readLong();
            reminder = in.readInt();
            sound = in.readString();
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
            dest.writeString(alertText);
            dest.writeLong(time);
            dest.writeInt(reminder);
            dest.writeString(sound);
            dest.writeByte((byte) (vibrate ? 1 : 0));
            dest.writeByte((byte) (repeat ? 1 : 0));
        }
    }
}
