package woverines.sfsuapp.database;


public class ALERTS_TABLE {
    public static final String TABLE_ALERTS = "alerts";
    public static final String COLUMN_ALERTS_ID = "_alerts_Id";
    public static final String COLUMN_COURSE_ID = "_course_Id";
    public static final String COLUMN_TEXT = "_text";
    public static final String COLUMN_TIME = "_time";
    public static final String COLUMN_REMINDER = "_reminder";
    public static final String COLUMN_SOUND = "_sound";
    public static final String COLUMN_VIBRATE = "_vibrate";
    public static final String COLUMN_REPEAT = "_repeat";

    public final String createTableQuery(){
        String query = "CREATE TABLE " +
                this.TABLE_ALERTS +
                "(" +
                this.COLUMN_ALERTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                this.COLUMN_COURSE_ID + " INTEGER, " +
                this.COLUMN_TEXT + " TEXT, " +
                this.COLUMN_TIME + " TEXT, " +
                this.COLUMN_REMINDER + " TEXT, " +
                this.COLUMN_SOUND + " TEXT, " +
                this.COLUMN_VIBRATE + " INTEGER, " +
                this.COLUMN_REPEAT + " INTEGER" +
                ");";
        return query;
    }

    public String deletValueQUERY(String value, String columnName){
        return "DELETE FROM " + this.TABLE_ALERTS + " WHERE " + columnName + "=\"" + value + "\";";
    }

    public String deletValueQUERY(int value, String columnName){
        return "DELETE FROM " + this.TABLE_ALERTS + " WHERE " + columnName + "=\"" + value + "\";";
    }
}
