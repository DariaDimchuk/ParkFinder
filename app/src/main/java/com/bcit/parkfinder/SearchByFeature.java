package com.bcit.parkfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchByFeature extends AppCompatActivity {

    Button btnSearch;
    Button btnClear;

    private ListView lvFeaturesList;
    ArrayList<Feature> featureList;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_feature);

        final ArrayList<Feature> chosenFeatures = new ArrayList<Feature>();
        lvFeaturesList = findViewById(R.id.lvFeatureList);

        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> chosenFeatures = getChosenFeatures();
                for (String feature: chosenFeatures) {
                    Log.d("features", feature);
                }

                Intent intent = new Intent(SearchByFeature.this, ParkListActivity.class);
                intent.putExtra("mode", "feature");
                intent.putStringArrayListExtra("features", chosenFeatures);
                startActivity(intent);
            }
        });

        btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllCheckedFeatures();
            }
        });

        new GetFeaturesTask().execute();
    }


    /**
     * Async task class to get SQL query result from DB
     */
    private class GetFeaturesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            SQLiteOpenHelper helper = new DBHelper(SearchByFeature.this);
            SQLiteDatabase db;
            try {
                db = helper.getReadableDatabase();
                Cursor cursor= db.rawQuery("SELECT DISTINCT FEATURE AS NAME from PARK_FEATURE " +
                        "UNION SELECT DISTINCT FACILITY AS NAME from PARK_FACILITY ORDER BY NAME", null);

                featureList = new ArrayList<Feature>();

                if (cursor.moveToFirst()) {
                    do {
                        Feature feature = new Feature(cursor.getString(0));
                        featureList.add(feature);

                    } while (cursor.moveToNext());
                }

            } catch (SQLiteException sqlex) {
                String msg = "[SearchByFeatures / doInBackground] DB unavailable";
                msg += "\n\n" + sqlex.toString();

                Toast t = Toast.makeText(SearchByFeature.this, msg, Toast.LENGTH_LONG);
                t.show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Attach the adapter to a ListView
            FeatureAdapter adapter = new FeatureAdapter(SearchByFeature.this, featureList);
            lvFeaturesList.setAdapter(adapter);
        }
    }

    private ArrayList<String> getChosenFeatures(){

        ArrayList<String> chosenFeatures = new ArrayList<String>();
        for (Feature feature: featureList) {
            if (feature.isChecked()){
                chosenFeatures.add(feature.getFeatureName());
            }
        }
        return chosenFeatures;
    }

    private void clearAllCheckedFeatures(){
        for (int i = 0; i < lvFeaturesList.getLastVisiblePosition() - lvFeaturesList.getFirstVisiblePosition(); i++) {
            View child = lvFeaturesList.getChildAt(i);

            CheckBox checkbox = child.findViewById(R.id.checkboxFeature);
            checkbox.setChecked(false);
        }
    }
}



