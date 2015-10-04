package com.umbrella.jotiwa.map.area348.handling;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by stesi on 3-10-2015.
 */
public class HunterObject {


    public HunterObject()
    {
        marker = new MarkerOptions();
        positions = new ArrayList<>();
    }

    private MarkerOptions marker;

    private ArrayList<LatLng> positions;


    public ArrayList<LatLng> getPositions() {
        return positions;
    }

    public MarkerOptions getMarker() {
        return marker;
    }

    public void setMarker(MarkerOptions marker) {
        this.marker = marker;
    }

    public void setPositions(ArrayList<LatLng> positions) {
        this.positions = positions;
    }
}
