package com.bcit.parkfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
    protected void onDestroy() {
        super.onDestroy();
        if (db != null)
            db.close();
    }

    public void onClickSearchByName(View view) {
        Intent intent = new Intent(this, SearchByName.class);
        startActivity(intent);
    }

    public void onClickSearchByLocation(View view) {
        Intent intent = new Intent(this, SearchByLocation.class);
        startActivity(intent);
    }

    public void onClickSeeAll(View view) {
        Intent intent = new Intent(this, ParkListActivity.class);
        startActivity(intent);
    }

    public void onClickSeeFavourites(View view) {
        Intent intent = new Intent(this, ParkListActivity.class);
        intent.putExtra("mode", "favourite");
        intent.putExtra("keyword", "favourite");
        startActivity(intent);
    }
}
