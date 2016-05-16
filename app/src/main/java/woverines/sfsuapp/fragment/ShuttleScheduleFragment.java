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
    private TextView timerDescription;
    private TextView daySchedule;
    private Timer countdown;
    private Calendar c;

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
        timerDescription = (TextView) rootView.findViewById(R.id.shuttleDescription);

        daySchedule = (TextView) rootView.findViewById(R.id.daySchedule);

        c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_WEEK);
        if(day > 1 && day <6){
            //Log.d("mon-thu","week");
            daySchedule.setText(R.string.weekday_schedule);
        }else if(day == 6){
            //Log.d("fri","week");
            daySchedule.setText(R.string.friday_schedule);
        }else{
            //Log.d("weekend","week");
            daySchedule.setText(R.string.whole_schedule);
        }




        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    c = Calendar.getInstance();
                    countdown.cancel();
                }catch(Exception e){

                }
                makeNewTimer();
            }
        });


        return rootView;
    }

    public void makeNewTimer(){
        //c = Calendar.getInstance();
        long currentTime = makeMs(c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),c.get(Calendar.SECOND));

        long firstShuttleTime = makeMs(7,0,0);
        long lastShuttleTime;
        int day = c.get(Calendar.DAY_OF_WEEK);

        //hard coding time for testing scenareos
        //currentTime = makeMs(13,40,41);
        //day = 5;

        if(day > 1 && day <6){
            //Log.d("mon-thu","week");
            lastShuttleTime = makeMs(22,30,0);

        }else if(day == 6){
            //Log.d("fri","week");
            lastShuttleTime = makeMs(19,15,0);
        }else{
            //Log.d("weekend","week");
            firstShuttleTime = 0;
            lastShuttleTime = 0;
        }

        if (currentTime<firstShuttleTime){
            //Log.d("before shuttle","week");
            timerDescription.setText("First shuttle in:");
            countdown = new Timer(firstShuttleTime - currentTime, 1000);
            countdown.start();
        }else if(currentTime < lastShuttleTime){
            //Log.d("mid shuttle","week");
            timerDescription.setText("Shuttles run for:");
            countdown = new Timer(lastShuttleTime-currentTime,1000);
            countdown.start();
        }else{
            //Log.d("after Shuttle","week");
            timerDescription.setText("There are no more shuttles running");
        }
    }

    public long makeMs(int hr, int min, int sec){
        return TimeUnit.HOURS.toMillis(hr)+
                TimeUnit.MINUTES.toMillis(min)+
                TimeUnit.SECONDS.toMillis(sec);

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
            secRemaining = TimeUnit.MILLISECONDS.toSeconds(msRemaining) - (60*60*hrRemaining) - (60*minRemaining);
            String timeString = String.format("%02d:%02d:%02d",hrRemaining,minRemaining,secRemaining);
            timerView.setText(timeString);

        }

        @Override
        public void onFinish() {
            timerView.setText("00:00:00");
        }
    }


}