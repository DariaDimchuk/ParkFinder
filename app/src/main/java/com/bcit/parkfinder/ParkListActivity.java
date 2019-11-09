package com.bcit.parkfinder;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class ParkListActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String mode = "";
    private String keyword = "";

    private String TAG = ParkListActivity.class.getSimpleName();
    private ListView lvParksList;

    private ArrayList<Park> parkList;
    private GoogleMap mMap;

    private SQLiteDatabase db;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_list);

        Intent intent = getIntent();    //creator
        mode = intent.getStringExtra("mode") == null ? "" : intent.getStringExtra("mode");
        if (mode != null && !mode.isEmpty())
            keyword = intent.getStringExtra("keyword");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        parkList = new ArrayList<Park>();
        lvParksList = findViewById(R.id.lvParkList);
        new GetParksTask().execute();
    }

    /**
     * Async task class to get SQL query result from DB
     */
    private class GetParksTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            SQLiteOpenHelper helper = new DBHelper(ParkListActivity.this);
            try {
                db = helper.getReadableDatabase();

                String whereSQL = "";
                if (mode.equals("name"))
                    whereSQL = " WHERE NAME LIKE '%" + keyword + "%'";
                else if (mode.equals("location"))
                    whereSQL = " WHERE NEIGHBORHOOD_NAME = '" + keyword + "'";

                Cursor cursor= db.rawQuery("SELECT PARK_ID, NAME, LATITUDE, LONGITUDE FROM PARK" + whereSQL + " ORDER BY NAME", null);

                if (cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(0);
                        String name = cursor.getString(1);
                        double latitude = cursor.getDouble(2);
                        double longitude = cursor.getDouble(3);
                        Park park = new Park(id, name, latitude, longitude);
                        parkList.add(park);
                    } while (cursor.moveToNext());
                }
            } catch (SQLiteException sqlex) {
                String msg = "DB unavailable";
                msg += "\n\n" + sqlex.toString();

                Toast t = Toast.makeText(ParkListActivity.this, msg, Toast.LENGTH_LONG);
                t.show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ParksAdapter adapter = new ParksAdapter(ParkListActivity.this, parkList);

            // Attach the adapter to a ListView
            lvParksList.setAdapter(adapter);

            // adds all park markers to the map fragment
            addAllParkMarkers();

            // move camera position to location of chosen park
            lvParksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // get park details
                    Park park = parkList.get(position);
                    double latitude = park.getLatitude();
                    double longitude = park.getLongitude();

                    // add marker and move camera position to park location
                    LatLng parkLocation = new LatLng(latitude, longitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parkLocation, 12.5f));
                }
            });

            // move camera position to location of chosen park
            lvParksList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Intent intent = new Intent(ParkListActivity.this, ParkDetailActivity.class);
                    Park park = parkList.get(position);
                    intent.putExtra("name", park.getName());
                    intent.putExtra("latitude", park.getLatitude());
                    intent.putExtra("longitude", park.getLongitude());
                    startActivity(intent);

                    return false;
                }
            });
        }
    }

    public void addAllParkMarkers() {

        boolean ready = false;
        while (!ready) {
            ready = mMap != null;
        }

        mMap.clear();
        for(Park park: parkList) {

            // Get park details
            String parkName = park.getName();
            double latitude = park.getLatitude();
            double longitude = park.getLongitude();

            // Add a marker for park in parkList
            LatLng parkLocation = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(parkLocation).title(parkName));
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        // moves camera position of the map over vancouver
        LatLng vancouver = new LatLng(49.246292, -123.116226);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vancouver, 11.5f));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

}
