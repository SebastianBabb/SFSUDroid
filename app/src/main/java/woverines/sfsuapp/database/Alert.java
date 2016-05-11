package woverines.sfsuapp.database;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Stores information about an alert.
 *
 * @author Gary Ng
 */
public class Alert implements Parcelable {

    public long id;
    public int courseId;
    public String alertText;
    public long time;
    public int reminder; // Minutes
    public Uri sound;
    public boolean vibrate;
    public boolean repeat;

    public Alert() {
        time = System.currentTimeMillis();
    }

    public Alert(long id) {
        this();
        this.id = id;
    }

    protected Alert(Parcel in) {
        id = in.readLong();
        courseId = in.readInt();
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
        dest.writeInt(courseId);
        dest.writeString(alertText);
        dest.writeLong(time);
        dest.writeInt(reminder);
        dest.writeParcelable(sound, flags);
        dest.writeByte((byte) (vibrate ? 1 : 0));
        dest.writeByte((byte) (repeat ? 1 : 0));
    }
}
