package hotelsearch.is.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import hotelsearch.is.hotelsearch.R;


public class DBAdapter {



    // For logging:
    private static final String TAG = "DBAdapter";

    // DB Fields
    public static final String KEY_ROWID = "_id";
    public static final int COL_ROWID = 0;

    public static final String KEY_NAME = "NAME";
    public static final String KEY_ADDRESS = "ADDRESS";
    public static final String KEY_ZIP = "ZIP";
    public static final String KEY_CITY = "CITY";
    public static final String KEY_WEBSITE = "WEBSITE";
    public static final String KEY_LATLNG = "LATLNG";

    // Field numbers 1...n
    public static final int COL_NAME = 1;
    public static final int COL_ADDRESS = 2;
    public static final int COL_ZIP = 3;
    public static final int COL_CITY = 4;
    public static final int COL_WEBSITE = 5;
    public static final int COL_LATLNG = 6;


    public static final String[] ALL_KEYS = new String[] {KEY_ROWID,
            KEY_NAME, KEY_ADDRESS, KEY_ZIP, KEY_CITY, KEY_WEBSITE, KEY_LATLNG};

    // DB info: it's name, and the table we are using (just one).
    public static final String DATABASE_NAME = "MyDb";
    public static final String DATABASE_TABLE = "mainTable";

    // TODO: IF there are changes made to the DB structure increment version by 1.

    public static final int DATABASE_VERSION = 6;

    // Creates the database with our columns we defined earlier

    private static final String DATABASE_CREATE_SQL =
            "create table " + DATABASE_TABLE
                    + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_NAME + " text not null, "
                    + KEY_ADDRESS + " text not null, "
                    + KEY_ZIP + " text not null, "
                    + KEY_CITY + " text not null, "
                    + KEY_WEBSITE + " string not null, "
                    + KEY_LATLNG + " string not null"
                    + ");";

    // Context of application who uses us.
    private final Context context;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;


    public DBAdapter(Context ctx) {
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }

    // Open the database connection.
    public DBAdapter open() {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        myDBHelper.close();
    }

    // Add a new set of values to the database.
    public long insertRow(String name, String address, String zip,
                          String city, String website, String latlng) {

        // Create row's data:
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_ADDRESS, address);
        initialValues.put(KEY_ZIP,zip);
        initialValues.put(KEY_CITY,city);
        initialValues.put(KEY_WEBSITE, website);
        initialValues.put(KEY_LATLNG, latlng);

        // Insert it into the database.
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    // Delete a row from the database, by rowId (primary key)
    public boolean deleteRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        return db.delete(DATABASE_TABLE, where, null) != 0;
    }

    public void deleteAll() {
        Cursor c = getAllRows();
        long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
        if (c.moveToFirst()) {
            do {
                deleteRow(c.getLong((int) rowId));
            } while (c.moveToNext());
        }
        c.close();
    }

    // Return all data in the database.
    public Cursor getAllRows() {
        String where = null;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Get a specific row (by rowId)
    public Cursor getRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Change an existing row to be equal to new data.
    public boolean updateRow(long rowId, String name, String address, String zip,
                             String city, String website, String latlng) {
        String where = KEY_ROWID + "=" + rowId;

        // Create row's data:
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_NAME, name);
        newValues.put(KEY_ADDRESS, address);
        newValues.put(KEY_ZIP,zip);
        newValues.put(KEY_CITY,city);
        newValues.put(KEY_WEBSITE, website);
        newValues.put(KEY_LATLNG, latlng);

        // Insert it into the database.
        return db.update(DATABASE_TABLE, newValues, where, null) != 0;
    }
    private void loadHotelFile() {
        new Thread(new Runnable() {
            public void run() {
                boolean test = false;
                while(!test){
                    try {

                        test = loadWords();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }

            }
        }).start();
    }
    private boolean loadWords() throws IOException {
        // read hotels from TXT database.
        InputStream inputStream = context.getResources().openRawResource(R.raw.hotels);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        long i = 0;
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                // Strings in text file splitted with " | " "\\" are escape characters
                // the first one escpaes the "|" and the second one escapes the escape char.. :)

                String[] strings = TextUtils.split(line, "\\|");
                // if 6 strings weren't extracted from the line we skip that line..
                if (strings.length < 6) continue;
                boolean update = updateRow(i,strings[0],strings[0],strings[0],strings[0],strings[0],strings[0]);
                i++;
                Log.d("Updated row number: ",""+i);

            }
        } finally {
            reader.close();
        }
        return true;
    }

    public Cursor searchDb(String string){
        // to be implemented...
        // search all fields for and return a Cursor
        // i.e a pointer to the database entry
        return null;
    }




    // inner class, handles creation of DB. importing data from the text file
    // destroying, and updating Db.
    private static class DatabaseHelper extends SQLiteOpenHelper {
        private final Context mHelperContext;
        private SQLiteDatabase helpDb;

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mHelperContext = context;
        }

        private void loadHotelDatabase() {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        loadHotels();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }
        private void loadHotels() throws IOException {
            InputStream inputStream = mHelperContext.getResources().openRawResource(R.raw.hotels);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            try {
                String line;
                Log.e("fór loadHotels fallið", " ");
                while ((line = reader.readLine()) != null) {
                    // Strings in text file splitted with " | " "\\" are escape characters
                    // the first one escpaes the "|" and the second one escapes the escape char.. :)

                    String[] strings = TextUtils.split(line, "\\|");
                    // if 6 strings weren't extracted from the line we skip that line..
                    Log.e("lengdin er: ", ""+strings.length);
                    if (strings.length < 6) continue;
                    Long id = addToDb(strings[0], strings[0], strings[0], strings[0], strings[0], strings[0]);
                    Log.e("Added stuff to db","strings like");
                    Log.e(strings[0], strings[1]);
                    Log.e("lengdin er: ", ""+strings.length);
                }
            } finally {
                reader.close();
            }
        }
        public long addToDb(String name, String address, String zip, String city, String www, String gps) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_NAME, name);
            initialValues.put(KEY_ADDRESS, address);
            initialValues.put(KEY_ZIP,zip);
            initialValues.put(KEY_CITY,city);
            initialValues.put(KEY_WEBSITE, www);
            initialValues.put(KEY_LATLNG, gps);
            return helpDb.insert(DATABASE_TABLE, null, initialValues);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            helpDb = _db;
            _db.execSQL(DATABASE_CREATE_SQL);
            Log.e("ERROR:","ROBOTS TOOK OVER MY LIFE!! And created a new database");
            loadHotelDatabase();

        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            // Recreate new database:
            onCreate(_db);
        }
    }
}