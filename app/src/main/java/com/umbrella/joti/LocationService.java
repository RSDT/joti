package com.umbrella.joti;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.umbrella.jotiwa.JotiApp;
import com.umbrella.jotiwa.communication.LinkBuilder;
import com.umbrella.jotiwa.communication.enumeration.area348.Area348_API;
import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.interaction.AsyncInteractionTask;
import com.umbrella.jotiwa.communication.interaction.InteractionRequest;
import com.umbrella.jotiwa.data.objects.area348.sendables.HunterInfoSendable;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;


public class LocationService extends Service implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private long last_location_send = 0l;
    private boolean pref_send_loc_old = false;
    private boolean recieving_locations = false;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;


    @Override
    public void onCreate() {
        JotiApp.debug("location service aangemaakt");
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext());
        int interval= Integer.parseInt(preferences.getString("pref_send_loc_interval", "1"));
        if (interval <1){
            interval = 1;
        }
        if (interval > 5){
            interval = 5;
        }
        createLocationRequest(interval * 60 * 1000, 60000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    public void createLocationRequest(int interval, int fastest) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(fastest);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }
    protected void startLocationUpdates(){
        startLocationUpdates(this.mLocationRequest);
    }
    protected void startLocationUpdates(LocationRequest mLocationRequest) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    protected void TryToSendLocation(Location location) {
        JotiApp.debug("trying to send loc");
        long time = System.currentTimeMillis();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (time - last_location_send > 60 * 1000) {
            boolean send_location = preferences.getBoolean("pref_send_loc", false);
            if (send_location) {
                String default_username = this.getString(R.string.standard_username);
                String username = preferences.getString("pref_username", default_username);
                if (username == default_username || username == "") {
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;
                    String text = getString(R.string.username_not_set);
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    last_location_send = time;
                    sendlocation(location, username);
                }
            } else {
                JotiApp.debug("locatie niet verzonden");
            }
        } else {
            JotiApp.debug("nog geen tijd");
        }
    }

    public void sendlocation(Location location, String username) {

        try {
            /**
             * 1) Create a sendable form of HunterInfo with the HunterInfoSendable.get() function.
             * 2) Serialize the sendable.
             * 3) Make sure the root of the LinkBuilder is set to the Area348's one.
             * 3) Make a interaction request with the url "http://jotihunt-api.area348.nl/hunter/" with the help of LinkBuilder
             * and set the data to the serialized sendable and finally set needs handling to false.
             * 4) Create a new interaction task and execute it, with the created InteractionRequest.
             * Tested, it works*/
            HunterInfoSendable sendable = HunterInfoSendable.get();
            String datas = new Gson().toJson(sendable);
            LinkBuilder.setRoot(Area348_API.root);
            InteractionRequest hunterPost = new InteractionRequest(LinkBuilder.build(new String[]{MapPart.Hunters.getValue()}), datas, false);
            new AsyncInteractionTask().execute(hunterPost);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("Locatie niet verzonden");
            Log.e("Debug", e.toString());
        }

        /**
         * Use mattijn's new fucntion.
         * */
        JotiApp.toast("Je locatie is verzonden");
    }

    @Override
    public void onLocationChanged(Location location) {
        JotiApp.debug("locationchanged");
        JotiApp.debug(location.toString());
        JotiApp.setLastLocation(location);
        TryToSendLocation(location);
    }

    @Override
    public void onConnected(Bundle bundle) {
        JotiApp.debug("connected");
        PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext()).registerOnSharedPreferenceChangeListener(this);
        JotiApp.debug("isconnected=" + mGoogleApiClient.isConnected());
        JotiApp.debug("isconnecting=" + mGoogleApiClient.isConnecting());
        if (LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient).isLocationAvailable()) {
            JotiApp.debug(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).toString());
        } else
            JotiApp.debug(LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient).toString());
        startLocationUpdates(mLocationRequest);

    }


    @Override
    public void onConnectionSuspended(int i) {
        JotiApp.debug("suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        JotiApp.debug("conectionfailed");
    }

    protected synchronized void buildGoogleApiClient() {
        JotiApp.debug("building google iets");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        JotiApp.debug("isconnecting=" + mGoogleApiClient.isConnecting());
        JotiApp.debug("isconnected=" + mGoogleApiClient.isConnected());
        JotiApp.debug(mGoogleApiClient.toString());
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        this.recieving_locations = false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pref_send_loc")) {
            JotiApp.debug("setting veranderd");
            if (sharedPreferences.getBoolean("pref_send_loc", false)) {
                startLocationUpdates();
                JotiApp.debug("setting veranderd naar uit");
            } else if (!sharedPreferences.getBoolean("pref_send_loc", false) ) {
                stopLocationUpdates();
                JotiApp.debug("setting veranderd naar uit");
            }
        }else if(key.equals("pref_send_loc_interval")){
            JotiApp.toast("location update interval is nog neit getest.");
            int interval= Integer.parseInt(sharedPreferences.getString("pref_send_loc_interval", "1"));
            if (interval <1){
                interval = 1;
            }
            if (interval > 5){
                interval = 5;
            }
            createLocationRequest(interval * 60 * 1000, 60000);
        }
    }
}
