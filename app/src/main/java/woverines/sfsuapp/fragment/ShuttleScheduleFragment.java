package woverines.sfsuapp.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import woverines.sfsuapp.R;


/**
 * A placeholder fragment containing a simple view.
 */

public class ShuttleScheduleFragment extends Fragment{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_TEXT = "text";
    private Button timeButton;
    private TextView timerView;
    private Timer countdown;

    public ShuttleScheduleFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     *
     */
    public static ShuttleScheduleFragment newInstance(int sectionNumber, String text) {

        ShuttleScheduleFragment fragment = new ShuttleScheduleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shuttle_schedule, container, false);
        Bundle args = getArguments();
        final TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        timeButton = (Button) rootView.findViewById(R.id.ShuttleButton);
        timerView = (TextView) rootView.findViewById(R.id.timerText);

        countdown = new Timer(180000,1000);
        makeNewTimer();
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNewTimer();
                countdown.start();
            }
        });


        return rootView;
    }

    public void makeNewTimer(){
        Calendar c = Calendar.getInstance();
        long ms = TimeUnit.HOURS.toMillis(c.get(Calendar.HOUR_OF_DAY))+
                TimeUnit.MINUTES.toMillis(c.get(Calendar.MINUTE))+
                TimeUnit.SECONDS.toMillis(c.get(Calendar.SECOND));


    }

    public class Timer extends CountDownTimer{

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public Timer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long msRemaining, hrRemaining, minRemaining, secRemaining;

            msRemaining = millisUntilFinished;
            hrRemaining = TimeUnit.MILLISECONDS.toHours(msRemaining);
            minRemaining = TimeUnit.MILLISECONDS.toMinutes(msRemaining) - 60*hrRemaining;
            secRemaining = TimeUnit.MILLISECONDS.toSeconds(msRemaining) - 60*minRemaining;
            String timeString = String.format("%02d:%02d:%02d",hrRemaining,minRemaining,secRemaining);
            timerView.setText(timeString);

        }

        @Override
        public void onFinish() {
            timerView.setText("00:00:00");
        }
    }


}