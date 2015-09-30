package com.umbrella.joti;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Objects;

public class LocationHandler extends Service implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private long last_location_send = 0l;
    private boolean pref_send_loc_old = false;
    private boolean recieving_locations = false;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    private void debug(CharSequence text){
        boolean debug_on = true;
        if (debug_on){
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }
    @Override
    public void onCreate(){
        debug("location service aangemaakt");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        createLocationRequest(120000, 60000);

    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void createLocationRequest(int interval, int fastest) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(fastest);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        this.recieving_locations = true;
    }
    protected void startLocationUpdates(LocationRequest mLocationRequest) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void TryToSendLocation(Location location){
        debug("trying to send loc");
        long time = System.currentTimeMillis();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (time - last_location_send > 60 * 1000){
            boolean send_location = preferences.getBoolean("pref_send_loc", false);
            if (send_location){
                String default_username = this.getString(R.string.standard_username);
                String username = preferences.getString("pref_username", default_username);
                if (Objects.equals(username, default_username) || Objects.equals(username, "")){
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;
                    String text = getString(R.string.username_not_set);
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }else{
                    last_location_send = time;
                    sendlocation(location, username);
                }
            }else{
                debug("locatie niet verzonden");
            }
        }else{
            debug("nog geen tijd");
        }
    }
    public void sendlocation(Location location, String username){
        Context context = getApplicationContext();
        CharSequence text = "Je locatie is verzonden";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


    @Override
    public void onLocationChanged(Location location) {
        debug("locationchanged");
        debug(location.toString());
        TryToSendLocation(location);
    }
/*
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        debug("status changed");
    }

    @Override
    public void onProviderEnabled(String provider) {
        debug("provider enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        debug("provider disabled");
    }
*/
    @Override
    public void onConnected(Bundle bundle) {
        debug("connected");
        debug("isconnected=" + mGoogleApiClient.isConnected());
        debug("isconnecting=" + mGoogleApiClient.isConnecting());
        if (LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient).isLocationAvailable()) {
                debug(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).toString());
            }
            else
               debug(LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient).toString());
        startLocationUpdates(mLocationRequest);
    }


    @Override
    public void onConnectionSuspended(int i) {
        debug("suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        debug("conectionfailed");
    }
    protected synchronized void buildGoogleApiClient() {
        debug("building google iets");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        mGoogleApiClient.connect();
        debug("isconnecting=" + mGoogleApiClient.isConnecting());
        debug("isconnected=" + mGoogleApiClient.isConnected());
        debug(mGoogleApiClient.toString());
        //while (mGoogleApiClient.isConnecting());
        //if (LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient).isLocationAvailable()) {
        //    debug(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).toString());
        //}
        //else
         //   debug(LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient).toString());
    }
}
