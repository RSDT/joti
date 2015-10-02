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

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;


public class LocationHandler extends Service implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private long last_location_send = 0l;
    private boolean pref_send_loc_old = false;
    private boolean recieving_locations = false;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    private void debug(CharSequence text) {
        boolean debug_on = false;
        if (debug_on) {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    @Override
    public void onCreate() {
        debug("location service aangemaakt");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        createLocationRequest(120000, 60000);
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
        this.recieving_locations = true;
    }

    protected void startLocationUpdates(LocationRequest mLocationRequest) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    protected void TryToSendLocation(Location location) {
        debug("trying to send loc");
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
                debug("locatie niet verzonden");
            }
        } else {
            debug("nog geen tijd");
        }
    }

    public String reformatString(String username) {
        username = username.replace("\\", "");
        username = username.replace("\"", "");
        username = username.replace("\n", "");
        username = username.replace("/", "");
        username = username.replace("\t", "");
        username = username.replace(" ", "");
        username = username.replace("-", "");
        username = username.replace("*", "");
        username = username.replace("'", "");
        username = username.replace("%", "");
        if (username.isEmpty()) {
            username = "IKMOETMIJNGEBRUIKERSNAAMVERANDEREN";
        }
        return username.toLowerCase();
    }

    public void sendlocation(Location location, String username) {
        /*
        ik wou deze functie kunnen testen dus ik heb maar op internet een functie opgezocht die de een post request kan versturn.
        ik had deze geïmplemnteerd en... het werkte niet
        ik heb die string data lopen veranderen en overal " neergezet. werkte t nog steeds niet.
        toen dacht ik fack it
        ik heb gegoogled op apk decompiler.
        heb op upload geklikt.
        en heb gekeken naar hoe tom t toen verzond.
        daar kwam ik toevallig een GET URL tegen.
        en dus verzend ie m zo.
        maar ik neem aan dat dit toch weer een post moet worden dus het is tijdelijk maar het werkt.
         */
        Context context = getApplicationContext();
        CharSequence text = "Je locatie is verzonden";
        int duration = Toast.LENGTH_SHORT;
        final String username2 = reformatString(username);
        final double lon = location.getLongitude();
        final double lat = location.getLatitude();

        final String data = "{gebruiker: " + username2 +
                ",latitude: " + lat +
                ",longitude: " + lon + "}";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://jotihunt.area348.nl/android/hunters_invoer.php?coords=" + lat + "," + lon + "&naam=" + username2);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    System.out.println(data);
                    //Set to POST
                    connection.setDoOutput(true);
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(10000);
                    Writer writer = new OutputStreamWriter(connection.getOutputStream());
                    writer.write(data);
                    writer.flush();
                    writer.close();
                    System.out.println(connection.getResponseMessage());
                    System.out.println("location send");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    System.out.println("location not send");
                    Log.e("Debug", e.toString());
                }
            }
        }).start();
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        debug("locationchanged");
        debug(location.toString());
        TryToSendLocation(location);
    }

    @Override
    public void onConnected(Bundle bundle) {
        debug("connected");
        debug("isconnected=" + mGoogleApiClient.isConnected());
        debug("isconnecting=" + mGoogleApiClient.isConnecting());
        if (LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient).isLocationAvailable()) {
            debug(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).toString());
        } else
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
    }
}
