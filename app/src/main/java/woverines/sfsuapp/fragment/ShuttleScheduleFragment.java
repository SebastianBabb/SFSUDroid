package woverines.sfsuapp.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;


import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import woverines.sfsuapp.R;


/**
 * Shuttle map fragment Java file.
 * Controls fragment_shuttle_schedule.xml
 * Displays Static map of campus shuttle map
 * Displays the NextShuttle timer when
 * @author Joshua Rubin
 */

public class ShuttleScheduleFragment extends Fragment{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_TEXT = "text";

    //Create Button vars
    private Button timeButton;
    private Button showDialog;
    private Button dismissDialog;
    //Create TextView vars
    private TextView timerView;
    private TextView timerDescription;
    //Create Shuttle Schedule Dialog var
    private Dialog mapInfoDialog;
    //Create WebView for zoomed Static Map
    public WebView mapWeb;
    //Create Timer for NextShuttle timer
    private Timer countdown;
    //Create Calender to create dynamic Timer
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

        mapWeb = (WebView) rootView.findViewById(R.id.mapWebView);
        String data = "<body>" + "<img src=\"sfsumapmed.png\"/></body>";
        mapWeb.loadDataWithBaseURL("file:///android_asset/", data, "text/html", "utf-8", null);
        mapWeb.getSettings().setBuiltInZoomControls(true);

        timerView = (TextView) rootView.findViewById(R.id.timerText);
        timerDescription = (TextView) rootView.findViewById(R.id.shuttleDescription);
//        daySchedule = (TextView) rootView.findViewById(R.id.daySchedule);


        c = Calendar.getInstance();

        //Picks a schedule based on the day.
//        int day = c.get(Calendar.DAY_OF_WEEK);
//        if(day > 1 && day <6){
//            //Log.d("mon-thu","week");
//            daySchedule.setText(R.string.weekday_schedule);
//        }else if(day == 6){
//            //Log.d("fri","week");
//            daySchedule.setText(R.string.friday_schedule);
//        }else{
//            //Log.d("weekend","week");
//            daySchedule.setText(R.string.whole_schedule);
//        }

        //Create On-click Listener for the Shuttle Countdown button
        timeButton = (Button) rootView.findViewById(R.id.ShuttleButton);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    countdown.cancel();
                }catch(Exception e){

                }
                makeNewTimer();
            }
        });

        //Create a new Dialog out of the dialog_shuttle_map_info fragment
        mapInfoDialog = new Dialog(getContext());
        mapInfoDialog.setContentView(R.layout.dialog_shuttle_map_info);
        mapInfoDialog.setTitle("Shuttle Map Info");

        //Make the show_map_info button on fragment_shuttle_map expand the dialog
        showDialog = (Button) rootView.findViewById(R.id.show_map_info);
        showDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapInfoDialog.show();
            }
        });

        //Make the dismiss_map_dialog button on the dialog_shuttle_map_info dialog dismiss itself
        dismissDialog = (Button) mapInfoDialog.findViewById(R.id.dismiss_map_dialog);
        dismissDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapInfoDialog.dismiss();
            }
        });


        return rootView;
    }

    /**
     * Create new Timer for the Shuttle Countdown.
     * The timer has 3 use cases:
     *      1-The button is pressed in the morning before the shuttles start running:
     *          The timer will display "First shuttle in hh:mm:ss" where "hh:mm:ss" will count down
     *          the hours:minutes:seconds until the first shuttle arrives
     *      2-The button is pressed during hours of shuttle operation:
     *          The timer will display "Shuttles run for hh:mm:ss" where "hh:mm:ss" will count down
     *          the hours:minutes:seconds until the last shuttle of the day will depart
     *      3-The button is pressed after the last shuttle is scheduled to depart:
     *          The timer will display "Out of service 00:00:00" to indicate the timer has run out
     *          and there will be no more shuttles for the night, or during the weekend
     *
     * The Shuttle Schedule will automatically set the first-last shuttle time based on the day
     * During the weekend there are 0 hours of operation, so the shuttle is always out of service.
     */
    public void makeNewTimer(){
        c = Calendar.getInstance();

        //Create the Current time of day in milliseconds into the day
        long currentTime = makeMs(c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),c.get(Calendar.SECOND));

        //The first shuttle is always scheduled at 7am
        long firstShuttleTime = makeMs(7,0,0);
        long lastShuttleTime;

        //Get current day of week as numerical value
        int day = c.get(Calendar.DAY_OF_WEEK);

        //hard coding time for testing scenareos
        //currentTime = makeMs(13,40,41);
        //day = 5;

        //Set the last scheduled Shuttle based on the current day
        if(day > 1 && day <6){ // MON - THURS SCHEDULE
            //Log.d("mon-thu","week");
            lastShuttleTime = makeMs(22,30,0);

        }else if(day == 6){ //FRIDAY SCHEDULE
            //Log.d("fri","week");
            lastShuttleTime = makeMs(19,15,0);
        }else{ //WEEKEND SCHEDULE, 0 HOURS OF OPERATION
            //Log.d("weekend","week");
            firstShuttleTime = 0;
            lastShuttleTime = 0;
        }

        //Compare Current time to First and last shuttle time

        if (currentTime<firstShuttleTime){//BUTTON IS PRESSED BEFORE FIRST SHUTTLE
            //Log.d("before shuttle","week");
            timerDescription.setText("First shuttle in ");
            countdown = new Timer(firstShuttleTime - currentTime, 1000);
            countdown.start();
        }else if(currentTime < lastShuttleTime){ //BUTTON IS PRESSED DURING HOURS OF OPERATION
            //Log.d("mid shuttle","week");
            timerDescription.setText("Shuttles run for ");
            countdown = new Timer(lastShuttleTime-currentTime,1000);
            countdown.start();
        }else{  //BUTTON IS PRESSED AFTER HOURS OR ON WEEKEND; OUT OF SERVICE
            //Log.d("after Shuttle","week");
            timerDescription.setText("Out of service");
        }
    }

    /**
     * Takes the current time of day in hr,min,sec and returns that time as a millisecond value
     *
     * @param hr hours into the current day
     * @param min minutes into the current hour
     * @param sec seconds into the current minute
     * @return long representing the current time in milliseconds into the day
     */
    public long makeMs(int hr, int min, int sec){
        return TimeUnit.HOURS.toMillis(hr)+
                TimeUnit.MINUTES.toMillis(min)+
                TimeUnit.SECONDS.toMillis(sec);

    }

    /**
     * Timer class uses Countdown timer to create the Shuttle Countdown
     * @author Joshua Rubin
     */
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

        /**
         * Takes the time left on the counter, converts it into hh:mm:ss and displays it on the
         * timerView in fragment_shuttle_Schedule
         * @param millisUntilFinished time left on the counter
         */
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

        /**
         * When the timer is finished, display 00:00:00 on the timerView in fragment_shuttle_schedule
         */
        @Override
        public void onFinish() {
            timerView.setText("00:00:00");
        }
    }


}