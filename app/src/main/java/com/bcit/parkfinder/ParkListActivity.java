package com.bcit.parkfinder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ParkListActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String TAG = ParkListActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lvParks;

    // URL to get contacts JSON
    private static String SERVICE_URL = "https://opendata.vancouver.ca/api/records/1.0/search/?dataset=parks&rows=300&facet=specialfeatures&facet=facilities&facet=washrooms&facet=neighbourhoodname";
    private ArrayList<Park> parkList;

    private GoogleMap mMap;
    private String parkName;
    private double[] parkCoordinates;

    private ArrayList<double[]> coordinatesList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_list);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        parkList = new ArrayList<Park>();
        lvParks = findViewById(R.id.lvParkList);
        new GetContacts().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ParkListActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            HttpHandler sh = new HttpHandler();
            String jsonStr = null;

            // Making a request to url and getting response
            try
            {
                jsonStr = sh.makeServiceCall(SERVICE_URL);
            }
            catch (final IOException ioe)
            {
                Log.e(TAG, "Json parsing error: " + ioe.getMessage());
            }

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null)
            {
                try
                {
                    // Getting JSON Array node
                    JSONObject o = new JSONObject(jsonStr);
                    JSONArray parkJsonArray = o.getJSONArray("records");

                    // looping through All Items
                    for (int i = 0; i < parkJsonArray.length(); i++)
                    {
                        // Park Info
                        JSONObject p = parkJsonArray.getJSONObject(i).getJSONObject("fields");

                        // authors (array)
                        JSONArray coordinatesArray = p.getJSONArray("googlemapdest");
                        double[] coordinates = new double[coordinatesArray.length()];
                        for(int c = 0; c < coordinatesArray.length(); c++) {
                            coordinates[c] = coordinatesArray.getDouble(c);
                        }

                        String name = p.getString("name");

                        // Creating a Park object
                        Park park = new Park(name, coordinates);

                        parkList.add(park);
                    }
                }
                catch (final JSONException e)
                {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            }
            else
            {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            ParksAdapter adapter = new ParksAdapter(ParkListActivity.this, parkList);

            // Attach the adapter to a ListView
            lvParks.setAdapter(adapter);

            // adds all park markers to the map fragment
            addAllParkMarkers();

            // move camera position to location of chosen park
            lvParks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // get park details
                    Park park = parkList.get(position);
                    parkName = park.getName();
                    parkCoordinates = park.getCoordinates();

                    // add marker and move camera position to park location
                    LatLng parkLocation = new LatLng(parkCoordinates[0], parkCoordinates[1]);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parkLocation, 12.5f));
                }
            });

        }
    }

    public void addAllParkMarkers() {
        mMap.clear();
        for(Park park: parkList) {

            // Get park details
            String parkName = park.getName();
            double[] coordinates = park.getCoordinates();

            // Add a marker for park in parkList
            LatLng parkLocation = new LatLng(coordinates[0], coordinates[1]);
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

    // On Click
//            lvParks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
//                {
//                    Intent intent = new Intent(ParkListActivity.this, ParkDetailActivity.class);
//                    Park park = parkList.get(i);
//                    intent.putExtra("name", park.getName());
//                    intent.putExtra("coordinates", park.getCoordinates());
//                    startActivity(intent);
//                }
//            });

}
