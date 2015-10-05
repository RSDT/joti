package com.umbrella.joti;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.umbrella.jotiwa.JotiApp;
import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;
import com.umbrella.jotiwa.data.objects.area348.receivables.BaseInfo;
import com.umbrella.jotiwa.data.objects.area348.receivables.FotoOpdrachtInfo;
import com.umbrella.jotiwa.data.objects.area348.receivables.HunterInfo;
import com.umbrella.jotiwa.data.objects.area348.receivables.ScoutingGroepInfo;
import com.umbrella.jotiwa.data.objects.area348.receivables.VosInfo;
import com.umbrella.jotiwa.map.area348.MapManager;
import com.umbrella.jotiwa.map.area348.MapPartState;
import com.umbrella.jotiwa.map.area348.binding.MapBindObject;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter, SharedPreferences.OnSharedPreferenceChangeListener {

    PageAdaptor pageAdaptor;

    ViewPager pager;

    MapManager mapManager;

    Date old;

    ArrayList<MapPartState> oldStates = new ArrayList<>();

    private boolean useActionbar = true;
    private boolean useSafedInstance = false; // TODO zie bijbehoorende commit 'locationhandler 3/3'

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
                return true;
            case R.id.action__map_camera:
                mapManager.CameraToCurrentLocation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            this.oldStates = (ArrayList<MapPartState>) savedInstanceState.getSerializable("mapManager");
        }

        setContentView(R.layout.activity_main);

        old = new Date();


        PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext()).registerOnSharedPreferenceChangeListener(this);

        if (useActionbar) {
            setContentView(R.layout.mapsonly);
        } else {
            setContentView(R.layout.activity_main);
        }
        Intent StartServiceIntent = new Intent(this, LocationService.class);
        startService(StartServiceIntent);

        Intent intent = getIntent();
        Uri data = intent.getData();

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

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("mapManager", (ArrayList<MapPartState>) mapManager);
    }


    public void onMapReady(GoogleMap map) {
        map.setInfoWindowAdapter(this);

        mapManager = new MapManager(map);

        /**
         * Checks if there were old states, if so add them and sync the storage with the map.
         * */
        if (oldStates.size() > 0) {
            mapManager.addAll(oldStates);
            mapManager.sync();
        }
        /**
         * If there are no old states, then this is the first run or no states were added.
         * Check the preferences to make sure.
         * */
        else {
            /**
             * TODO: Only update the parts that are enabled in the settings.
             * */
/*            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String format = "pref_";

            String vosFormat = format + "vos";
            TeamPart[] teams = new TeamPart[] { TeamPart.Alpha, TeamPart.Bravo, TeamPart.Charlie,
                    TeamPart.Delta, TeamPart.Echo, TeamPart.Foxtrot, TeamPart.XRay };
            for(int i = 0; i < teams.length-1; i++)
            {
                if(sharedPreferences.getBoolean(vosFormat + teams[i].getSubChar(), false));
                {
                    mapManager.add(new MapPartState(MapPart.Vossen, teams[i], true , true));
                }
            }*/

            mapManager.add(new MapPartState(MapPart.All, TeamPart.All, true, true));
            mapManager.update();
        }
        mapManager.CameraToCurrentLocation();
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        String[] typeCode = key.split("_");
        if (Objects.equals(typeCode[1], "vos")) {
            MapPart mapPart = MapPart.parse(typeCode[1]);

            switch (mapPart) {
                case Vossen:
                    TeamPart teamPart = TeamPart.parse(typeCode[2]);
                    MapPartState stateVos = mapManager.findState(MapPart.Vossen, teamPart, MapPartState.getAccesor(MapPart.Vossen, teamPart));
                    MapBindObject bindObjectVos = mapManager.getMapBinder().getAssociatedMapBindObject(stateVos);
                    bindObjectVos.setVisiblty(preferences.getBoolean(key, false));
                    break;
                case Hunters:

                    /**
                     * TODO: Add code for hunter visibilty.
                     * */
                    for (int i = 0; i < mapManager.size(); i++) {
                        MapPartState current = mapManager.get(i);
                        if (current.getMapPart() == MapPart.Hunters && !current.getAccessor().matches("hunter")) {
                            MapBindObject mapBindObjectHunter = mapManager.getMapBinder().getAssociatedMapBindObject(current);
                            mapBindObjectHunter.setVisiblty(preferences.getBoolean(key, false));
                        }
                    }
                    break;
                default:
                    MapPartState stateGen = mapManager.findState(mapPart, TeamPart.None, MapPartState.getAccesor(mapPart, TeamPart.None));
                    MapBindObject bindObjectGen = mapManager.getMapBinder().getAssociatedMapBindObject(stateGen);
                    bindObjectGen.setVisiblty(preferences.getBoolean(key, false));
                    break;
            }
        }
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


        switch (part) {
            case Vossen:
                TeamPart teamPart = TeamPart.parse(splitted[1]);
                infoType.setBackgroundColor(TeamPart.getAssociatedColor(teamPart));
                MapPartState stateVos = mapManager.findState(part, teamPart, MapPartState.getAccesor(part, teamPart));
                MapBindObject bindObject = mapManager.getMapBinder().getAssociatedMapBindObject(stateVos);


                VosInfo info = (VosInfo) mapManager.getMapStorage().findInfo(stateVos, Integer.parseInt(splitted[2]));
                String dateString = info.datetime;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext());
                float speed= Float.parseFloat(preferences.getString("pref_speed", "6.0"));
                float aantal_meters_per_uur = speed * 1000;
                try {
                    Date date = dateFormat.parse(dateString);
                    SharedPreferences sharedpeferences = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext());
                    boolean debug_on = sharedpeferences.getBoolean("pref_debug", false);
                    if (debug_on) {
                        date.setMonth(new Date().getMonth());
                        date.setDate(new Date().getDate());
                    }
                    long duration = (new Date()).getTime() - date.getTime();

                    long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);

                    float diffInHours = ((float) diffInSeconds / 60f) / 60f;

                    float radius = diffInHours * aantal_meters_per_uur;

                    old = date;
                    boolean isLastMarker = Integer.parseInt(splitted[2]) >= bindObject.getMarkers().size();
                    if (isLastMarker) {
                        bindObject.getCircles().get(0).setRadius(radius);
                    }


                } catch (ParseException e) {
                    Date date = new Date();
                    long duration = date.getTime() - old.getTime();

                    long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);

                    float diffInHours = ((float) diffInSeconds / 60f) / 60f;

                    float increaseM = diffInHours * aantal_meters_per_uur;
                    old = date;
                    double radius = bindObject.getCircles().get(0).getRadius();
                    boolean isLastMarker = Integer.parseInt(splitted[2]) == bindObject.getMarkers().size();
                    if (isLastMarker) {
                        bindObject.getCircles().get(0).setRadius(increaseM + radius);
                    }
                }
                infoType.setText("Vos");
                naam.setText(info.team_naam);
                dateTime_adres.setText(info.datetime);
                coordinaat.setText(((Double) info.latitude + " , " + ((Double) info.longitude).toString()));
                break;
            case Hunters:
                MapPartState state = mapManager.findState(part, TeamPart.None, splitted[1]);
                HunterInfo hunterInfo = (HunterInfo) mapManager.getMapStorage().findInfo(state, Integer.parseInt(splitted[2]));
                infoType.setText("Hunter");
                try {
                    naam.setText(hunterInfo.gebruiker);
                    dateTime_adres.setText(hunterInfo.datetime);
                    coordinaat.setText(((Double) hunterInfo.latitude + " , " + ((Double) hunterInfo.longitude).toString()));
                } catch (Exception e) {
                    naam.setText("Error");
                    dateTime_adres.setText(e.toString());
                    coordinaat.setText("Error");
                }

                break;
            default:
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
                        naam.setText(fotoOpdrachtInfo.extra);
                        dateTime_adres.setText(fotoOpdrachtInfo.info);
                        break;
                }
                break;
        }
        return view;
    }


}
