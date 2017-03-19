package com.example.android.quakereport;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class EarthquakePreferenceFragment extends PreferenceFragment
    implements Preference.OnPreferenceChangeListener{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            // Update the preference summary when the
            // settings activity is launched. Given the
            // key of a preference, we can use PreferenceFragment's
            // findPreference() method to get the Preference object,
            // and setup the preference using a helper method
            // called bindPreferenceSummaryToValue()
            Preference minMagnitude =
                    findPreference(getString(R.string.settings_min_magnitude_key));
            bindPreferenceSummaryToValue(minMagnitude);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

            Preference region = findPreference(getString(R.string.settings_region_key));
            bindPreferenceSummaryToValue(region);
        }

        // When the preference changes - update the summary
        // so that the app knows that it has been changed.
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            // If it is a ListPreference we have to find which of the options
            // has been chosen by finding its index and obtaining the array of the values,
            // then using the index to extract the option and set it up for the summary.
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        // Set the current EarhtquakePreferenceFragment
        // instance as the listener on each preference. We also read
        // the current value of the preference stored in the SharedPreferences
        // on the device, and display that in the preference summary
        // (so that the user can see the current value of the preference).
        private void bindPreferenceSummaryToValue(Preference preference){
            if(preference != null) {
                preference.setOnPreferenceChangeListener(this);
                SharedPreferences preferences =
                        PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                String preferenceString =
                        preferences.getString(preference.getKey(), "");
                onPreferenceChange(preference, preferenceString);
            }
        }
    }
}
