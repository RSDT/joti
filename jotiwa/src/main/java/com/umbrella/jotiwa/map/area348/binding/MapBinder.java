package com.umbrella.jotiwa.map.area348.binding;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.map.MapItemListManager;
import com.umbrella.jotiwa.map.ItemType;
import com.umbrella.jotiwa.map.area348.MapPartState;

import java.util.ArrayList;

/**
 * Created by stesi on 25-9-2015.
 */
public class MapBinder {

    public MapBinder()
    {
        markers = new MapItemListManager<>();
        polylines = new MapItemListManager<>();
        circles = new MapItemListManager<>();
    }

    private MapItemListManager<ArrayList<Marker>> markers;

    private MapItemListManager<ArrayList<Polyline>> polylines;

    private MapItemListManager<ArrayList<Circle>> circles;

    public <T> ArrayList<T> getAssociated(MapPartState mapPartState, ItemType type)
    {
        MapItemListManager manager = null;
        switch(type)
        {
            case MARKERS:
                manager = markers;
                break;
            case POLYLINES:
                manager = polylines;
                break;
            case CIRCLES:
                manager = circles;
                break;
        }

        if(mapPartState.getMapPart() == MapPart.Vossen)
        {
            safetyCheck(mapPartState, type);
            return (ArrayList<T>)manager.getItem(mapPartState.getTeamPart().getSubChar());
        }
        else
        {
            safetyCheck(mapPartState, type);
            return (ArrayList<T>)manager.getItem(mapPartState.getMapPart().getValue());
        }
    }

    public void add(MapPartState mapPartState, GoogleMap gMap, ArrayList items, ItemType type)
    {
        switch(type)
        {
            case MARKERS:
                ArrayList<Marker> markers;
                if(mapPartState.getMapPart() == MapPart.Vossen)
                {
                    safetyCheck(mapPartState, type);
                   markers = this.markers.getItem(mapPartState.getTeamPart().getSubChar());
                }
                else
                {
                    safetyCheck(mapPartState, type);
                    markers = this.markers.getItem(mapPartState.getMapPart().getValue());
                }
                ArrayList<MarkerOptions> markersOption = items;
                for(int i = 0; i < markersOption.size(); i++)
                {
                    markers.add(gMap.addMarker(markersOption.get(i)));
                }
                break;

            case POLYLINES:
                ArrayList<Polyline> polylines;
                if(mapPartState.getMapPart() == MapPart.Vossen)
                {
                    safetyCheck(mapPartState, type);
                    polylines = this.polylines.getItem(mapPartState.getTeamPart().getSubChar());
                }
                else
                {
                    safetyCheck(mapPartState, type);
                    polylines = this.polylines.getItem(mapPartState.getMapPart().getValue());
                }
                ArrayList<PolylineOptions> polylineOptions = items;
                for(int i = 0; i < polylineOptions.size(); i++)
                {
                    polylines.add(gMap.addPolyline(polylineOptions.get(i)));
                }
                break;

            case CIRCLES:
                ArrayList<Circle> circles;
                if(mapPartState.getMapPart() == MapPart.Vossen)
                {
                    safetyCheck(mapPartState, type);
                    circles = this.circles.getItem(mapPartState.getTeamPart().getSubChar());
                }
                else
                {
                    safetyCheck(mapPartState, type);
                    circles = this.circles.getItem(mapPartState.getMapPart().getValue());
                }
                ArrayList<CircleOptions> circleOptions = items;
                for(int i = 0; i < circleOptions.size(); i++)
                {
                    circles.add(gMap.addCircle(circleOptions.get(i)));
                }

                break;
        }




    }

    private void safetyCheck(MapPartState mapPartState, ItemType type)
    {
        switch(type)
        {
            case MARKERS:
                if(mapPartState.getMapPart() == MapPart.Vossen)
                {
                    if(this.markers.getItem(mapPartState.getTeamPart().getSubChar()) == null)
                        this.markers.newItem(mapPartState.getTeamPart().getSubChar(), new ArrayList<Marker>());
                }
                else
                {
                    if(this.markers.getItem(mapPartState.getMapPart().getValue()) == null)
                        this.markers.newItem(mapPartState.getMapPart().getValue(), new ArrayList<Marker>());
                }
                break;
            case POLYLINES:
                if(mapPartState.getMapPart() == MapPart.Vossen)
                {
                    if(this.polylines.getItem(mapPartState.getTeamPart().getSubChar()) == null)
                        this.polylines.newItem(mapPartState.getTeamPart().getSubChar(), new ArrayList<Polyline>());
                }
                else
                {
                    if(this.polylines.getItem(mapPartState.getMapPart().getValue()) == null)
                        this.polylines.newItem(mapPartState.getMapPart().getValue(), new ArrayList<Polyline>());
                }
                break;
            case CIRCLES:
                if(mapPartState.getMapPart() == MapPart.Vossen)
                {
                    if(this.circles.getItem(mapPartState.getTeamPart().getSubChar()) == null)
                        this.circles.newItem(mapPartState.getTeamPart().getSubChar(), new ArrayList<Circle>());
                }
                else
                {
                    if(this.circles.getItem(mapPartState.getMapPart().getValue()) == null)
                        this.circles.newItem(mapPartState.getMapPart().getValue(), new ArrayList<Circle>());
                }
                break;
        }
    }
}
