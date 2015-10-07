package com.umbrella.jotiwa.map.area348.handling;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.umbrella.jotiwa.data.objects.area348.receivables.HunterInfo;

import java.util.ArrayList;

/**
 * Created by stesi on 3-10-2015.
 */
public class HunterObject {


    /**
     *
     */
    public HunterObject() {
        marker = new MarkerOptions();
        positions = new ArrayList<>();
        hunterInfo = new ArrayList<>();
    }

    private MarkerOptions marker;

    private ArrayList<LatLng> positions;

    private ArrayList<HunterInfo> hunterInfo;


    /**
     * @return
     */
    public ArrayList<LatLng> getPositions() {
        return positions;
    }

    /**
     * @return
     */
    public MarkerOptions getMarker() {
        return marker;
    }

    public ArrayList<HunterInfo> getHunterInfo()
    {
        return hunterInfo;
    }

    /**
     * @param marker
     */
    public void setMarker(MarkerOptions marker) {
        this.marker = marker;
    }

    /**
     * @param positions
     */
    public void setPositions(ArrayList<LatLng> positions) {
        this.positions = positions;
    }

    public void setHunterInfo(ArrayList<HunterInfo> hunterInfo) {
        this.hunterInfo = hunterInfo;
    }
}
