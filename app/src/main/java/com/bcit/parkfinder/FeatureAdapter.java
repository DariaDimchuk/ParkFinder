package com.bcit.parkfinder;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.bcit.parkfinder.SearchByFeature.featureList;

public class FeatureAdapter extends ArrayAdapter<Feature> {

    Context _context;

    public FeatureAdapter(Context context, ArrayList<Feature> features) {
        super(context, 0, features);
        this._context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Activity activity = (Activity) _context;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.feature_list_layout, parent, false);
        }

        // Set text of the TextView to the Feature name
        final Feature feature = getItem(position);
        TextView tvFeature = convertView.findViewById(R.id.tvFeature);
        tvFeature.setText(feature.getFeatureName());

        // Attach onCheckChangedListener to update setChecked value of Features
        final CheckBox checkbox = convertView.findViewById(R.id.checkboxFeature);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    feature.setChecked(true);
                }
                else {
                    feature.setChecked(false);
                }
            }
        });

        checkbox.setChecked(feature.isChecked());

        // Attach onClickListener to row in ArrayList allowing user to click entire row to check
        // select a Feature
        LinearLayout row = convertView.findViewById(R.id.featureRow);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkbox = view.findViewById(R.id.checkboxFeature);
                boolean reversed = !checkbox.isChecked();

                checkbox.setChecked(reversed); //triggers onchange checkbox listener


                Button clear = ((Activity) _context).findViewById(R.id.btnClear);
                //if at least one checkbox is on, clear button should be enabled
                if(reversed){
                    clear.setEnabled(true);
                } else {
                    //check - if no checkboxes are on, disable clear button

                    ListView lvFeaturesList = ((Activity) _context).findViewById(R.id.lvFeatureList);
                    boolean foundOne = false;
                    for (Feature feature: featureList) {
                        if(feature.isChecked())
                            foundOne = true;
                    }

                    if(!foundOne){
                        clear.setEnabled(false);
                    }
                }

            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}


