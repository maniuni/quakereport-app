/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.Loader;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity
implements LoaderManager.LoaderCallbacks<List<Earthquake>>{

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;

    /** Adapter for the list of earthquakes */
    private EarthquakeAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyView;

    /** URL for earthquake data from the USGS dataset
     * top 10 most recent earthquakes in the world with at least a magnitude of 6 */
    private static final String USGS_REQUEST_URL =
            "http://earthquake.usgs.gov/fdsnws/event/1/query";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Create a new {@link ArrayAdapter} of earthquakes that takes in an empty ArrayList
        mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Find a reference to the TextView for no earthquakes in the layout
        mEmptyView = (TextView) findViewById(R.id.empty_view);

        // Set the empty state TextView onto the ListView
        earthquakeListView.setEmptyView(mEmptyView);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        // Set a click listener so that we know which list item has been clicked on
        // in the list of earthquakes
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Get the clicked on earthquake
                Earthquake currentEarthquake = (Earthquake) mAdapter.getItem(i);

                // Make an intent so that the website with details for this earthquake opens
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentEarthquake.getUrl()));
                startActivity(intent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();


        // If there is network connection, fetch data
        if(activeNetwork != null && activeNetwork.isConnected()) {
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            getLoaderManager().initLoader(EARTHQUAKE_LOADER_ID, null, this).forceLoad();
        }
        else {
            // Otherwise display error
            // First, hide loading indicator so error message will be visible
            View loadingSpinner = findViewById(R.id.loading_spinner);
            loadingSpinner.setVisibility(View.GONE);
            // Update empty state with no connection message
            mEmptyView.setText(R.string.no_connection);
        }
    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {
        // Find a reference to the SharedPreferences object
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Check the user's preferred minimum magnitude or use the default value
        String minMagnitude = sharedPreferences.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));
        // Check the user's preferred sort order or use the default value
        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
        // Check the user's preferred region or use the default value
        String region = sharedPreferences.getString(
                getString(R.string.settings_region_key),
                getString(R.string.settings_region_default)
        );

        // Use the base url and then add the necessary
        // search queries using some of the preferences of the user
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "20");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);
        // See if the user chose Europe, otherwise show all -
        // by default it shows all so we don't need additional search queries.
        if(region.equals("europe")) {
            // Latitude and longitude of Europe
            uriBuilder.appendQueryParameter("minlatitude", "35");
            uriBuilder.appendQueryParameter("minlongitude", "-15");
            uriBuilder.appendQueryParameter("maxlongitude", "30");
        }
        // Create a new loader for the given URL
        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {
        // Find a reference to the loading spinner in the layout
        ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        // Set the spinner to not appear if there are results to show
        loadingSpinner.setVisibility(View.GONE);
        // Clear the adapter of previous earthquake data
        mAdapter.clear();
        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }
        //Set the empty state text to display "No earthquakes found."
        mEmptyView.setText(R.string.no_earthquakes);

    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        // Clear the adapter of previous earthquake data
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
