package com.bcit.parkfinder;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ParkDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Park park;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_detail);

        Intent intent = getIntent();

        // Retrieve the park info
        Bundle b = intent.getBundleExtra("bundle");
        park = (Park)b.getSerializable("park");

        fillTextViews();

//        MapFragment mapFragment = MapFragment.newInstance();
//        android.app.FragmentTransaction trans = getFragmentManager().beginTransaction();
//        trans.add(R.id.parkDetail, mapFragment);
//        trans.commit();
//        mapFragment.getMapAsync(this);


        Button favoritesButton = findViewById(R.id.btnFavorite);
        favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFavouritedStatus();
            }
        });


    }


    protected void fillTextViews(){
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
        if(park.getFacility() == null || park.getFacility().length == 0){
            facilityView.setVisibility(View.GONE);
        } else {
            facilityView.append("\n\n - " + TextUtils.join("\n - ", park.getFacility()));
        }



        TextView featuresView = findViewById(R.id.tvFeatures);
        if(park.getFeature() == null || park.getFeature().length == 0){
            featuresView.setVisibility(View.GONE);
        } else {
            featuresView.append("\n\n - " + TextUtils.join("\n - ", park.getFeature()));
        }

        //featuresView.append("\n\n"); //creates space at bottom of view

    }
    
    
    protected void updateFavouriteButtonIconAndText(){
        Button favouriteButton = findViewById(R.id.btnFavorite);

        if(park.isFavourite()){
            favouriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star, 0, 0, 0);
            favouriteButton.setText(R.string.removeFavourite);
        } else {
            favouriteButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.empty_star, 0, 0, 0);
            favouriteButton.setText(R.string.addFavourite);
        }

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


    protected void updateFavouritedStatus(){
        if(park.isFavourite()){
            //remove from list
            System.out.println("REMOVING FROM LIST"); //TODO change to update db
            park.setFavourite(false);


//            try {
//                SQLiteOpenHelper helper = new DBHelper(this);
//                db = helper.getWritableDatabase();
//
//                String query = "SELECT * FROM FAV_PARK WHERE DELETED = 0 AND PARK_ID = " + park.getParkId();
//                Cursor favouriteCursor = db.rawQuery(query, null);
//
//                if(favouriteCursor.moveToFirst()){
//                    park.setFavourite(true);
//                } else {
//                    park.setFavourite(false);
//                }
//
//                updateFavouriteButtonIconAndText();
//
//                db.close();
//            } catch (SQLiteException sqle) {
//                System.out.println(sqle+"");
//            }



        } else{
            //adding to list
            System.out.println("ADDING TO LIST"); //TODO change to update db
            park.setFavourite(true);
        }

        updateFavouriteButtonIconAndText();
    }

}
