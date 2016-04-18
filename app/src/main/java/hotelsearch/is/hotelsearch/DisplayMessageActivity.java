package hotelsearch.is.hotelsearch;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import hotelsearch.is.database.DBAdapter;

public class DisplayMessageActivity extends AppCompatActivity {
    public final static String HOTEL_INFO = "hotelsearch.is.hotelsearch.HOTEL";

    DBAdapter myDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
       // unused..
        String message = intent.getStringExtra(SearchActivity.EXTRA_MESSAGE);

        openDb();
        populateHotelListView();
        hotelListCallBack();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDB();
    }

    private void closeDB() {
        myDb.close();
    }


    private void openDb() {
        myDb= new DBAdapter(this);
        myDb.open();
    }
    // Fill list with Cursor from DB (list of hotels)
    private void populateHotelListView(){
        // Get all rows from Db to populate list
        Cursor cursor = myDb.getAllRows();
        // Match DB keys to View ids - e.g name = hotelname
        String[] fromFieldNames = new String[] {DBAdapter.KEY_NAME,DBAdapter.KEY_ADDRESS};
        int[] toViewIDs = new int[] {R.id.hotelNameTextView, R.id.hotelAddressTextView};
        // set up Cursor adapter to use my layout, my tables from the db,
        // from my db to the list ids
        SimpleCursorAdapter myCursorAdapter = new SimpleCursorAdapter(getBaseContext(),
                R.layout.hotel_layout, cursor,fromFieldNames,toViewIDs,0);
        // Grab the listview from my activity and set it to use the newly
        // created cursor adapter

        ListView listView = (ListView) findViewById(R.id.hotelListView);
        listView.setAdapter(myCursorAdapter);

    }
    private void hotelListCallBack() {
        ListView listView = (ListView) findViewById(R.id.hotelListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long idInDB) {
                Cursor cursor = myDb.getRow(idInDB);
                if(cursor.moveToFirst()) {
                    String[] hotelInfo = new String[6];
                    long idDb = cursor.getLong(DBAdapter.COL_ROWID);
                    hotelInfo[0] = cursor.getString(DBAdapter.COL_NAME);
                    hotelInfo[1] = cursor.getString(DBAdapter.COL_ADDRESS);
                    hotelInfo[2] = cursor.getString(DBAdapter.COL_CITY);
                    hotelInfo[3] = cursor.getString(DBAdapter.COL_ZIP);
                    hotelInfo[4] = cursor.getString(DBAdapter.COL_WEBSITE);
                    hotelInfo[5] = cursor.getString(DBAdapter.COL_LATLNG);

                    Intent intent = new Intent(getApplicationContext(), ShowMap.class);
                    intent.putExtra(HOTEL_INFO,hotelInfo);
                    startActivity(intent);

                }
                cursor.close();

            }
        });


    }

}
