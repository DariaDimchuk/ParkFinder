package com.bcit.parkfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class SearchByFeature extends AppCompatActivity {

    Spinner spFeature;
    Spinner spFacility;
    Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_feature);

        spFeature = findViewById(R.id.spFeature);
        final String[] features = getFeatures();
        final String[] selectedFeatures = new String[features.length];

        spFacility = findViewById(R.id.spFacility);
        final String[] facilities = getFacilities();
        final String[] selectedFacilities = new String[facilities.length];

        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchByFeature.this, ParkListActivity.class);
                intent.putExtra("mode", "feature");
                intent.putExtra("features", selectedFeatures);
                intent.putExtra("facilities", selectedFacilities);
                startActivity(intent);
            }
        });

        ArrayAdapter<String> featuresAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, features);
        featuresAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFeature.setAdapter(featuresAdapter);

        ArrayAdapter<String> facilitiesAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, facilities);
        facilitiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFacility.setAdapter(facilitiesAdapter);


        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchByFeature.this, ParkListActivity.class);
                startActivity(intent);
            }
        });
    }

    private String[] getFeatures() {
        SQLiteOpenHelper helper = new DBHelper(this);
        SQLiteDatabase db;
        String[] features = null;
        try {
            db = helper.getReadableDatabase();
            Cursor cursor= db.rawQuery("SELECT DISTINCT FEATURE from PARK_FEATURE ORDER BY FEATURE", null);

            int count = cursor.getCount();
            features = new String[count];

            if (cursor.moveToFirst()) {
                int ndx=0;
                do {
                    features[ndx++] = cursor.getString(0);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException sqlex) {
            String msg = "[SearchByFeatures / getFeatures] DB unavailable";
            msg += "\n\n" + sqlex.toString();

            Toast t = Toast.makeText(this, msg, Toast.LENGTH_LONG);
            t.show();
        }
        return features;
    }

    private String[] getFacilities() {
        SQLiteOpenHelper helper = new DBHelper(this);
        SQLiteDatabase db;
        String[] facilities = null;
        try {
            db = helper.getReadableDatabase();
            Cursor cursor= db.rawQuery("SELECT DISTINCT FACILITY from PARK_FACILITY ORDER BY FACILITY", null);

            int count = cursor.getCount();
            facilities = new String[count];

            if (cursor.moveToFirst()) {
                int ndx=0;
                do {
                    facilities[ndx++] = cursor.getString(0);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException sqlex) {
            String msg = "[SearchByFeatures / getFacilities] DB unavailable";
            msg += "\n\n" + sqlex.toString();

            Toast t = Toast.makeText(this, msg, Toast.LENGTH_LONG);
            t.show();
        }
        return facilities;
    }

}

