package com.bcit.parkfinder;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ParkDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String name;
    private double[] coordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_detail);

        Intent intent = getIntent();

        // Retrieve the park info
        name = intent.getStringExtra("name");
        coordinates = intent.getDoubleArrayExtra("coordinates");

        // Park name
        TextView nameView = findViewById(R.id.tvDetailName);
        nameView.setText(name);

        // Park coordinates
        TextView coordinatesView = findViewById(R.id.tvCoordinates);
        coordinatesView.setText(coordinates[0] + ", " + coordinates[1]);

        MapFragment mapFragment = MapFragment.newInstance();
        android.app.FragmentTransaction trans = getFragmentManager().beginTransaction();
        trans.add(R.id.parkDetail, mapFragment);
        trans.commit();
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker for park and move the camera
        LatLng parkLocation = new LatLng(coordinates[0], coordinates[1]);
        mMap.addMarker(new MarkerOptions().position(parkLocation).title(name));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parkLocation, 6.0f));
    }

//    @Override
//    public void onMapReady(GoogleMap map) {
//        map.addMarker(new MarkerOptions()
//                .position(new LatLng(0, 0))
//                .title("Marker"));
//    }

}
