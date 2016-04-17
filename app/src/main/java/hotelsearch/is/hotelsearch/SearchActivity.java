package hotelsearch.is.hotelsearch;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import hotelsearch.is.database.DBAdapter;

public class SearchActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "hotelsearch.is.hotelsearch.MESSAGE";

    DBAdapter myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        openDb();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /** Called when the user clicks the Search button */

    public void searchString(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String s = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE,s);
        startActivity(intent);
    }

    // No button references this right now
    public void onClickAdd(View view){

        long newId = myDb.insertRow("Testhótel", "Aðalgata 2", "101","Reykjavík",
                "Hotel.is","64.1364755,-21.874752100000023");

        Cursor cursor = myDb.getRow(newId);

    }

    public void displayText(String message){
        TextView textView = (TextView) findViewById(R.id.editText);
        textView.setText(message);
    }

    public void onClickView(View view){
        Cursor cursor = myDb.getAllRows();
        displayDbRow(cursor);

    }

    private void displayDbRow(Cursor cursor){
        String message ="";

        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(DBAdapter.COL_ROWID);
                String name = cursor.getString(DBAdapter.COL_NAME);
                String address = cursor.getString(DBAdapter.COL_ADDRESS);
                String zip = cursor.getString(DBAdapter.COL_ZIP);
                String city = cursor.getString(DBAdapter.COL_CITY);
                String www = cursor.getString(DBAdapter.COL_WEBSITE);
                String latlng = cursor.getString(DBAdapter.COL_LATLNG);
                message += "Hotel =" + name
                        +",\nAddress =" + address
                        +", \nGPS coords=" + latlng
                        +"\n";
            }while(cursor.moveToNext());
        }

        cursor.close();

        displayText(message);
    }
}
