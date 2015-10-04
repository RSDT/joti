package com.umbrella.joti;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {

    PageAdaptor pageAdaptor;

    ViewPager pager;

    MapManager mapManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Uri data = intent.getData();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Intent");
        if(data != null)
        {
            String gebied = data.getQueryParameter("gebied").toLowerCase();
            mapManager.add(new MapPartState(MapPart.Vossen, TeamPart.parse(gebied), true, true));
            builder.setMessage("Updating " + TeamPart.parse(gebied));
            builder.create().show();
        }

        pageAdaptor = new PageAdaptor(getSupportFragmentManager());

        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(pageAdaptor);

        MapFragment.setOnMapReadyCallback(this);
    }

    public void onMapReady(GoogleMap map)
    {
        map.setInfoWindowAdapter(this);

        mapManager = new MapManager(map);
        mapManager.add(new MapPartState(MapPart.All, TeamPart.All, true, true));
        mapManager.update();

        CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(new LatLng(52.021675, 6.059437), 10);
        map.moveCamera(camera);
    }

    public View getInfoContents(Marker marker)
    {
        return null;
    }

    public View getInfoWindow(Marker marker)
    {
        /**
         * Inflate the info window.
         * */
        View view = getLayoutInflater().inflate(R.layout.info_window, null);

        /**
         * Get the textviews.
         * */
        TextView infoType = (TextView)view.findViewById(R.id.infoWindow_infoType);
        TextView naam = (TextView)view.findViewById(R.id.infoWindow_naam);
        TextView dateTime_adres = (TextView)view.findViewById(R.id.infoWindow_dateTime_adres);
        TextView coordinaat = (TextView)view.findViewById(R.id.infoWindow_coordinaat);

        /**
         * Get the type indicators of the marker and parse them.
         * */
        String[] splitted = marker.getTitle().split(";");
        MapPart part = MapPart.parse(splitted[0]);

        switch(part)
        {
            case Vossen:
                TeamPart teamPart = TeamPart.parse(splitted[1]);
                infoType.setBackgroundColor(TeamPart.getAssociatedColor(teamPart));
                VosInfo info = (VosInfo)mapManager.getMapStorage().findInfo(new MapPartState(part, teamPart), Integer.parseInt(splitted[2]));
                infoType.setText("Vos");
                naam.setText(info.team_naam);
                dateTime_adres.setText(info.datetime);
                coordinaat.setText(((Double)info.latitude + " , "+ ((Double)info.longitude).toString()));
                break;
            case Hunters:
                MapPartState state = mapManager.findState(part, TeamPart.None, splitted[1]);
                HunterInfo hunterInfo = (HunterInfo)mapManager.getMapStorage().findInfo(state, Integer.parseInt(splitted[2]));
                infoType.setText("Hunter");
                naam.setText(hunterInfo.gebruiker);
                dateTime_adres.setText(hunterInfo.datetime);
                coordinaat.setText(((Double)hunterInfo.latitude + " , "+ ((Double)hunterInfo.longitude).toString()));
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
                        naam.setText(fotoOpdrachtInfo.naam);
                        dateTime_adres.setText(fotoOpdrachtInfo.info);
                        break;
                }
                break;
        }
        return view;
    }





}
