package com.umbrella.joti;


import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;
import com.umbrella.jotiwa.JotiApp;
import com.umbrella.jotiwa.RealTimeTracker;
import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;
import com.umbrella.jotiwa.data.objects.area348.receivables.BaseInfo;
import com.umbrella.jotiwa.data.objects.area348.receivables.FotoOpdrachtInfo;
import com.umbrella.jotiwa.data.objects.area348.receivables.HunterInfo;
import com.umbrella.jotiwa.data.objects.area348.receivables.ScoutingGroepInfo;
import com.umbrella.jotiwa.data.objects.area348.receivables.VosInfo;
import com.umbrella.jotiwa.data.objects.area348.sendables.HunterInfoSendable;
import com.umbrella.jotiwa.map.area348.MapManager;
import com.umbrella.jotiwa.map.area348.MapPartState;
import com.umbrella.jotiwa.map.area348.binding.MapBindObject;
import com.umbrella.jotiwa.map.area348.storage.StorageObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter, SharedPreferences.OnSharedPreferenceChangeListener {

    private PageAdaptor pageAdaptor;

    private ViewPager pager;
    private HashMap<TeamPart,Pair<Date,Circle>> cirlces = new HashMap<>();
    private RealTimeTracker listener = new RealTimeTracker() {
        @Override
        public void onNewLocation(Location location) {
            if(MapManager.getMapManagerHandler() != null)
            {
                Message message = new Message();
                message.obj = location;
                message.what = MapManager.ManagerMessageType.MANAGER_MESSAGE_TYPE_SEND_LOC;
                MapManager.getMapManagerHandler().sendMessage(message);
            }
        }
    };

    private Runnable updateTask = new Runnable() {
        @Override
        public void run() {

            refresh();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext());
            int updateTime = Integer.parseInt(preferences.getString("pref_update", "1"));
            if (updateTime < 1){
                updateTime = 1;
            }
            updateHandler.postDelayed(updateTask, updateTime* 60 * 1000); //loop
        }
    };
    private Runnable circleTask = new Runnable(){
        @Override
        public void run() {

            for (TeamPart key : cirlces.keySet()){
                MapPartState stateVos = mapManager.findState(MapPart.Vossen, key, MapPartState.getAccesor(MapPart.Vossen, key));
                Date date =cirlces.get(key).first;
                SharedPreferences sharedpeferences = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext());
                boolean debug_on = sharedpeferences.getBoolean("pref_debug", false);
                float speed = Float.parseFloat(sharedpeferences.getString("pref_speed", "6.0"));
                float aantal_meters_per_uur = speed * 1000f;
                if (debug_on) {
                    date.setMonth(new Date().getMonth());
                    date.setDate(new Date().getDate());
                }
                long duration = (new Date()).getTime() - date.getTime();

                float diffInHours = TimeUnit.MILLISECONDS.toSeconds(duration)/60f/60f;

                if (diffInHours > 30)
                    diffInHours = 30;
                float radius = diffInHours * aantal_meters_per_uur;
                MapBindObject bindObject = mapManager.getMapBinder().getAssociatedMapBindObject(stateVos);
                bindObject.getCircles().get(0).setRadius(radius);
            }
        }
    };
    private MapManager mapManager;

    private ArrayList<MapPartState> oldStates = new ArrayList<>();
    private MapPartState TempMapState = null;

    private boolean useActionbar = true;
    private Handler updateHandler;
    private FastLocationUpdater fastLocationUpdater;
    private KmlLoader kmlLoader;

    /**
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_refresh:
                refresh();
                return true;
            case R.id.action__map_camera:
                mapManager.cameraToCurrentLocation();
                return true;
            case R.id.action_reset_circles:
                TeamPart[] parts = new TeamPart[]{
                        TeamPart.Alpha, TeamPart.Bravo, TeamPart.Charlie,
                        TeamPart.Charlie, TeamPart.Delta, TeamPart.Echo,
                        TeamPart.Foxtrot, TeamPart.XRay};
                for (int i = 0; i < parts.length; i++) {
                    MapPart part = MapPart.Vossen;
                    MapPartState stateVos = mapManager.findState(part, parts[i], MapPartState.getAccesor(part, parts[i]));
                    MapBindObject bindObject = mapManager.getMapBinder().getAssociatedMapBindObject(stateVos);
                    bindObject.getCircles().get(0).setRadius(0);
                    if (cirlces.containsKey(parts[i])){
                        cirlces.get(parts[i]).second.setRadius(0);
                    }

                }
                cirlces= new HashMap<>();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void refresh() {
        if (mapManager != null) {
            JotiApp.toast("updating Data");

            mapManager.update();
            mapManager.syncAll();
            updateHandler.postDelayed(circleTask,5000);// hee; slecht dit. hier wachten tot async stuff klaar is.
        }
    }

    @Override
    public void onResume() {
        fastLocationUpdater.startLocationUpdates(fastLocationUpdater.mLocationRequest); // foei mattijn
        updateHandler = new Handler();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext());
        int updateTime = Integer.parseInt(preferences.getString("pref_update", "1"));
        if (updateTime < 1){
            updateTime = 1;
        }
        updateHandler.postDelayed(updateTask, updateTime * 60 * 1000);// update everyminute
        super.onResume();
    }

    @Override
    public void onPause() {
        updateHandler.removeCallbacks(updateTask);
        fastLocationUpdater.stopLocationUpdates();
        super.onPause();
    }

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (useActionbar) {
            setContentView(R.layout.mapsonly);
        } else {
            setContentView(R.layout.activity_main);
        }
        if (savedInstanceState != null) {
            this.oldStates = (ArrayList<MapPartState>) savedInstanceState.getSerializable("mapManager");
        }

        //setContentView(R.layout.activity_main);


        JotiApp.addLocationListener(listener);

        PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext()).registerOnSharedPreferenceChangeListener(this);



        Intent StartServiceIntent = new Intent(this, LocationService.class);
        startService(StartServiceIntent);

        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            String gebied = data.getQueryParameter("gebied").toLowerCase();
            if (TempMapState == null) {
                JotiApp.toast("Updating " + TeamPart.parse(gebied));
                TempMapState = new MapPartState(MapPart.Vossen, TeamPart.parse(gebied), true, true);
            } else {
                JotiApp.toast("Er is niet geupdate door een error. Herstart de app.");
            }
            JotiApp.toast("Als je niks ziet moet je de app zelf openen.");
        }
        if (!useActionbar) {
            pageAdaptor = new PageAdaptor(getSupportFragmentManager());
            pager = (ViewPager) findViewById(R.id.pager);
            pager.setAdapter(pageAdaptor);
        }
        fastLocationUpdater = new FastLocationUpdater();
        MapFragment.setOnMapReadyCallback(this);
    }

    /**
     * @param savedInstanceState
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("mapManager", (ArrayList<MapPartState>) mapManager);
    }


    /**
     * @param map
     */
    public void onMapReady(GoogleMap map) {
        map.setInfoWindowAdapter(this);
        kmlLoader = new KmlLoader(map, R.raw.jotihunt2014);
        PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext()).registerOnSharedPreferenceChangeListener(kmlLoader);
        kmlLoader.ReadKML();
        mapManager = new MapManager(map);

        /**
         * Checks if there were old states, if so add them and sync the storage with the map.
         * */
        if (oldStates.size() > 0) {
            mapManager.addAll(oldStates);
            if (TempMapState != null) {
                mapManager.add(TempMapState);
                TempMapState = null;
            }
            mapManager.syncAll();
        }
        /**
         * If there are no old states, then this is the first run or no states were added.
         * Check the preferences to make sure.
         * */
        else {
            mapManager.add(new MapPartState(MapPart.All, TeamPart.All, true, true));
            mapManager.update();
        }
        mapManager.cameraToCurrentLocation();
    }

    /**
     * @param preferences
     * @param key
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        String[] temp = key.split("_");
        String[] typeCode = new String[3];
        for (int i = 0; i < temp.length && i < 3; i++) {
            typeCode[i] = temp[i];
        }
        MapPart mapPart = MapPart.parse(typeCode[1]);
        if (mapPart == null)
            return;
        switch (mapPart) {
            case Vossen:
                TeamPart teamPart = TeamPart.parse(typeCode[2]);
                MapPartState stateVos = mapManager.findState(MapPart.Vossen, teamPart, MapPartState.getAccesor(MapPart.Vossen, teamPart));
                MapBindObject bindObjectVos = mapManager.getMapBinder().getAssociatedMapBindObject(stateVos);
                bindObjectVos.setVisiblty(preferences.getBoolean(key, false));
                break;
            case ScoutingGroepen:
                TeamPart[] parts = new TeamPart[]{
                        TeamPart.Alpha, TeamPart.Bravo, TeamPart.Charlie,
                        TeamPart.Charlie, TeamPart.Delta, TeamPart.Echo,
                        TeamPart.Foxtrot, TeamPart.XRay};
                for (int i = 0; i < parts.length; i++) {
                    MapPartState stateGen = mapManager.findState(mapPart, parts[0], MapPartState.getAccesor(mapPart, parts[0]));
                    if (stateGen != null) {
                        MapBindObject bindObjectGen = mapManager.getMapBinder().getAssociatedMapBindObject(stateGen);
                        bindObjectGen.setVisiblty(preferences.getBoolean(key, false));
                    }
                }
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
                if (stateGen == null) return;
                MapBindObject bindObjectGen = mapManager.getMapBinder().getAssociatedMapBindObject(stateGen);
                bindObjectGen.setVisiblty(preferences.getBoolean(key, false));
                break;
        }
    }

    /**
     * @param marker
     * @return
     */
    public View getInfoContents(Marker marker) {
        return null;
    }

    /**
     * @param marker
     * @return
     */
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
                StorageObject storageObject = mapManager.getMapStorage().getAssociatedStorageObject(stateVos);

                VosInfo info = (VosInfo) mapManager.getMapStorage().findInfo(stateVos, Integer.parseInt(splitted[2]));
                String dateString = info.datetime;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext());
                float speed = Float.parseFloat(preferences.getString("pref_speed", "6.0"));
                float aantal_meters_per_uur = speed * 1000f;
                try {
                    Date date = dateFormat.parse(dateString);
                    SharedPreferences sharedpeferences = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext());
                    boolean debug_on = sharedpeferences.getBoolean("pref_debug", false);
                    if (debug_on) {
                        date.setMonth(new Date().getMonth());
                        date.setDate(new Date().getDate());
                    }
                    long duration = (new Date()).getTime() - date.getTime();

                    float diffInHours = TimeUnit.MILLISECONDS.toSeconds(duration)/60f/60f;

                    if (diffInHours > 30)
                        diffInHours = 30;
                    float radius = diffInHours * aantal_meters_per_uur;
                    MapBindObject bindObject = mapManager.getMapBinder().getAssociatedMapBindObject(stateVos);

                    if (mapManager.getMapStorage().isLastInfo(stateVos, info)) {
                        Circle circle = bindObject.getCircles().get(0);
                        circle.setRadius(radius);
                        cirlces.put(teamPart, new Pair<>(date, circle));
                        //((Circle)storageObject.getCircles().get(0)).setRadius(radius);
                        //mapManager.sync(stateVos);
                    }

                } catch (ParseException e) {
                    JotiApp.toast("Error" + e.toString());
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
            case Me:
                HunterInfoSendable me = HunterInfoSendable.get();
                infoType.setText("You");
                naam.setText(me.gebruiker);
                dateTime_adres.setText(new Date().toString());
                coordinaat.setText(me.latitude + " , " + me.longitude);
                break;
            default:
                BaseInfo baseInfo = mapManager.getMapStorage().findInfo(new MapPartState(part, TeamPart.None), Integer.parseInt(splitted[1]));
                coordinaat.setText(((Double) baseInfo.latitude + " , " + ((Double) baseInfo.longitude).toString()));
                switch (part) {
                    case ScoutingGroepen:
                        TeamPart teamPartsc = TeamPart.parse(splitted[2].toLowerCase());
                        infoType.setBackgroundColor(TeamPart.getAssociatedColor(teamPartsc));
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
