package hotelsearch.is.hotelsearch;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
//import com.android.support:appcompat-v7:23.0.1;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import hotelsearch.is.database.DBAdapter;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback {
    private GoogleMap map;
    DBAdapter myDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        openDb();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void openDb() {
        myDb= new DBAdapter(this);
        myDb.open();
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //googleMap.addMarker(new MarkerOptions()
        //        .position(new LatLng(10, 10))
        //        .title("Hello world"));
        Cursor cursor = myDb.getAllRows();
        generateMarkers(googleMap,cursor);

        //LatLng location = buildInfo(info);
        //CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location,17);
        //googleMap.addMarker(new MarkerOptions().position(location).title(HotelName));
        //googleMap.animateCamera(update);
        //
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(64.1393429, -21.91833799999995), 12));
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

    }

    public void generateMarkers(GoogleMap googleMap, Cursor cursor){
        String message ="";

        if(cursor.moveToFirst()){
            do{
                String[] latlong =  cursor.getString(DBAdapter.COL_LATLNG).split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                //LatLng location = new LatLng(latitude, longitude);
                String name = cursor.getString(DBAdapter.COL_NAME);


                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title(name));

            }while(cursor.moveToNext());
        }

        cursor.close();
    }
}

