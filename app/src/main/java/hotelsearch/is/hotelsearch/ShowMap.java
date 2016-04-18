package hotelsearch.is.hotelsearch;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import hotelsearch.is.database.DBAdapter;

public class ShowMap extends FragmentActivity
        implements OnMapReadyCallback {
    private GoogleMap map;
    private String[] info = new String[6];
    private String HotelName = "Missing";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        // Fetch data from intent (previous screen and build Info)
        Intent intent = getIntent();
        info = intent.getStringArrayExtra(DisplayMessageActivity.HOTEL_INFO);




        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




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

            TextView layout1 = (TextView) findViewById(R.id.header);
            layout1.setText(info);
            // build latitude + longitude
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
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location,17);
        googleMap.addMarker(new MarkerOptions().position(location).title(HotelName));
        googleMap.animateCamera(update);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

    }
}
