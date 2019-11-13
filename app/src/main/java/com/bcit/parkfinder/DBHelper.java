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

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "VanParks.db";
    private static final int DB_VERSION = 2;
    private Context context;

    private String TAG = ParkListActivity.class.getSimpleName();
    private static String[] SERVICE_URL = {
            "https://opendata.vancouver.ca/api/records/1.0/search/?dataset=parks&rows=300",
            "https://opendata.vancouver.ca/api/records/1.0/search/?dataset=parks-facilities&rows=1000",
            "https://opendata.vancouver.ca/api/records/1.0/search/?dataset=parks-special-features&rows=100"
    };

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
            for (int dataN = 0; dataN < 3; dataN++) {

                String jsonStr = null;

                // Making a request to url and getting response
                try {
                    jsonStr = sh.makeServiceCall(SERVICE_URL[dataN]);
                } catch (final IOException ioe) {
                    Log.e(TAG, "Json parsing error: " + ioe.getMessage());
                }
                Log.e(TAG, "Response from url " + dataN + ": " + jsonStr);

                if (jsonStr != null) {
                    try {
                        // Getting JSON Array node
                        JSONObject o = new JSONObject(jsonStr);
                        JSONArray parkJsonArray = o.getJSONArray("records");

                        // Initialize SQL helper
                        SQLiteOpenHelper helper = new DBHelper(context);
                        SQLiteDatabase db = helper.getWritableDatabase();

                        // looping through All Items
                        for (int i = 0; i < parkJsonArray.length(); i++) {
                            // Park Info
                            JSONObject p = parkJsonArray.getJSONObject(i).getJSONObject("fields");
                            int parkId = p.getInt("parkid");

                            // First Json Data - Park (Table: PARK)
                            if (dataN == 0) {
                                JSONArray coordinatesArray = p.getJSONArray("googlemapdest");
                                double latitude = coordinatesArray.getDouble(0);
                                double longitude = coordinatesArray.getDouble(1);
                                String name = p.getString("name");
                                String washroom = p.getString("washrooms");
                                String neighbourhoodName = p.getString("neighbourhoodname");
                                String neighbourhoodUrl = p.getString("neighbourhoodurl");
                                String streetName = p.getString("streetname");
                                String streetNumber = p.getString("streetnumber");

                                Park park = new Park(parkId, name, latitude, longitude, washroom,
                                        neighbourhoodName, neighbourhoodUrl, streetNumber, streetName);

                                insertPark(db, park);

                            // Second Json Data - Park Facilities (Table: PARK_FACILITY)
                            } else if (dataN == 1) {
                                String facility = p.getString("facilitytype");
                                Park park = new Park();
                                park.setParkId(parkId);
                                park.setFacility(facility);

                                insertParkFacility(db, park);

                            // Third Json Data - Park Feature (Table: PARK_FEATURE)
                            } else {
                                String feature = p.getString("specialfeature");
                                Park park = new Park();
                                park.setParkId(parkId);
                                park.setFeature(feature);

                                insertParkFeature(db, park);
                            }
                        }
                    } catch (final JSONException e) {
                        Log.e(TAG, "Json parsing/DB conversion error: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Couldn't get json from server.");
                }
            }
            Log.e(TAG, "DB Work Done.");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        updateMyDatabase(sqLiteDatabase, i, i1);
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

    private String getCreateParkFacilityTableSql() {
        String sql = "";
        sql += "CREATE TABLE PARK_FACILITY (";
        sql += "ID INTEGER PRIMARY KEY AUTOINCREMENT, ";
        sql += "PARK_ID INTEGER, ";
        sql += "FACILITY TEXT, ";
        sql += "FOREIGN KEY (ID) REFERENCES PARK (PARK_ID));";

        return sql;
    }

    private void insertParkFacility(SQLiteDatabase db, Park park) {
        ContentValues values = new ContentValues();
        values.put("PARK_ID", park.getParkId());
        values.put("FACILITY", park.getFacility());

        db.insert("PARK_FACILITY", null, values);
    }

    private String getCreateParkFeatureTableSql() {
        String sql = "";
        sql += "CREATE TABLE PARK_FEATURE (";
        sql += "ID INTEGER PRIMARY KEY AUTOINCREMENT, ";
        sql += "PARK_ID INTEGER, ";
        sql += "FEATURE TEXT, ";
        sql += "FOREIGN KEY (ID) REFERENCES PARK (PARK_ID));";

        return sql;
    }

    private void insertParkFeature(SQLiteDatabase db, Park park) {
        ContentValues values = new ContentValues();
        values.put("PARK_ID", park.getParkId());
        values.put("FEATURE", park.getFeature());

        db.insert("PARK_FEATURE", null, values);
    }

    private String getCreateFavParkTableSql() {
        String sql = "";
        sql += "CREATE TABLE FAV_PARK (";
        sql += "FAV_ID INTEGER PRIMARY KEY AUTOINCREMENT, ";
        sql += "PARK_ID INTEGER, ";
        sql += "DELETED INTEGER DEFAULT 0, ";
        sql += "FOREIGN KEY (FAV_ID) REFERENCES PARK (PARK_ID));";

        return sql;
    }

    // For testing purpose -- To be deleted later
    private void insertFavPark(SQLiteDatabase db, int parkId) {
        ContentValues values = new ContentValues();
        values.put("PARK_ID", parkId);
        db.insert("FAV_PARK", null, values);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(TAG, "DB OldV: " + oldVersion + "\tnewV: " + newVersion);
        try {

            if (oldVersion < 1) {
                db.execSQL(getCreateParkTableSql());
                db.execSQL(getCreateParkFacilityTableSql());
                db.execSQL(getCreateParkFeatureTableSql());
                db.execSQL(getCreateFavParkTableSql());

                GetParks parkTask = new GetParks();
                parkTask.execute();
            }

            // For testing purpose -- To be deleted later
            if (oldVersion < 2) {
                insertFavPark(db, 1);
                insertFavPark(db, 2);
                insertFavPark(db, 3);
                insertFavPark(db, 4);
                insertFavPark(db, 5);
            }

        } catch (SQLException sqle) {
            String msg = "DB unavailable";
            msg += "\n\n" + sqle.toString();
            Toast t = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            t.show();
        }
    }


}

