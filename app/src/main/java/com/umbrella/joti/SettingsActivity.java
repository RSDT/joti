package com.umbrella.joti;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;


/**
 * Created by Mattijn on 30-9-2015.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        return true;
    }
}
