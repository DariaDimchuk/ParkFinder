package com.bcit.parkfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class ParksAdapter extends ArrayAdapter<Park> {
    Context _context;
    int position;

    public ParksAdapter(Context context, ArrayList<Park> parks) {
        super(context, 0, parks);
        _context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Activity activity = (Activity) _context;
        // Get the data item for this position

        this.position = position;
        Park park = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row_layout, parent, false);
        }
        // Lookup view for data population
        TextView tvTitle = convertView.findViewById(R.id.tvName);
        TextView tvAddress = convertView.findViewById(R.id.tvAddress);

        // Populate the data into the template view using the data object
        tvTitle.setText(park.getName());
        tvAddress.setText(park.getStreetNumber() + " " + park.getStreetName());


        ImageButton detailsIcon = convertView.findViewById(R.id.btnInfoDetails);
        detailsIcon.setFocusable(false); //allows click listener for list row to work

        detailsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Park park = getItem(position);

                Intent intent = new Intent(_context, ParkDetailActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("park", park);
                intent.putExtra("bundle", b);

                _context.startActivity(intent);
            }
        });


        // Return the completed view to render on screen
        return convertView;
    }
}


