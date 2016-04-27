package woverines.sfsuapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//Table: Alerts
//Fields: Alert ID (int), Course ID (int), Text (text), Time (int), Reminder (int), Sound (text), Vibrate (int), Repeat (int)
public class DBHandler extends SQLiteOpenHelper {

        /** Private Data membrs*/
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME ="databaseDB.db";


        //For LOGGING
        private static final String TAG = "DATABASE";

        /**
         * @author Allen Space
         * Description: Constructor for SQLiteHandler the follow are its params
         * @param context Used call, can use this keyword for passsing
         * @param factory CursorFactory for SQLITEDATABASE class
         *
         */
        public DBHandler(Context context, SQLiteDatabase.CursorFactory factory)
        {
            super(context, DATABASE_NAME, factory, DATABASE_VERSION);

        }

        public DBHandler(Context context)
        {
            this(context, null);
        }

        /**
         * @author Allen Space
         * Description: Override onCreate from android and creates new table
         *              with
         * @param db  SQLiteDatabase object passed in. For new query.
         * */
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            //TODO Create run all Create Tables Query here!!!!!!
            db.execSQL(ALERTS_TABLE.CREATE_QUERY);

            Log.i(TAG, "Properly Created Database..");
        }

        /**
         * @author Allen Space
         * Description: Use for upgrade table. deletes and calls onCreat() to create new table.
         * @param db SQLiteDabase object from java.
         * @param oldVersion Must need old verison of the database.
         * @param newVersion Needs also new verison of the database.
         * */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            //db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
            onCreate(db);

            Log.i(TAG, "Properly executed OnUpgrade().");
        }

        /**
         * @author Allen Space
         * @param entry Object that is passed in for new row.
         * Description: Adds new row into the table.
         * */
        public void addEntry(Object entry)
        {
            ContentValues values = new ContentValues();
            //Add entries to the DB
            values.put("NEEDS TABLE COLUMN NAME", "ENTRY here");
            SQLiteDatabase db = getWritableDatabase();
            db.insert("NEEDS TABLE NAME", null, values);
            db.close();

            Log.i(TAG, "Properly added entry to DB.");
        }

        /**
         * @author Allen Space
         * Description: Finds entry string and deletes.
         * @param deleteQuery String query to delete.
         * */
        public void deleteEntry(String deleteQuery)
        {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL(deleteQuery);

            Log.i(TAG, "Properly deleted Field.");

        }

        /**
         * @author Allen Space
         * Description: Will take all contents in the database and turn to string.
         * @return dbString Will return the database string.
         *
         * */
        public String databaseToString()
        {
            //New string sets nothing.
            //New sql object.
            String dbString = "";
            SQLiteDatabase db = getWritableDatabase();

            //Go back to beginning and start from there.
            String query = "SELECT * FROM " + "TABLE NAME" + " WHERE 1";

            //Cursor points to a location in results.
            Cursor c = db.rawQuery(query, null);
            //Move to the first row.
            c.moveToFirst();

            //When beginning with first data string, continue to ends of database.
            while (!c.isAfterLast())
            {
                if(c.getString(c.getColumnIndex("entryname")) != null)
                {
                    dbString += c.getString(c.getColumnIndex("entryname"));
                    dbString += "\n";

                }

                c.moveToNext();
            }
            db.close();

            Log.i(TAG,"Returning data, in string type");

            return dbString; //string return with all strings from database.
        }
}

