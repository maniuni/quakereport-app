package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * {@link EarthquakeAdapter} is an {@link ArrayAdapter} that can provide the layout for each list item
 * based on a data source, which is a list of {@link Earthquake} objects.
 */
public class EarthquakeAdapter extends ArrayAdapter {

    public EarthquakeAdapter(Context context, ArrayList<Earthquake> earthquakes){
        super(context, 0, earthquakes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.earthquake_listitem, parent, false);
        }

        Earthquake earthquake = (Earthquake) getItem(position);

        // Find the TextView in the earthquake_listitem layout
        // and set the text to be the location of the current {@link Earthquake} object
        TextView location_offset = (TextView) listItemView.findViewById(R.id.location_offset);

        location_offset.setText(getOffsetLocation(earthquake.getLocation()));

        // Find the TextView in the earthquake_listitem layout
        // and set the text to be the location of the current {@link Earthquake} object
        TextView location_primary = (TextView) listItemView.findViewById(R.id.location_primary);

        location_primary.setText(getPrimaryLocation(earthquake.getLocation()));

        // Find the TextView in the earthquake_listitem layout
        TextView magnitude = (TextView) listItemView.findViewById(R.id.magnitude);

        // Make a new double variable to hold the full number for the magnitude
        double magnitudeFull = earthquake.getMagnitude();

        // Format the magnitude using 1 decimal place
        DecimalFormat decimalFormatter = new DecimalFormat("0.0");
        String magnitudeFormatted = decimalFormatter.format(magnitudeFull);

        // Set the text to be the magnitude of the current {@link Earthquake} object
        magnitude.setText(magnitudeFormatted);

        // Create a new {@link Date} object from the time in milliseconds for this earthquake
        Date dateObj = new Date(earthquake.getmTimeInMilliseconds());

        // Find the TextView in the earthquake_listitem layout
        TextView date = (TextView) listItemView.findViewById(R.id.date);

        // Format the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, y");
        String dateToShow = dateFormat.format(dateObj);

        // Set the text to be the date of the current {@link Earthquake} object
        date.setText(dateToShow);

        // Find the TextView in the earthquake_listitem layout
        // and set the text to be the date of the current {@link Earthquake} object
        TextView time = (TextView) listItemView.findViewById(R.id.time);

        // Format the time
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        String timeToShow = timeFormat.format(dateObj);

        // Set the text to be the date of the current {@link Earthquake} object
        time.setText(timeToShow);

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) listItemView.findViewById(R.id.magnitude).getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(earthquake.getMagnitude());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        return listItemView;
    }

    // Make a string to hold the primary location for this earthquake from the whole location string
    private String getPrimaryLocation(String location){

        String primaryLocation = "";
        if(location.contains("of")) {
            int index = location.indexOf("of ");
            primaryLocation = location.substring(index + 3);
        }
        else {
            primaryLocation = location;
        }

        return primaryLocation;
    }

    // Make a string to hold the offset from the location for this earthquake from the whole location string
    private String getOffsetLocation(String location){

        String offsetLocation = "";

        if(location.contains("of")) {
            int index = location.indexOf("of ");
            offsetLocation = location.substring(0, index + 2);
        }
        else {
            offsetLocation = "Near the ";
        }

        return offsetLocation;
    }

    private int getMagnitudeColor(double magnitude){

        int magnitudeColorResourceId;
        int mag = (int) Math.floor(magnitude);

        switch (mag){
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
        }

        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }
}
