package hotelsearch.is.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;


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

	// TODO: IF there are changes made to the DB structure inrement version by 1.

    public static final int DATABASE_VERSION = 2;
	
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
	public boolean updateRow(long rowId, String name, String address, String website,
                             String zip, String city, String latlng) {
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
	
	
	
	/////////////////////////////////////////////////////////////////////
	//	Private Helper Classes:
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Private class which handles database creation and upgrading.
	 * Used to handle low-level database access.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE_SQL);			
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
