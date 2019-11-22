package com.bcit.parkfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

                if (jsonStr != null) {
                    try {
                        // Getting JSON Array node
                        JSONObject o = new JSONObject(jsonStr);
                        JSONArray parkJsonArray = o.getJSONArray("records");

                        // Initialize SQL helper
                        SQLiteOpenHelper helper = new DBHelper(context);
                        SQLiteDatabase db = helper.getWritableDatabase();

                        // looping through All Items
                        int i = 0;
                        for (i = 0; i < parkJsonArray.length(); i++) {
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

                                insertPark(db, parkId, name, latitude, longitude, washroom,
                                        neighbourhoodName, neighbourhoodUrl, streetNumber, streetName);

                            // Second Json Data - Park Facilities (Table: PARK_FACILITY)
                            } else if (dataN == 1) {
                                String facility = p.getString("facilitytype");
                                insertParkFacility(db, parkId, facility);

                            // Third Json Data - Park Feature (Table: PARK_FEATURE)
                            } else if (dataN == 2){
                                String feature = p.getString("specialfeature");
                                insertParkFeature(db, parkId, feature);
                            } else {}
                        }
                        Log.e(TAG, "Response from dataset " + dataN + ": " + i + " rows created.");
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

    /**
     * Creates PARK table to store data obtained from JSON Park data.
     * @return String statement to create Park Table
     */
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

    /**
     * Inserts a record into the PARK Table.
     * @param db SQLiteDatabase
     * @param id int id of the Park
     * @param name String name of the Park
     * @param lat double latitude coordinate of the Park
     * @param lon double longitude coordinate of the Park
     * @param washroom String describing if Park contains washroom
     * @param neighbor String neighborhood the Park exists in
     * @param url String url of the neighborhood the Park exists in
     * @param stNum String street number of the Park
     * @param stName String street name of the Park
     */
    private void insertPark(SQLiteDatabase db, int id, String name, double lat, double lon,
                            String washroom, String neighbor, String url, String stNum, String stName) {
        ContentValues values = new ContentValues();
        values.put("PARK_ID", id);
        values.put("NAME", name);
        values.put("LATITUDE", lat);
        values.put("LONGITUDE", lon);
        values.put("WASHROOM", washroom);
        values.put("NEIGHBORHOOD_NAME", neighbor);
        values.put("NEIGHBORHOOD_URL", url);
        values.put("STREET_NUMBER", stNum);
        values.put("STREET_NAME", stName);

        db.insert("PARK", null, values);
    }

    /**
     * Creates PARK_FACILITY table to store data obtained from the JSON Park data.
     * @return String statement to create PARK_FACILITY Table
     */
    private String getCreateParkFacilityTableSql() {
        String sql = "";
        sql += "CREATE TABLE PARK_FACILITY (";
        sql += "ID INTEGER PRIMARY KEY AUTOINCREMENT, ";
        sql += "PARK_ID INTEGER, ";
        sql += "FACILITY TEXT, ";
        sql += "FOREIGN KEY (ID) REFERENCES PARK (PARK_ID));";

        return sql;
    }

    /**
     * Inserts a record into the PARK_FACILITY Table.
     * @param db SQLiteDatabase object
     * @param park_id int Park Id
     * @param facility String name of Park facility
     */
    private void insertParkFacility(SQLiteDatabase db, int park_id, String facility) {
        ContentValues values = new ContentValues();
        values.put("PARK_ID", park_id);
        values.put("FACILITY", facility);

        db.insert("PARK_FACILITY", null, values);
    }

    /**
     * Creates PARK_FEATURE Table.
     * @return String statement to create PARK_FEATURE
     */
    private String getCreateParkFeatureTableSql() {
        String sql = "";
        sql += "CREATE TABLE PARK_FEATURE (";
        sql += "ID INTEGER PRIMARY KEY AUTOINCREMENT, ";
        sql += "PARK_ID INTEGER, ";
        sql += "FEATURE TEXT, ";
        sql += "FOREIGN KEY (ID) REFERENCES PARK (PARK_ID));";

        return sql;
    }

    /**
     * Inserts record into the PARK_FEATURE table.
     * @param db SQLiteDatabase object
     * @param park_id int Park id
     * @param feature String name of Park feature
     */
    private void insertParkFeature(SQLiteDatabase db, int park_id, String feature) {
        ContentValues values = new ContentValues();
        values.put("PARK_ID", park_id);
        values.put("FEATURE", feature);

        db.insert("PARK_FEATURE", null, values);
    }

    /**
     * Creates FAV_PARK table to store user favourited Parks.
     * @return String statement to create FAV_PARK table
     */
    private String getCreateFavParkTableSql() {
        String sql = "";
        sql += "CREATE TABLE FAV_PARK (";
        sql += "FAV_ID INTEGER PRIMARY KEY AUTOINCREMENT, ";
        sql += "PARK_ID INTEGER, ";
        sql += "DELETED INTEGER DEFAULT 0, ";
        sql += "FOREIGN KEY (FAV_ID) REFERENCES PARK (PARK_ID));";

        return sql;
    }


    /**
     * Inserts a record into the FAV_PARK table.
     * @param db SQLiteDatabase object
     * @param parkId int Park id
     */
    public void insertFavPark(SQLiteDatabase db, int parkId) {
        ContentValues values = new ContentValues();
        values.put("PARK_ID", parkId);
        db.insert("FAV_PARK", null, values);
    }

    /**
     * Soft deletes record in FAV_PARK table by updating the DELETED column to 1.
     * @param db SQLiteDatabase object
     * @param parkId int Park id
     */
    public void removeFavPark(SQLiteDatabase db, int parkId) {
        ContentValues values = new ContentValues();
        values.put("DELETED", 1);
        db.update("FAV_PARK", values, "PARK_ID=" + parkId, null);
    }

    /**
     * Queries PARK_FEATURE and updates Park's feature attribute with features.
     * @param db SQLiteDatabase object
     * @param park Park object
     */
    void setParkFeatures(SQLiteDatabase db, Park park) {
        Cursor cursor = db.rawQuery("SELECT FEATURE FROM PARK_FEATURE WHERE PARK_ID = " + park.getParkId(), null);
        int numFeatures = cursor.getCount();
        if (cursor.moveToFirst()) {
            String[] features = new String[numFeatures];
            for (int i = 0; i < numFeatures; i++) {
                features[i] = cursor.getString(0);
                cursor.moveToNext();
            }
            park.setFeature(features);
        }
    }

    /**
     * Queries PARK_FACILITY and updates Park's facility attribute with facilities.
     * @param db SQLiteDatabase object
     * @param park Park object
     */
    void setParkFacilities(SQLiteDatabase db, Park park) {
        Cursor cursor = db.rawQuery("SELECT FACILITY FROM PARK_FACILITY WHERE PARK_ID = " + park.getParkId(), null);
        int numFacilities = cursor.getCount();
        if (cursor.moveToFirst()) {
            String[] facilities = new String[numFacilities];
            for (int i = 0; i < numFacilities; i++) {
                facilities[i] = cursor.getString(0);
                cursor.moveToNext();
            }
            park.setFacility(facilities);
        }
    }

    /**
     * Sets the Park object's favourite attribute if found in the FAV_PARK table.
     * @param db SQLiteDatabase object
     * @param park Park object
     */
    void setParkFavourite(SQLiteDatabase db, Park park) {
        Cursor favCursor = db.rawQuery("SELECT FAV_ID FROM FAV_PARK WHERE DELETED = 0 AND PARK_ID = " + park.getParkId(), null);
        park.setFavourite(favCursor.getCount() > 0);
    }

    /**
     * Update database version numbers and recreates tables containing updated park data.
     * @param db SQLiteDatabase object
     * @param oldVersion int old version of the database
     * @param newVersion int new version of the database
     */
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

        } catch (SQLException sqle) {
            String msg = "DB unavailable";
            msg += "\n\n" + sqle.toString();
            Toast t = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            t.show();
        }
    }


}

