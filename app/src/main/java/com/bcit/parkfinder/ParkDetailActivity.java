package com.bcit.parkfinder;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_detail);

        Intent intent = getIntent();

        // Retrieve the park info
        Bundle b = intent.getBundleExtra("bundle");
        park = (Park)b.getSerializable("park");

        // Set Facilities, Features, and Favourite information onto the Park object.
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        helper.setParkFacilities(db, park);
        helper.setParkFeatures(db, park);
        helper.setParkFavourite(db, park);


        fillTextViews();
        updateFavouriteButtonIconAndText();

        // Attaches functionality to the favourite button, allowing users to favourite parks.
        Button favoritesButton = findViewById(R.id.btnFavorite);
        favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFavouritedStatus();
            }
        });


    }

    /**
     * Fills all TextViews on the ParkDetail activity with information such as the name of the park,
     * the neighborhood that the park is located in, and the park's features/facilites.
     */
    protected void fillTextViews(){
        // Park name
        TextView nameView = findViewById(R.id.tvDetailName);
        nameView.setText(park.getName());

        // populate address of park (street number + street name)
        TextView addressView = findViewById(R.id.tvAddress);
        addressView.append(park.getStreetNumber() + " " + park.getStreetName());

        // populate neighbourhood url
        TextView neighbourhoodURLView = findViewById(R.id.tvNeighbourhoodURL);
        String url = "<a href=\"" + park.getNeighbourhoodurl() + "\">" + park.getNeighbourhoodName() +"</a>";
        neighbourhoodURLView.setText(Html.fromHtml(url));
        neighbourhoodURLView.setMovementMethod(LinkMovementMethod.getInstance());

        // populate washroom view with detail describing availability
        TextView washroomView = findViewById(R.id.tvWashroom);
        washroomView.append(park.getWashroomFormattedString());

        // populate features and facilities TextView if data is found otherwise append message
        TextView featuresFacilitiesView = findViewById(R.id.tvFeatures);
        String[] data = park.getCombinedFeaturesFacilities();
        if(data == null || data.length == 0){
            featuresFacilitiesView.append("No features or facilities found");
        } else {
            featuresFacilitiesView.append(TextUtils.join("\n", data));
        }

    }

    /**
     * Updates the Favourite Icon with a different text and appearance when it is toggled.
     */
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

    /**
     * Sets the Park's favourite status.
     */
    protected void updateFavouritedStatus() {
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        if(park.isFavourite()){
            //remove from list
            System.out.println("REMOVING FROM LIST");
            park.setFavourite(false);
            helper.removeFavPark(db, park.getParkId());
        } else{
            //adding to list
            System.out.println("ADDING TO LIST");
            park.setFavourite(true);
            helper.insertFavPark(db, park.getParkId());
        }

        updateFavouriteButtonIconAndText();
    }


}
