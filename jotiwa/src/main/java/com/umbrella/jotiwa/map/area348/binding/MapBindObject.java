package com.umbrella.jotiwa.map.area348.binding;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

/**
 * Created by stesi on 3-10-2015.
 */
public class MapBindObject {

    private ArrayList<Marker> markers;

    private ArrayList<Polyline> polylines;

    private ArrayList<Circle> circles;


    public MapBindObject()
    {
        markers = new ArrayList<>();
        polylines = new ArrayList<>();
        circles = new ArrayList<>();
    }

    public void setCircles(ArrayList<Circle> circles) {
        this.circles = circles;
    }

    public void setPolylines(ArrayList<Polyline> polylines) {
        this.polylines = polylines;
    }

    public void setMarkers(ArrayList<Marker> markers) {
        this.markers = markers;
    }

    public ArrayList<Circle> getCircles() {
        return circles;
    }

    public ArrayList<Marker> getMarkers() {
        return markers;
    }

    public ArrayList<Polyline> getPolylines() {
        return polylines;
    }

    public void setVisiblty(boolean visible)
    {
        for(int m = 0; m < this.markers.size(); m++)
        {
            this.markers.get(m).setVisible(visible);
        }
        this.markers.clear();

        for(int l = 0; l < this.polylines.size(); l++)
        {
            this.polylines.get(l).setVisible(visible);
        }
        this.polylines.clear();

        for(int c = 0; c < this.circles.size(); c++)
        {
            this.circles.get(c).setVisible(visible);
        }
        this.circles.clear();
    }

    public void remove()
    {
        for(int m = 0; m < this.markers.size(); m++)
        {
            this.markers.get(m).remove();
        }
        this.markers.clear();

        for(int l = 0; l < this.polylines.size(); l++)
        {
            this.polylines.get(l).remove();
        }
        this.polylines.clear();

        for(int c = 0; c < this.circles.size(); c++)
        {
            this.circles.get(c).remove();
        }
        this.circles.clear();
    }
}
