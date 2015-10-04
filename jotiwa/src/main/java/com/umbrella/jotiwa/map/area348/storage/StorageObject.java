package com.umbrella.jotiwa.map.area348.storage;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.umbrella.jotiwa.data.objects.area348.BaseInfo;

import java.util.ArrayList;

/**
 * Created by stesi on 2-10-2015.
 */
public class StorageObject<T> {

    public StorageObject()
    {
        this.associatedInfo = new ArrayList<>();
        this.markers = new ArrayList<>();
        this.polylines = new ArrayList<>();
        this.circles = new ArrayList<>();
    }


    //region values
    private ArrayList<MarkerOptions> markers;

    private ArrayList<PolylineOptions> polylines;

    private ArrayList<CircleOptions> circles;

    private ArrayList<BaseInfo> associatedInfo;
    //endregion


    //region fields

    //region get
    public ArrayList<MarkerOptions> getMarkers() {
        return markers;
    }

    public ArrayList<PolylineOptions> getPolylines() {
        return polylines;
    }

    public ArrayList<CircleOptions> getCircles() {
        return circles;
    }

    public ArrayList<BaseInfo> getAssociatedInfo() {
        return associatedInfo;
    }
    //endregion

    //region set
    public void setMarkers(ArrayList<MarkerOptions> markers) {
        this.markers = markers;
    }

    public void setPolylines(ArrayList<PolylineOptions> polylines) {
        this.polylines = polylines;
    }

    public void setCircles(ArrayList<CircleOptions> circles) {
        this.circles = circles;
    }

    public void setAssociatedInfo(ArrayList<BaseInfo> associatedInfo) {
        this.associatedInfo = associatedInfo;
    }
    //endregion

    //endregion



}
