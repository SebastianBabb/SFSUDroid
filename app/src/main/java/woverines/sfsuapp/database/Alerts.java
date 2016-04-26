package woverines.sfsuapp.database;

/**
 * Created by ironsquishy on 4/26/16.
 */


public class Alerts {

    private int alertID;
    private int courseID;
    private int mVibrate;
    private int mReapeat;

    private String mTime;
    private String mReminder;
    private String mSound;

    public Alerts(){
        //default constructor
    }

    public Alerts(int id, int courseid, String time, String text, String reminder, String sound, int vibrate, int repeat){
        this.alertID = id;
        this.courseID = courseid;
        this.mVibrate = vibrate;
        this.mReapeat = repeat;
        this.mTime = time;
        this.mReminder = reminder;
        this.mSound = sound;
    }

    public int getAlertID() {
        return alertID;
    }

    public void setAlertID(int alertID) {
        this.alertID = alertID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public int getmVibrate() {
        return mVibrate;
    }

    public void setmVibrate(int mVibrate) {
        this.mVibrate = mVibrate;
    }

    public int getmReapeat() {
        return mReapeat;
    }

    public void setmReapeat(int mReapeat) {
        this.mReapeat = mReapeat;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    public String getmReminder() {
        return mReminder;
    }

    public void setmReminder(String mReminder) {
        this.mReminder = mReminder;
    }

    public String getmSound() {
        return mSound;
    }

    public void setmSound(String mSound) {
        this.mSound = mSound;
    }
}
