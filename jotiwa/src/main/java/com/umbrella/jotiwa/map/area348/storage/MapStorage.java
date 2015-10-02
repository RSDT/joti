package com.umbrella.jotiwa.map.area348.storage;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;
import com.umbrella.jotiwa.data.objects.area348.BaseInfo;
import com.umbrella.jotiwa.data.objects.area348.FotoOpdrachtInfo;
import com.umbrella.jotiwa.data.objects.area348.HunterInfo;
import com.umbrella.jotiwa.data.objects.area348.ScoutingGroepInfo;
import com.umbrella.jotiwa.data.objects.area348.VosInfo;
import com.umbrella.jotiwa.map.MapItemListManager;
import com.umbrella.jotiwa.map.ItemType;
import com.umbrella.jotiwa.map.area348.HunterMapPartState;
import com.umbrella.jotiwa.map.area348.handling.HandlingResult;
import com.umbrella.jotiwa.map.area348.MapPartState;
import com.umbrella.jotiwa.map.area348.handling.OnExtractionCompleted;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by stesi on 22-9-2015.
 * Class for binding items to the map.
 *
 */
public class MapStorage extends Handler implements Parcelable {

    protected MapStorage(Parcel in) {
        Object[] objects = (Object[])in.readSerializable();
        this.handlingResults = (ArrayList<HandlingResult>)objects[0];
        this.markers = (MapItemListManager<ArrayList<MarkerOptions>>)objects[1];
        this.polylines = (MapItemListManager<ArrayList<PolylineOptions>>)objects[2];
        this.circles = (MapItemListManager<ArrayList<CircleOptions>>)objects[3];
        this.vossen = (HashMap<String, ArrayList<VosInfo>>)objects[4];
        this.hunters = (HashMap<String, ArrayList<HunterInfo>>)objects[5];
        this.sc = (HashMap<String, ArrayList<ScoutingGroepInfo>>)objects[6];
        this.foto = (HashMap<String, ArrayList<FotoOpdrachtInfo>>)objects[7];
    }

    public MapStorage(OnExtractionCompleted onExtractionCompletedListener)
    {
        this.onExtractionCompletedListener = onExtractionCompletedListener;
        this.markers = new MapItemListManager<>();
        this.polylines = new MapItemListManager<>();
        this.circles = new MapItemListManager<>();

        this.vossen = new HashMap<>();
        this.hunters = new HashMap<>();
        this.sc = new HashMap<>();
        this.foto = new HashMap<>();
    }

    public void setOnExtractionCompletedListener(OnExtractionCompleted onExtractionCompletedListener) {
        this.onExtractionCompletedListener = onExtractionCompletedListener;
    }

    private MapItemListManager<ArrayList<MarkerOptions>> markers;

    private MapItemListManager<ArrayList<PolylineOptions>> polylines;

    private MapItemListManager<ArrayList<CircleOptions>> circles;

    private HashMap<String, ArrayList<VosInfo>> vossen;

    private HashMap<String, ArrayList<HunterInfo>> hunters;

    private HashMap<String, ArrayList<ScoutingGroepInfo>> sc;

    private HashMap<String, ArrayList<FotoOpdrachtInfo>> foto;


    private ArrayList<HandlingResult> handlingResults = new ArrayList<>();

    private OnExtractionCompleted onExtractionCompletedListener;

    public static final Creator<MapStorage> CREATOR = new Creator<MapStorage>() {
        @Override
        public MapStorage createFromParcel(Parcel in) {
            return new MapStorage(in);
        }

        @Override
        public MapStorage[] newArray(int size) {
            return new MapStorage[size];
        }
    };

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



    public <T> ArrayList<T> getAssociatedMapItems(MapPartState mapPartState, ItemType type)
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
        if(mapPartState.getMapPart() == MapPart.Hunters)
        {
            ArrayList<ArrayList<T>> items = new ArrayList<>();
            for(String name : hunters.keySet())
            {
                items.add((ArrayList<T>)manager.getItem(name));
            }
            return (ArrayList<T>)items;
        }
        else
        {
            return (ArrayList<T>)manager.getItem(mapPartState.getMapPart().getValue());
        }
    }

    public <T> ArrayList<T> getAssociatedInfos(MapPartState mapPartState)
    {
        Map map = null;
        switch(mapPartState.getMapPart())
        {
            case Vossen:
                map = vossen;
                break;
            case Hunters:
                map = hunters;
                break;
            case ScoutingGroepen:
                map = sc;
                break;
            case FotoOpdrachten:
                map = foto;
                break;
        }

        if(mapPartState.getMapPart() == MapPart.Vossen)
        {
            return (ArrayList<T>)map.get(mapPartState.getTeamPart().getSubChar());
        }
        else
        {
            if(mapPartState.getMapPart() == MapPart.Hunters)
            {
                ArrayList<T> buffer = new ArrayList<>();
                HunterMapPartState hunterMapPartState = (HunterMapPartState)mapPartState;
                String[] accessors = hunterMapPartState.getAccessors();
                for(int i = 0; i < accessors.length; i++)
                {
                    buffer.add((T)map.get(accessors[i]));
                }
                return buffer;
            }
            else
            {
                return (ArrayList<T>)map.get(mapPartState.getMapPart().getValue());
            }
        }
    }

    public <T extends BaseInfo> T findInfo(MapPartState mapPartState, int id) {
        ArrayList<T> infos = getAssociatedInfos(mapPartState);
        for(int i = 0; i < infos.size(); i++)
        {
            if(infos.get(i).id == id) return infos.get(i);
        }
        return null;
    }

    public HunterInfo findHunterInfo(String name, int id)
    {
        ArrayList<HunterInfo> hunterInfos = hunters.get(name);
        if(hunterInfos != null)
        {
            for(int i = 0; i < hunterInfos.size(); i++)
            {
                if(hunterInfos.get(i).id == id) return hunterInfos.get(i);
            }
        }
        return null;
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
        MapPartState special = null;
        for(int i = 0; i < results.length; i++)
        {
            HandlingResult current = results[i];
            switch(current.getMapPart())
            {
                case Vossen:

                    /**
                     * Do a safety check, then get the current marker list and clear it.
                     * After that add the marker to the list.
                     * */
                    safetyCheck(current.getMapPart(), current.getTeamPart(), ItemType.MARKERS);
                    ArrayList<MarkerOptions> markersVos = markers.getItem(current.getTeamPart().getSubChar());
                    markersVos.clear();
                    markersVos.addAll((ArrayList<MarkerOptions>) current.getObjects()[0]);

                    /**
                     * Do a safety check, then get the current polyline list and clear it.
                     * After that add the polyline to the list.
                     * */
                    safetyCheck(current.getMapPart(), current.getTeamPart(), ItemType.POLYLINES);
                    ArrayList<PolylineOptions> linesVos = polylines.getItem(current.getTeamPart().getSubChar());
                    linesVos.clear();
                    linesVos.add((PolylineOptions) current.getObjects()[1]);

                    /**
                     * Do a safety check, then get the current circle list and clear it.
                     * After that add the circle to the list.
                     * */
                    safetyCheck(current.getMapPart(), current.getTeamPart(), ItemType.CIRCLES);
                    ArrayList<CircleOptions> circlesVos = circles.getItem(current.getTeamPart().getSubChar());
                    circlesVos.clear();
                    circlesVos.add((CircleOptions) current.getObjects()[2]);

                    /**
                     * Do a safety check, then get the current info list and clear it.
                     * After that add the new info to the list.
                     * */
                    safetyCheckInfo(current.getMapPart(), current.getTeamPart());
                    ArrayList<VosInfo> info = vossen.get(current.getTeamPart().getSubChar());
                    info.clear();
                    info.addAll(Arrays.asList((VosInfo[]) current.getObjects()[3]));
                    break;
                case Hunters:
                    /**
                     * TODO:Implement add code.
                     * */
                    HashMap<String, MarkerOptions> mapMarkers = (HashMap<String, MarkerOptions>)current.getObjects()[0];
                    for(Map.Entry<String, MarkerOptions> entry : mapMarkers.entrySet())
                    {
                        if(markers.getItem(entry.getKey()) == null) markers.newItem(entry.getKey(), new ArrayList<MarkerOptions>());
                        ArrayList<MarkerOptions> markersHunter = markers.getItem(entry.getKey());
                        markersHunter.clear();
                        markersHunter.add(entry.getValue());
                    }

                    HashMap<String, ArrayList<LatLng>> mapLocations = (HashMap<String, ArrayList<LatLng>>)current.getObjects()[1];
                    for(Map.Entry<String, ArrayList<LatLng>> entry : mapLocations.entrySet())
                    {
                        /**
                         * Safety check to see if the item exists, if not create one.
                         * */
                        if(polylines.getItem(entry.getKey()) == null) polylines.newItem(entry.getKey(), new ArrayList<PolylineOptions>());

                        /**
                         * Get the list of polylines of the hunter. It's one.
                         * */
                        ArrayList<PolylineOptions> polylinesHunter = polylines.getItem(entry.getKey());

                        /**
                         * Gets the list of the new positions out of the line.
                         * */
                        ArrayList<LatLng> latLngsNew = entry.getValue();

                        if(polylinesHunter.size() > 0)
                        {
                            PolylineOptions polylineHunter = polylinesHunter.get(0);
                            /**
                             * Get the list of positions out of the line.
                             * */
                            ArrayList<LatLng> latLngs = (ArrayList<LatLng>)polylineHunter.getPoints();

                            /**
                             * Loop through each new LatLng.
                             * */
                            for(int a = 0; i < latLngsNew.size(); i++)
                            {
                                LatLng currentLatLng = latLngsNew.get(a);
                                boolean copy = false;
                                /**
                                 * Loop through each old LatLng, and compare it.
                                 * */
                                for(int s = 0; s < latLngs.size(); s++)
                                {
                                    if(currentLatLng == latLngs.get(s))
                                    {
                                        copy = true;
                                    }
                                }

                                /**
                                 * If the location is not a copy, it is new so add it to the map.
                                 * */
                                if(!copy)
                                {
                                    polylineHunter.add(currentLatLng);
                                }
                            }
                        }
                        else
                        {
                            PolylineOptions pOptions = new PolylineOptions();
                            pOptions.addAll(latLngsNew);
                            pOptions.color(Color.GRAY);
                            pOptions.width(5);
                            polylinesHunter.add(pOptions);
                        }
                    }

                    HunterInfo[][] hunterInfos = (HunterInfo[][])current.getObjects()[2];
                    int count = 0;
                    String[] accessors = new String[mapMarkers.keySet().size()];
                    for(String name : mapMarkers.keySet())
                    {
                        if(hunters.get(name) == null) hunters.put(name, new ArrayList<HunterInfo>());
                        ArrayList<HunterInfo> infos = hunters.get(name);
                        infos.clear();
                        infos.addAll(Arrays.asList(hunterInfos[count]));
                        accessors[count] = name;
                        count++;
                    }
                    special = new HunterMapPartState(accessors);
                    break;
                case ScoutingGroepen:

                    /**
                     * Do a safety check, then get the current marker list and clear it.
                     * After that add the marker to the list.
                     * */
                    safetyCheck(current.getMapPart(), current.getTeamPart(), ItemType.MARKERS);
                    ArrayList<MarkerOptions> markersSc = markers.getItem(current.getMapPart().getValue());
                    markersSc.clear();
                    markersSc.addAll((ArrayList<MarkerOptions>) current.getObjects()[0]);

                    /**
                     * Do a safety check, then get the current circle list and clear it.
                     * After that add the circle to the list.
                     * */
                    safetyCheck(current.getMapPart(), current.getTeamPart(), ItemType.CIRCLES);
                    ArrayList<CircleOptions> circlesSc = circles.getItem(current.getMapPart().getValue());
                    circlesSc.clear();
                    circlesSc.addAll((ArrayList<CircleOptions>)current.getObjects()[1]);

                    /**
                     * Do a safety check, then get the current info list and clear it.
                     * After that add the new info to the list.
                     * */
                    safetyCheckInfo(current.getMapPart(), current.getTeamPart());
                    ArrayList<ScoutingGroepInfo> infoSc = sc.get(current.getMapPart().getValue());
                    infoSc.clear();
                    infoSc.addAll(Arrays.asList((ScoutingGroepInfo[]) current.getObjects()[2]));

                    break;
                case FotoOpdrachten:

                    /**
                     * Do a safety check, then get the current marker list and clear it.
                     * After that add the marker to the list.
                     * */
                    safetyCheck(current.getMapPart(), current.getTeamPart(), ItemType.MARKERS);
                    ArrayList<MarkerOptions> markersFoto = markers.getItem(current.getMapPart().getValue());
                    markersFoto.clear();
                    markersFoto.addAll((ArrayList<MarkerOptions>) current.getObjects()[0]);

                    /**
                     * Do a safety check, then get the current info list and clear it.
                     * After that add the new info to the list.
                     * */
                    safetyCheckInfo(current.getMapPart(), current.getTeamPart());
                    ArrayList<FotoOpdrachtInfo> infoFoto = foto.get(current.getMapPart().getValue());
                    infoFoto.clear();
                    infoFoto.addAll(Arrays.asList((FotoOpdrachtInfo[]) current.getObjects()[1]));

                    break;
            }
        }
        this.onExtractionCompletedListener.onExtractionCompleted(special);
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

    private void safetyCheckInfo(MapPart part, TeamPart teamPart)
    {
        switch(part)
        {
            case Vossen:
                if(vossen.get(teamPart.getSubChar()) == null) vossen.put(teamPart.getSubChar(), new ArrayList<VosInfo>());
                break;
            case Hunters:
                if(hunters.get(part.getValue()) == null) hunters.put(part.getValue(), new ArrayList<HunterInfo>());
                break;
            case ScoutingGroepen:
                if(sc.get(part.getValue()) == null) sc.put(part.getValue(), new ArrayList<ScoutingGroepInfo>());
                break;
            case FotoOpdrachten:
                if(foto.get(part.getValue()) == null) foto.put(part.getValue(), new ArrayList<FotoOpdrachtInfo>());
                break;
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(new Object[] { this.handlingResults, this.markers, this.polylines, this.circles, this.vossen, this.hunters, this.sc, this.foto });
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
