package com.umbrella.joti;

import android.os.Bundle;
import android.preference.PreferenceActivity;


/**
 * Created by Mattijn on 30-9-2015.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}