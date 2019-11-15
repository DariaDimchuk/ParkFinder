package com.bcit.parkfinder;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

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
        addressView.append(" " + park.getStreetNumber() + " " + park.getStreetName());

        TextView neighbourhoodView = findViewById(R.id.tvNeighbourhoodName);
        neighbourhoodView.append(" " + park.getNeighbourhoodName());

        TextView neighbourhoodURLView = findViewById(R.id.tvNeighbourhoodURL);
        neighbourhoodURLView.setText(park.getNeighbourhoodurl());


        TextView washroomView = findViewById(R.id.tvWashroom);
        washroomView.append(" " + park.getWashroom());

        TextView facilityView = findViewById(R.id.tvFacility);
        if(park.getFacility() == null){
            facilityView.append(" none");
        } else {
            facilityView.append(arrayToString(park.getFacility()));
        }



        TextView featuresView = findViewById(R.id.tvFeatures);
        if(park.getFeature() == null){
            featuresView.append(" none");
        } else {
            featuresView.append(arrayToString(park.getFeature()));
        }

//        MapFragment mapFragment = MapFragment.newInstance();
//        android.app.FragmentTransaction trans = getFragmentManager().beginTransaction();
//        trans.add(R.id.parkDetail, mapFragment);
//        trans.commit();
//        mapFragment.getMapAsync(this);


        Button favoritesButton = findViewById(R.id.btnFavorite);
        favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    public String arrayToString(String[] array) {
        String str = "";
        for (int i = 0; i < array.length; i++) {
            str += array[i] + "\n";
        }
        return str;
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
