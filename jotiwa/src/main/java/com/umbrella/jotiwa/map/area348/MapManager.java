package com.umbrella.jotiwa.map.area348;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;
import com.umbrella.jotiwa.communication.interaction.area348.DataUpdater;
import com.umbrella.jotiwa.map.ItemType;
import com.umbrella.jotiwa.map.area348.binding.MapBinder;
import com.umbrella.jotiwa.map.area348.handling.OnExtractionCompleted;
import com.umbrella.jotiwa.map.area348.storage.MapStorage;

import java.util.ArrayList;

/**
 * Created by stesi on 25-9-2015.
 * The final control unit for map managing.
 */
public class MapManager implements OnExtractionCompleted {


    private Context context;

    public MapManager(MapStorage storage, ArrayList<MapPartState> states,Context context)
    {
        storage.setOnExtractionCompletedListener(this);
        this.mapStorage = storage;
        this.mapBinder = new MapBinder();
        this.dataUpdater = new DataUpdater(mapStorage,context);
        this.mapPartStates = states;
        this.migrated = true;
        this.context = context;
    }

    public MapManager(Context context)
    {
        this.context = context;
        this.mapBinder = new MapBinder();
        this.mapStorage = new MapStorage(this);
        this.dataUpdater = new DataUpdater(mapStorage, context);
    }

    boolean migrated = false;

    boolean operable = false;

    ArrayList<MapPartState> mapPartStates = new ArrayList<>();

    GoogleMap gMap;

    MapBinder mapBinder;

    MapStorage mapStorage;

    DataUpdater dataUpdater;

    public MapStorage getMapStorage() {
        return mapStorage;
    }

    public ArrayList<MapPartState> getMapPartStates() {
        return mapPartStates;
    }

    public boolean isMigrated() {
        return migrated;
    }

    public void setGoogleMap(GoogleMap gMap) {
        this.gMap = gMap;
        operable = true;
        if(migrated)
        {
            for(int i = 0; i < this.mapPartStates.size(); i++)
            {
                reAddToMap(this.mapPartStates.get(i));
            }
        }
    }

    public void add(MapPartState mapPartState)
    {
        boolean copy = false;
        if(mapPartState.getTeamPart() == TeamPart.All)
        {
            TeamPart[] parts = new TeamPart[] {
                    TeamPart.Alpha, TeamPart.Bravo, TeamPart.Charlie,
                    TeamPart.Charlie, TeamPart.Delta, TeamPart.Echo,
                    TeamPart.Foxtrot, TeamPart.XRay };
            for(int x = 0; x < parts.length; x++)
            {
                this.add(new MapPartState(MapPart.Vossen, parts[x], mapPartState.getShow(), mapPartState.isUpdate()));
            }
        }

        for(int i = 0; i < mapPartStates.size(); i++)
        {
            MapPartState current = mapPartStates.get(i);
            if(current.getMapPart() == mapPartState.getMapPart() && current.getTeamPart() == mapPartState.getTeamPart())
            {
                copy = true;
            }
        }

        /**
         * Execute when the state is not a copy.
         * */
        if(!copy)
        {
            this.mapPartStates.add(mapPartState);
        }
    }

    public void update()
    {
        /**
         * Loops trough each, state and updates it.
         * */
        for(int i = 0; i < this.mapPartStates.size(); i++)
        {
            MapPartState current = this.mapPartStates.get(i);

            /**
             * Checks if the state needs updating.
             * */
            if(current.isUpdate())
            {
                /**
                 * Activated the chain update, and sets the pending value to true,
                 * to indicate that a update is beining preform on the state.
                 * */
                current.setPending(true);
                dataUpdater.update(current.getMapPart(), current.getTeamPart());
            }
        }
        /**
         * Tell the updater to interact.
         * */
        dataUpdater.interact();
    }

    public void remove(MapPartState mapPartState)
    {
        if(!this.operable) return;
        this.mapPartStates.remove(mapPartState);
    }

    private void reAddToMap(MapPartState mapPartState)
    {
        if(!this.operable) return;
        /**
         * Checks if the state has local data, if not there's no point in adding it.
         * */
        if(!mapPartState.hasLocalData(mapStorage)) return;

        /**
         * Checks if the state is on the map and if it should be shown.
         * */
        if(mapPartState.isOnMap() || !mapPartState.getShow()) return;

        /**
         * Get the markers that are on the map, and remove them from the map.
         * */
        ArrayList<Marker> onTheMapMarkers = mapBinder.getAssociated(mapPartState, ItemType.MARKERS);
        for(int i = 0; i < onTheMapMarkers.size(); i++) { onTheMapMarkers.get(i).remove(); }
        onTheMapMarkers.clear();

        /**
         * Get the new markers and add them to the map.
         * */
        ArrayList<Marker> markers = mapStorage.getAssociated(mapPartState, ItemType.MARKERS);
        mapBinder.add(mapPartState, gMap, markers, ItemType.MARKERS);

        /**
         * Restriction, so that only map types with lines get updated.
         * */
        if(mapPartState.getMapPart() == MapPart.Vossen || mapPartState.getMapPart() == MapPart.Hunters)
        {
            /**
             * Get the lines that are on the map, and remove them from the map.
             * */
            ArrayList<Polyline> onTheMapLines = mapBinder.getAssociated(mapPartState, ItemType.POLYLINES);
            for(int i = 0; i < onTheMapLines.size(); i++) { onTheMapLines.get(i).remove(); }
            onTheMapLines.clear();

            /**
             * Get the new lines and add them to the map.
             * */
            ArrayList<PolylineOptions> polylines = mapStorage.getAssociated(mapPartState, ItemType.POLYLINES);
            mapBinder.add(mapPartState, gMap, polylines, ItemType.POLYLINES);
        }

    }

    @Override
    public void onExtractionCompleted() {
        if(!this.operable) return;
        for(int i = 0; i < this.mapPartStates.size(); i++)
        {
            MapPartState mapPartState = this.mapPartStates.get(i);
            if(mapPartState.isPending())
            {
                reAddToMap(mapPartState);
                mapPartState.setPending(false);
            }
        }

    }


}
