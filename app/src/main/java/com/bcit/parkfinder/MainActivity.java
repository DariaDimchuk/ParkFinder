package com.bcit.parkfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickSearchByName(View view) {
        Intent intent = new Intent(this, SearchByName.class);
        startActivity(intent);
    }

    public void onClickSeeAll(View view) {
        Intent intent = new Intent(this, ParkListActivity.class);
        startActivity(intent);
    }
}
