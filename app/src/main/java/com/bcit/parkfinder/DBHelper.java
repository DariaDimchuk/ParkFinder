package com.bcit.parkfinder;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "VanParks.db";
    private static final int DB_VERSION = 2;
    private Context context;

    private String TAG = ParkListActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ArrayList<Park> parkList = new ArrayList<Park>();
    private static String SERVICE_URL = "https://opendata.vancouver.ca/api/records/1.0/search/?dataset=parks&rows=300&facet=specialfeatures&facet=facilities&facet=washrooms&facet=neighbourhoodname";

    public DBHelper(Context context) {
        // The 3'rd parameter (null) is an advanced feature relating to cursors
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        updateMyDatabase(sqLiteDatabase, 0, DB_VERSION);
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetParks extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            HttpHandler sh = new HttpHandler();
            String jsonStr = null;

            // Making a request to url and getting response
            try {
                jsonStr = sh.makeServiceCall(SERVICE_URL);
            } catch (final IOException ioe) {
                Log.e(TAG, "Json parsing error: " + ioe.getMessage());
            }

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    // Getting JSON Array node
                    JSONObject o = new JSONObject(jsonStr);
                    JSONArray parkJsonArray = o.getJSONArray("records");

                    // looping through All Items
                    for (int i = 0; i < parkJsonArray.length(); i++) {
                        // Park Info
                        JSONObject p = parkJsonArray.getJSONObject(i).getJSONObject("fields");
                        JSONArray coordinatesArray = p.getJSONArray("googlemapdest");
                        double latitude = coordinatesArray.getDouble(0);
                        double longitude = coordinatesArray.getDouble(1);
                        String name = p.getString("name");
                        int parkId = p.getInt("parkid");
                        String washroom = p.getString("washrooms");
                        String neighbourhoodName = p.getString("neighbourhoodname");
                        String neighbourhoodUrl = p.getString("neighbourhoodurl");
                        String streetName = p.getString("streetname");
                        String streetNumber = p.getString("streetnumber");

                        // Creating a Park object
                        Park park = new Park(parkId, name, latitude, longitude, washroom,
                                    neighbourhoodName, neighbourhoodUrl, streetNumber, streetName);

                        // Insert a park to the DB
                        SQLiteOpenHelper helper = new DBHelper(context);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        insertPark(db, park);
                        parkList.add(park);
                        Log.e(TAG, "Park added: " + i);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else  {
                Log.e(TAG, "Couldn't get json from server.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // set parkList to DBHelper
            setParkList(parkList);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        updateMyDatabase(sqLiteDatabase, i, i1);
    }

    public void setParkList(ArrayList<Park> parkList) {
        this.parkList = parkList;
    }

    private String getCreateParkTableSql() {
        String sql = "";
        sql += "CREATE TABLE PARK (";
        sql += "PARK_ID INTEGER PRIMARY KEY, ";
        sql += "NAME TEXT, ";
        sql += "LATITUDE REAL, ";
        sql += "LONGITUDE REAL, ";
        sql += "WASHROOM TEXT, ";
        sql += "NEIGHBORHOOD_NAME TEXT, ";
        sql += "NEIGHBORHOOD_URL TEXT, ";
        sql += "STREET_NUMBER TEXT, ";
        sql += "STREET_NAME TEXT);";

        return sql;
    }


    private void insertPark(SQLiteDatabase db, Park park) {
        ContentValues values = new ContentValues();
        values.put("PARK_ID", park.getParkId());
        values.put("NAME", park.getName());
        values.put("LATITUDE", park.getLatitude());
        values.put("LONGITUDE", park.getLongitude());
        values.put("WASHROOM", park.getWashroom());
        values.put("NEIGHBORHOOD_NAME", park.getNeighbourhoodName());
        values.put("NEIGHBORHOOD_URL", park.getNeighbourhoodurl());
        values.put("STREET_NUMBER", park.getStreetNumber());
        values.put("STREET_NAME", park.getStreetName());

        db.insert("PARK", null, values);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(TAG, "DB OldV: " + oldVersion + "\tnewV: " + newVersion);
        try {
            if (oldVersion < 1) {
                db.execSQL(getCreateParkTableSql());
            }
            if (oldVersion < 2) {
                GetParks parkTask = new GetParks();
                parkTask.execute();
            }
        } catch (SQLException sqle) {
            String msg = "DB unavailable";
            msg += "\n\n" + sqle.toString();
            Toast t = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            t.show();
        }
    }


}

