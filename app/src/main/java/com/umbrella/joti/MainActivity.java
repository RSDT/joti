package com.umbrella.joti;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;
import com.umbrella.jotiwa.map.area348.MapManager;
import com.umbrella.jotiwa.map.area348.MapPartState;
import com.umbrella.jotiwa.map.area348.storage.MapStorage;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    PageAdaptor pageAdaptor;

    ViewPager pager;

    MapManager mapManager;

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
                // TODO hier een refresh toevoegen
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsonly);


        if(savedInstanceState != null && false)  // TODO ik heb dit eruit gehaald omdat het bugte bij mij
        {
            mapManager = new MapManager((MapStorage)savedInstanceState.getParcelable("mapStorage"),
                    (ArrayList<MapPartState>)savedInstanceState.getSerializable("states"));
        }
        else
        {
            mapManager = new MapManager();
        }

        //pageAdaptor = new PageAdaptor(getSupportFragmentManager());

        //pager = (ViewPager) findViewById(R.id.pager);
        //pager.setAdapter(pageAdaptor);

        MapFragment.setOnMapReadyCallback(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
       if (true){ // TODO ik heb dit eruit gehaald omdat het bugte bij mij
           return;
       }
        outState.putParcelable("mapStorage", mapManager.getMapStorage());
        outState.putSerializable("states", mapManager.getMapPartStates());
        super.onSaveInstanceState(outState);
    }

    public void onMapReady(GoogleMap map)
    {
        mapManager.setGoogleMap(map);
        if(!mapManager.isMigrated())
        {
            mapManager.add(new MapPartState(MapPart.Vossen, TeamPart.All, true, true));
            mapManager.update();
        }


        CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(new LatLng(52.006074, 6.028140), 10);
        map.moveCamera(camera);
    }





}
