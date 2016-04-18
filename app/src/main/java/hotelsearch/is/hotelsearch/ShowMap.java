package hotelsearch.is.hotelsearch;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import hotelsearch.is.database.DBAdapter;

public class ShowMap extends FragmentActivity
        implements OnMapReadyCallback {
    DBAdapter myDb;
    private GoogleMap map;
    private String[] info = new String[6];
    private Long hotelID = 0L;
    private String HotelName = "Missing";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);
        openDb();

        // Fetch data from intent (previous screen and build Info)
        Intent intent = getIntent();
        info = intent.getStringArrayExtra(DisplayMessageActivity.HOTEL_INFO);
        hotelID = intent.getLongExtra(DisplayMessageActivity.HOTEL_ID, 0L);

        // Handle the listview
        populateHotelListView();
        hotelListCallBack();




        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




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

    private void populateHotelListView() {
        Cursor cursor = myDb.getRow(hotelID);

        // Match DB keys to View ids - e.g name = hotelname
        String[] fromFieldNames = new String[] {DBAdapter.KEY_NAME,DBAdapter.KEY_ADDRESS,DBAdapter.KEY_CITY,
                DBAdapter.KEY_ZIP,DBAdapter.KEY_WEBSITE};
        int[] toViewIDs = new int[] {R.id.hotelmapname, R.id.hotelmapaddress, R.id.hotelmapcity,
                R.id.hotelmapzip,R.id.hotelmapwww};

        SimpleCursorAdapter myCursorAdapter = new SimpleCursorAdapter(getBaseContext(),
                R.layout.hotel_map_layout, cursor,fromFieldNames,toViewIDs,0);
        // Grab the listview from my activity and set it to use the newly
        // created cursor adapter

        ListView listView = (ListView) findViewById(R.id.wwwListView);
        listView.setAdapter(myCursorAdapter);
    }

    private void hotelListCallBack() {
        ListView listView = (ListView) findViewById(R.id.wwwListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = myDb.getRow(id);
                if(cursor.moveToFirst()){
                    String wwwAddress = "";
                    wwwAddress = cursor.getString(DBAdapter.COL_WEBSITE);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(wwwAddress));
                    startActivity(intent);


                }
                cursor.close();
            }
        });


    }


    private void addMarker(LatLng location) {
        map.addMarker(new MarkerOptions().position(location));
    }

    private LatLng buildInfo(String[] myHotel) {
        LatLng location = new LatLng(0,0);
        if(myHotel.length == 6) {
            HotelName = myHotel[0];
            String address = myHotel[1];
            String city = myHotel[2];
            String zip = myHotel[3];
            String website = myHotel[4];
            String info = HotelName + "\n" + address + "\n" + city + " "+ zip +
                    "\n" + website;

            String[] latlong =  myHotel[5].split(",");
            double latitude = Double.parseDouble(latlong[0]);
            double longitude = Double.parseDouble(latlong[1]);
            location = new LatLng(latitude, longitude);

        }
        return  location;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng location = buildInfo(info);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location,17);
        googleMap.addMarker(new MarkerOptions().position(location).title(HotelName));
        googleMap.animateCamera(update);


    }
}
