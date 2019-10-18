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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ParkListActivity extends AppCompatActivity {

    private String TAG = ParkListActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;
    // URL to get contacts JSON
    private static String SERVICE_URL = "https://opendata.vancouver.ca/api/records/1.0/search/?dataset=parks&rows=300&facet=specialfeatures&facet=facilities&facet=washrooms&facet=neighbourhoodname";
    private ArrayList<Park> parkList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_list);

        parkList = new ArrayList<Park>();
        lv = findViewById(R.id.parkList);
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
                        Park park = new Park();
                        park.setName(name);
                        park.setCoordinates(coordinates);

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
            lv.setAdapter(adapter);

            // On Click
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                {
                    Intent intent = new Intent(ParkListActivity.this, ParkDetailActivity.class);
                    Park park = parkList.get(i);
                    intent.putExtra("name", park.getName());
                    intent.putExtra("coordinates", park.getCoordinates());
                    startActivity(intent);
                }
            });

        }
    }

}
