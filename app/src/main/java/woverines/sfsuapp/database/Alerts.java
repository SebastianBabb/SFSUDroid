package woverines.sfsuapp.database;

/**
 * Created by ironsquishy on 4/26/16.
 */


public class Alerts {

    private long alertID;
    private int courseID;
    private String text;
    private int mVibrate;
    private int mReapeat;

    private long mTime;
    private int mReminder;
    private String mSound;

    public Alerts(){
        //default constructor
    }

    public Alerts(long id, int courseid, long time, String text, int reminder, String sound, int vibrate, int repeat){
        this.alertID = id;
        this.courseID = courseid;
        this.text = text;
        this.mVibrate = vibrate;
        this.mReapeat = repeat;
        this.mTime = time;
        this.mReminder = reminder;
        this.mSound = sound;
    }

    public long getAlertID() {
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public long getmTime() {
        return mTime;
    }

    public void setmTime(long mTime) {
        this.mTime = mTime;
    }

    public int getmReminder() {
        return mReminder;
    }

    public void setmReminder(int mReminder) {
        this.mReminder = mReminder;
    }

    public String getmSound() {
        return mSound;
    }

    public void setmSound(String mSound) {
        this.mSound = mSound;
    }
}
