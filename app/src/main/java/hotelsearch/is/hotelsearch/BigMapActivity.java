package hotelsearch.is.hotelsearch;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import hotelsearch.is.database.DBAdapter;

public class BigMapActivity extends AppCompatActivity
implements OnMapReadyCallback {
    private GoogleMap map;
    DBAdapter myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openDb();
        setContentView(R.layout.activity_big_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.bigmap);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Do nothing since we're in the map section
        if (id == R.id.action_settings) {
            return true;
        }
        // Clicking action_home will open SearchActivity
        if( id == R.id.action_home) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        Cursor cursor = myDb.getAllRows();
        generateMarkers(googleMap,cursor);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(64.1393429, -21.91833799999995), 12));
        googleMap.getUiSettings().setMapToolbarEnabled(true);

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
