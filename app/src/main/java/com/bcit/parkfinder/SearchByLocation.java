package com.bcit.parkfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SearchByLocation extends AppCompatActivity {

    Spinner spLocation;
    Button btnSearch;
    String keyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_location);

        // get neighborhoods from PARK table
        spLocation = findViewById(R.id.spLocation);
        String[] locations = getLocations();

        // attach ArrayAdapter containing neighborhoods to spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, locations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLocation.setAdapter(adapter);
        spLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView item = (TextView) view;
                keyword = item.getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // send chosen neighborhood to ParkListAcivity through intent
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchByLocation.this, ParkListActivity.class);
                intent.putExtra("mode", "location");
                intent.putExtra("keyword", keyword);
                startActivity(intent);
            }
        });
    }

    /**
     * Gets all neighborhood names from PARK table and appends them to a list.
     * @return String[] list of all neighborhood names in PARK table.
     */
    private String[] getLocations() {
        SQLiteOpenHelper helper = new DBHelper(this);
        SQLiteDatabase db;
        String[] locations = null;
        try {
            db = helper.getReadableDatabase();
            Cursor cursor= db.rawQuery("SELECT DISTINCT NEIGHBORHOOD_NAME from PARK ORDER BY NEIGHBORHOOD_NAME", null);

            int count = cursor.getCount();
            locations = new String[count];

            if (cursor.moveToFirst()) {
                int ndx=0;
                do {
                    locations[ndx++] = cursor.getString(0);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException sqlex) {
            String msg = "[SearchByLocation / getLocations] DB unavailable";
            msg += "\n\n" + sqlex.toString();

            Toast t = Toast.makeText(this, msg, Toast.LENGTH_LONG);
            t.show();
        }
        return locations;
    }

}
