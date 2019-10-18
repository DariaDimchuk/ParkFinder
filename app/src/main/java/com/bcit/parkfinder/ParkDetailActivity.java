package com.bcit.parkfinder;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ParkDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_detail);

        Intent intent = getIntent();

        // Retrieve the park info
        String name = intent.getStringExtra("name");

        TextView nameView = findViewById(R.id.tvDetailName);
        nameView.setText(name);

    }

}
