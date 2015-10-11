package com.umbrella.joti;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.umbrella.jotiwa.JotiApp;

/**
 * Created by mattijn on 9-10-15.
 */
public class FastLocationUpdater implements SharedPreferences.OnSharedPreferenceChangeListener,com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private boolean shouldStartLocationUpdates = false;
    private boolean shouldStopLocationUpdates= false;

    FastLocationUpdater(){
        buildGoogleApiClient();
    }
    private static final String LOCATION_FOLLOW_KEY = "pref_follow";
    private GoogleMap gmap;
    private GoogleApiClient mGoogleApiClient;
    private boolean locationUpdates = false;
    public LocationRequest mLocationRequest;

    @Override
    public void onLocationChanged(Location location) {
        JotiApp.setLastLocation(location);
    }
    protected synchronized void buildGoogleApiClient() {
        JotiApp.debug("building google iets");
        mGoogleApiClient = new GoogleApiClient.Builder(JotiApp.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    @Override
    public void onConnected(Bundle bundle) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext());
        createLocationRequest();
        if (preferences.getBoolean(LOCATION_FOLLOW_KEY, false) || shouldStartLocationUpdates) {
            startLocationUpdates(mLocationRequest);
        }
        if (shouldStopLocationUpdates){
            stopLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);}

    protected void startLocationUpdates(LocationRequest mLocationRequest) {
        if (mGoogleApiClient.isConnected()){
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        locationUpdates= true;
        shouldStartLocationUpdates = false;
        }
        else{
            shouldStartLocationUpdates = true;
        }
    }
    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            locationUpdates = false;
            shouldStopLocationUpdates = false;
        }else{
            shouldStopLocationUpdates = true;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (key.equals(LOCATION_FOLLOW_KEY)){
            if (preferences.getBoolean("pref_send_loc_interval", true)){
                if (!locationUpdates){
                    startLocationUpdates(mLocationRequest);
                }
            }else{
                if (locationUpdates){
                    stopLocationUpdates();
                }
            }
        }
    }
}
