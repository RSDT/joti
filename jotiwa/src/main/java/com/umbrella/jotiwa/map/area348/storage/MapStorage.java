package com.umbrella.jotiwa.map.area348.storage;

import android.os.Handler;
import android.os.Message;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;
import com.umbrella.jotiwa.map.MapItemListManager;
import com.umbrella.jotiwa.map.ItemType;
import com.umbrella.jotiwa.map.area348.handling.HandlingResult;
import com.umbrella.jotiwa.map.area348.MapPartState;
import com.umbrella.jotiwa.map.area348.handling.OnExtractionCompleted;


import java.util.ArrayList;

/**
 * Created by stesi on 22-9-2015.
 * Class for binding items to the map.
 *
 */
public class MapStorage extends Handler {


    public MapStorage(OnExtractionCompleted onExtractionCompletedListener)
    {
        this.onExtractionCompletedListener = onExtractionCompletedListener;
        this.markers = new MapItemListManager<>();
        this.polylines = new MapItemListManager<>();
        this.circles = new MapItemListManager<>();
    }

    private MapItemListManager<ArrayList<MarkerOptions>> markers;

    private MapItemListManager<ArrayList<PolylineOptions>> polylines;

    private MapItemListManager<ArrayList<CircleOptions>> circles;

    private ArrayList<HandlingResult> handlingResults = new ArrayList<>();

    private OnExtractionCompleted onExtractionCompletedListener;

    public HandlingResult getAssociatedHandlingResult(MapPartState mapPartState)
    {
        for(int i = 0; i < this.handlingResults.size(); i++)
        {
            HandlingResult current = handlingResults.get(i);
            if(current.getMapPart() == mapPartState.getMapPart() && current.getTeamPart() == mapPartState.getTeamPart())
            {
                return current;
            }
        }
        return null;
    }

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
            return (ArrayList<T>)manager.getItem(mapPartState.getTeamPart().getSubChar());
        }
        else
        {
            return (ArrayList<T>)manager.getItem(mapPartState.getMapPart().getValue());
        }
    }


    @Override
    public void handleMessage(Message msg) {

        /**
         * Get the handling results out of the message.
         * */
        HandlingResult[] results = (HandlingResult[])msg.obj;

        /**
         * Loops through each new handling result.
         * */
        for(int i = 0; i < results.length; i++)
        {
            /**
             * Get the current handling result and create value indicating if a copy is present.
             * */
            HandlingResult current = results[i];
            boolean copy = false;

            /**
             * Loops through each (old) local handling result.
             * */
            for(int x = 0; x < this.handlingResults.size(); x++)
            {
                HandlingResult compareResult = this.handlingResults.get(x);
                /**
                 * Checks if a predicate already exists.
                 * */
                if(current.getMapPart() == compareResult.getMapPart() && current.getTeamPart() == compareResult.getTeamPart())
                {
                    /**
                     * Removes the old result and adds the new one.
                     * */
                    copy = true;
                    this.handlingResults.remove(x);
                    this.handlingResults.add(current);
                }
            }

            /**
             * If the handling result is not a copy, then add it without removing it first.
             * */
            if(!copy)
            {
                this.handlingResults.add(current);
            }
        }
        this.extract(results);
        super.handleMessage(msg);
    }

    private void extract(HandlingResult[] results)
    {
        for(int i = 0; i < results.length; i++)
        {
            HandlingResult current = results[i];
            switch(current.getMapPart())
            {
                case Vossen:

                    /**
                     * Do a safety check, then get the current list and clear it.
                     * After that add the marker to the list.
                     * */
                    safetyCheck(current.getMapPart(), current.getTeamPart(), ItemType.MARKERS);
                    ArrayList<MarkerOptions> markersVos = markers.getItem(current.getTeamPart().getSubChar());
                    markersVos.clear();
                    markersVos.addAll((ArrayList<MarkerOptions>) current.getObjects()[0]);

                    /**
                     * Do a safety check, then get the current list and clear it.
                     * After that add the polyline to the list.
                     * */
                    safetyCheck(current.getMapPart(), current.getTeamPart(), ItemType.POLYLINES);
                    ArrayList<PolylineOptions> linesVos = polylines.getItem(current.getTeamPart().getSubChar());
                    linesVos.clear();
                    linesVos.add((PolylineOptions)current.getObjects()[1]);

                    break;
            }
        }
        this.onExtractionCompletedListener.onExtractionCompleted();
    }

    private void safetyCheck(MapPart mapPart, TeamPart teamPart, ItemType type)
    {
        switch(type)
        {
            case MARKERS:
                if(mapPart == MapPart.Vossen)
                {
                    if(this.markers.getItem(teamPart.getSubChar()) == null)
                        this.markers.newItem(teamPart.getSubChar(), new ArrayList<MarkerOptions>());
                }
                else
                {
                    if(this.markers.getItem(mapPart.getValue()) == null)
                        this.markers.newItem(mapPart.getValue(), new ArrayList<MarkerOptions>());
                }

                break;
            case POLYLINES:
                if(mapPart == MapPart.Vossen)
                {
                    if(this.polylines.getItem(teamPart.getSubChar()) == null)
                        this.polylines.newItem(teamPart.getSubChar(), new ArrayList<PolylineOptions>());
                }
                else
                {
                    if(this.polylines.getItem(mapPart.getValue()) == null)
                        this.polylines.newItem(mapPart.getValue(), new ArrayList<PolylineOptions>());
                }

                break;
            case CIRCLES:
                if(mapPart == MapPart.Vossen)
                {
                    if(this.circles.getItem(teamPart.getSubChar()) == null)
                        this.circles.newItem(teamPart.getSubChar(), new ArrayList<CircleOptions>());
                }
                else
                {
                    if(this.circles.getItem(mapPart.getValue()) == null)
                        this.circles.newItem(mapPart.getValue(), new ArrayList<CircleOptions>());
                }
                break;
        }
    }

}
