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
    private Park park;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_detail);

        Intent intent = getIntent();

        // Retrieve the park info
        Bundle b = intent.getBundleExtra("bundle");
        park = (Park)b.getSerializable("park");

        // Park name
        TextView nameView = findViewById(R.id.tvDetailName);
        nameView.setText(park.getName());

        // Park coordinates
        TextView coordinatesView = findViewById(R.id.tvCoordinates);
        coordinatesView.setText(park.getLatitude() + ", " + park.getLongitude());

        TextView addressView = findViewById(R.id.tvAddress);
        addressView.setText(park.getStreetNumber() + " " + park.getStreetName());

        TextView neighbourhoodView = findViewById(R.id.tvNeighbourhoodName);
        neighbourhoodView.setText(park.getNeighbourhoodName());

        TextView facilityView = findViewById(R.id.tvFacility);
        if(park.getFacility() == null || park.getFacility().isEmpty()){
            facilityView.setVisibility(View.GONE);
        } else {
            facilityView.setText(park.getFacility());
        }

        TextView washroomView = findViewById(R.id.tvWashroom);
        if(park.getWashroom() == null || park.getWashroom().isEmpty()){
            washroomView.setVisibility(View.GONE);
        } else {
            washroomView.setText(park.getWashroom());
        }

        TextView featuresView = findViewById(R.id.tvFeatures);
        if(park.getFeature() == null || park.getFeature().isEmpty()){
            featuresView.setVisibility(View.GONE);
        } else {
            featuresView.setText(park.getFeature());
        }


//        MapFragment mapFragment = MapFragment.newInstance();
//        android.app.FragmentTransaction trans = getFragmentManager().beginTransaction();
//        trans.add(R.id.parkDetail, mapFragment);
//        trans.commit();
//        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker for park and move the camera
        LatLng parkLocation = new LatLng(park.getLatitude(), park.getLongitude());
        mMap.addMarker(new MarkerOptions().position(parkLocation).title(park.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parkLocation, 12.5f));
    }

//    @Override
//    public void onMapReady(GoogleMap map) {
//        map.addMarker(new MarkerOptions()
//                .position(new LatLng(0, 0))
//                .title("Marker"));
//    }

}
