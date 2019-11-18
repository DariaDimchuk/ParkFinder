package com.bcit.parkfinder;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class ParkListActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String mode = "";
    private String keyword = "";

    private String TAG = ParkListActivity.class.getSimpleName();
    private ListView lvParksList;

    private ArrayList<Park> parkList;
    private ArrayList<String> features;
    private GoogleMap mMap;

    SQLiteOpenHelper helper;
    private SQLiteDatabase db;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_list);

        Intent intent = getIntent();    //creator
        mode = intent.getStringExtra("mode") == null ? "" : intent.getStringExtra("mode");
        if (mode.equals("feature") ) {
            features = intent.getStringArrayListExtra("features");
        } else if (!mode.isEmpty()) {
            keyword = intent.getStringExtra("keyword");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        parkList = new ArrayList<Park>();
        lvParksList = findViewById(R.id.lvParkList);
        new GetParksTask().execute();
    }

    @Override
    public void onRestart() {
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        super.onRestart();

        //When it's a favourite mode, update the park list to reflect the newly added/removed fav park.
        if (mode.equals("favourite")) {
            System.out.println("REFRESH THE PARK LIST");
            new GetParksTask().execute();
        }
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
            helper = new DBHelper(ParkListActivity.this);

            String baseSQL = "SELECT DISTINCT * FROM PARK";

            try {
                parkList.clear();
                db = helper.getReadableDatabase();

                String whereSQL = "";
                if (mode.equals("name")) {
                    whereSQL = " WHERE NAME LIKE '%" + keyword + "%'";

                } else if (mode.equals("location")) {
                    whereSQL = " WHERE NEIGHBORHOOD_NAME = '" + keyword + "'";

                } else if (mode.equals("favourite")) {
                    whereSQL = " WHERE PARK_ID IN (SELECT PARK_ID FROM FAV_PARK WHERE DELETED = 0)";

                } else if (mode.equals("feature") && features.size() > 0) {
                    baseSQL = "SELECT DISTINCT P.PARK_ID, NAME, LATITUDE, LONGITUDE, WASHROOM," +
                        " NEIGHBORHOOD_NAME, NEIGHBORHOOD_URL, STREET_NUMBER, STREET_NAME FROM PARK P " +
                        "LEFT JOIN (SELECT park_id, feature AS feature FROM park_feature UNION " +
                        "SELECT park_id, facility AS feature FROM park_facility) f ON f.park_id = p.park_id";

                    int num = features.size();
                    String conditions = "(";
                    for (int i = 0; i < num; i++) {
                        conditions += "'" + features.get(i) + (i == num - 1 ? "')" : "', ");
                    }
                    whereSQL = " WHERE feature IN " + conditions +
                            " GROUP BY p.park_id " +
                            " HAVING COUNT(DISTINCT feature) = " + num ;
                    System.out.println(baseSQL + whereSQL);
                }

                Cursor cursor = db.rawQuery(baseSQL + whereSQL + " ORDER BY NAME", null);
                System.out.println(baseSQL + whereSQL);

                if (cursor.moveToFirst()) {
                    do {
                        // Creating Park Object
                        int id = cursor.getInt(0);
                        String name = cursor.getString(1);
                        double latitude = cursor.getDouble(2);
                        double longitude = cursor.getDouble(3);
                        String washroom = cursor.getString(4);
                        String neighbourhoodName = cursor.getString(5);
                        String neighbourhoodURL = cursor.getString(6);
                        String stNumber = cursor.getString(7);
                        String stName = cursor.getString(8);
                        Park park = new Park(id, name, latitude, longitude, washroom, neighbourhoodName,
                                neighbourhoodURL, stNumber, stName);

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
            TextView tvNoResult = findViewById(R.id.tvNoResult);

            if (parkList.isEmpty()) {
                tvNoResult.setVisibility(View.VISIBLE);
            } else {

                tvNoResult.setVisibility(View.GONE);

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
                        String parkName = park.getName();

                        // add marker and move camera position to park location
                        LatLng parkLocation = new LatLng(latitude, longitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parkLocation, 14f));
                        Marker marker = mMap.addMarker(new MarkerOptions().position(parkLocation).title(parkName));
                        marker.showInfoWindow();
                    }
                });
            }

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

        // Vancouver by default, if the result is not empty, update it with the first park's position
        LatLng location = new LatLng(49.246292, -123.116226);
        if (!parkList.isEmpty()) {
            Park park = parkList.get(0);
            location = new LatLng(park.getLatitude(), park.getLongitude());
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

}
