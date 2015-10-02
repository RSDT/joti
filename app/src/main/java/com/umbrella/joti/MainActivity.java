package com.umbrella.joti;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;
import com.umbrella.jotiwa.data.objects.area348.BaseInfo;
import com.umbrella.jotiwa.data.objects.area348.FotoOpdrachtInfo;
import com.umbrella.jotiwa.data.objects.area348.HunterInfo;
import com.umbrella.jotiwa.data.objects.area348.ScoutingGroepInfo;
import com.umbrella.jotiwa.data.objects.area348.VosInfo;
import com.umbrella.jotiwa.map.area348.MapManager;
import com.umbrella.jotiwa.map.area348.MapPartState;
import com.umbrella.jotiwa.map.area348.storage.MapStorage;

import org.w3c.dom.Text;

import java.util.ArrayList;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    PageAdaptor pageAdaptor;

    ViewPager pager;

    MapManager mapManager;
    private boolean useActionbar = true;
    private boolean useSafedInstance = false; // TODO zie bijbehoorende commit 'locationhandler 3/3'
    private GoogleApiClient mGoogleApiClient = null;
    private boolean isConnected;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_refresh:
                mapManager.update();
                // TODO hier een refresh toevoegen
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                String text = "Deze knop werknt nog niet. Maar voor nu werkt het scherm draaien ook.";
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();
        if (useActionbar) {
            setContentView(R.layout.mapsonly);
        } else{
            setContentView(R.layout.activity_main);
        }

        Intent StartServiceIntent = new Intent(this, LocationHandler.class);
        startService(StartServiceIntent);

        Intent intent = getIntent();
        Uri data = intent.getData();


        if (savedInstanceState != null && useSafedInstance) //TODO zie bijbehorende commit 'locationhandler 3/3'
        {
            mapManager = new MapManager((MapStorage) savedInstanceState.getParcelable("mapStorage"), (ArrayList<MapPartState>) savedInstanceState.getSerializable("states"));
        } else {
            mapManager = new MapManager();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Intent");
        if (data != null) {
            String gebied = data.getQueryParameter("gebied").toLowerCase();
            mapManager.add(new MapPartState(MapPart.Vossen, TeamPart.parse(gebied), true, true));
            builder.setMessage("Updating " + TeamPart.parse(gebied));
            builder.create().show();
        }
        if (!useActionbar) {
            pageAdaptor = new PageAdaptor(getSupportFragmentManager());
            pager = (ViewPager) findViewById(R.id.pager);
            pager.setAdapter(pageAdaptor);
        }
        MapFragment.setOnMapReadyCallback(this);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (!useSafedInstance) { // TODO zie bijbehoorende commit 'locationhandler 3/3'
            super.onSaveInstanceState(outState);
            return;
        }
        outState.putParcelable("mapStorage", mapManager.getMapStorage());
        outState.putSerializable("states", mapManager.getMapPartStates());
        super.onSaveInstanceState(outState);
    }

    public void onMapReady(GoogleMap map) {
        map.setInfoWindowAdapter(this);
        mapManager.setGoogleMap(map);
        if (!mapManager.isMigrated()) {
            mapManager.add(new MapPartState(MapPart.All, TeamPart.All, true, true));
            mapManager.update();
        }


        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        CameraUpdate camera = null;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        float zoom= Float.valueOf(preferences.getString("pref_zoom", "10"));
        if (lastLocation == null) {
            camera = CameraUpdateFactory.newLatLngZoom(new LatLng(52.021675, 6.059437), zoom);
        }
        else{
            camera = CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 10);
        }
        map.moveCamera(camera);
    }

    public View getInfoContents(Marker marker) {
        return null;
    }

    public View getInfoWindow(Marker marker) {
        /**
         * Inflate the info window.
         * */
        View view = getLayoutInflater().inflate(R.layout.info_window, null);

        /**
         * Get the textviews.
         * */
        TextView infoType = (TextView) view.findViewById(R.id.infoWindow_infoType);
        TextView naam = (TextView) view.findViewById(R.id.infoWindow_naam);
        TextView dateTime_adres = (TextView) view.findViewById(R.id.infoWindow_dateTime_adres);
        TextView coordinaat = (TextView) view.findViewById(R.id.infoWindow_coordinaat);

        /**
         * Get the type indicators of the marker and parse them.
         * */
        String[] splitted = marker.getTitle().split(";");
        MapPart part = MapPart.parse(splitted[0]);

        if (part == MapPart.Vossen) {
            TeamPart teamPart = TeamPart.parse(splitted[1]);
            infoType.setBackgroundColor(TeamPart.getAssociatedColor(teamPart));
            VosInfo info = mapManager.getMapStorage().findInfo(new MapPartState(part, teamPart), Integer.parseInt(splitted[2]));
            infoType.setText("Vos");
            naam.setText(info.team_naam);
            dateTime_adres.setText(info.datetime);
            coordinaat.setText(((Double) info.latitude + " , " + ((Double) info.longitude).toString()));
        } else {
            if (part == MapPart.Hunters) {
                HunterInfo hunterInfo = mapManager.getMapStorage().findHunterInfo(splitted[1], Integer.parseInt(splitted[2]));
                infoType.setText("Hunter");
                naam.setText(hunterInfo.gebruiker);
                dateTime_adres.setText(hunterInfo.datetime);
            } else {
                BaseInfo baseInfo = mapManager.getMapStorage().findInfo(new MapPartState(part, TeamPart.None), Integer.parseInt(splitted[1]));
                coordinaat.setText(((Double) baseInfo.latitude + " , " + ((Double) baseInfo.longitude).toString()));
                switch (part) {
                    case ScoutingGroepen:
                        infoType.setText("ScoutingGroep");
                        ScoutingGroepInfo scoutingGroepInfo = (ScoutingGroepInfo) baseInfo;
                        naam.setText(scoutingGroepInfo.naam);
                        dateTime_adres.setText(scoutingGroepInfo.adres);
                        break;
                    case FotoOpdrachten:
                        infoType.setText("FotoOpdracht");
                        FotoOpdrachtInfo fotoOpdrachtInfo = (FotoOpdrachtInfo) baseInfo;
                        naam.setText(fotoOpdrachtInfo.naam);
                        dateTime_adres.setText(fotoOpdrachtInfo.info);
                        break;
                }
            }
        }
        return view;
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        isConnected = true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        isConnected = true;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
