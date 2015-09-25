package com.umbrella.joti;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;
import com.umbrella.jotiwa.map.area348.MapManager;
import com.umbrella.jotiwa.map.area348.MapPartState;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    PageAdaptor pageAdaptor;

    ViewPager pager;

    MapManager mapManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pageAdaptor = new PageAdaptor(getSupportFragmentManager());

        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(pageAdaptor);

        pageAdaptor.mapFragment.setOnMapReadyCallback(this);
    }

    public void onMapReady(GoogleMap map)
    {
        mapManager = new MapManager(map);
        mapManager.add(new MapPartState(MapPart.Vossen, TeamPart.All, true, true));
        mapManager.update();

        CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(new LatLng(52.021675, 6.059437), 10);
        map.moveCamera(camera);
    }





}
