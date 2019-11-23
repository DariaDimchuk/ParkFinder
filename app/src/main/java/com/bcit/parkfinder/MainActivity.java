package com.bcit.parkfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            SQLiteOpenHelper helper = new DBHelper(this);
            db = helper.getWritableDatabase();
            db.close();
        } catch (SQLiteException sqle) {
            System.out.println(sqle+"");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        View v = findViewById(R.id.mainView);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            v.setBackground(getDrawable(R.drawable.background_landscape));
        } else {
            v.setBackground(getDrawable(R.drawable.background));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null)
            db.close();
    }

    /**
     * Directs user to the SearchByFeature activity.
     * @param view View object
     */
    public void onClickSearchByFeature(View view) {
        Intent intent = new Intent(this, SearchByFeature.class);
        startActivity(intent);
    }

    /**
     * Directs user to the SearchByNameActivity.
     * @param view View object
     */
    public void onClickSearchByName(View view) {
        Intent intent = new Intent(this, SearchByName.class);
        startActivity(intent);
    }

    /**
     * Directs user to the SearchByLocationActivity.
     * @param view View object
     */
    public void onClickSearchByLocation(View view) {
        Intent intent = new Intent(this, SearchByLocation.class);
        startActivity(intent);
    }

    /**
     * Directs user to the ParksListActivity.
     * @param view View object
     */
    public void onClickSeeAll(View view) {
        Intent intent = new Intent(this, ParkListActivity.class);
        startActivity(intent);
    }

    /**
     * Directs user to the ParksListActivity.
     * @param view View object
     */
    public void onClickSeeFavourites(View view) {
        Intent intent = new Intent(this, ParkListActivity.class);
        intent.putExtra("mode", "favourite");
        intent.putExtra("keyword", "favourite");
        startActivity(intent);
    }
}
