package com.umbrella.jotiwa.map.area348;

import android.os.Handler;
import android.os.Message;

import com.google.android.gms.maps.GoogleMap;
import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;
import com.umbrella.jotiwa.communication.interaction.area348.DataUpdater;
import com.umbrella.jotiwa.map.area348.binding.MapBinder;
import com.umbrella.jotiwa.map.area348.handling.OnNewDataAvailable;
import com.umbrella.jotiwa.map.area348.storage.MapStorage;

import java.util.ArrayList;

/**
 * Created by stesi on 25-9-2015.
 * The final control unit for map managing.
 */
public class MapManager extends ArrayList<MapPartState> implements OnNewDataAvailable {

    public MapManager(GoogleMap gMap)
    {
        super();
        this.gMap = gMap;
        this.mapBinder = new MapBinder(gMap);
        mapManagerHandler = new MapManagerHandler();
        mapStorage = new MapStorage(this);
        dataUpdater = new DataUpdater();
        this.operable = true;
    }

    /**
     * Value indicating if the manager is operable.
     * */
    boolean operable = false;

    /**
     * The reference to the GoogleMap.
     * */
    GoogleMap gMap;


    MapBinder mapBinder;


    public MapBinder getMapBinder() {
        return mapBinder;
    }

    public class MapManagerHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what)
            {

            }
            onNewDataAvailable((ArrayList<MapPartState>)msg.obj);
            super.handleMessage(msg);
        }
    }

    private static MapManagerHandler mapManagerHandler;

    public static MapManagerHandler getMapManagerHandler() {
        return mapManagerHandler;
    }

    /**
     * TODO:Consider making MapStorage and DataUpdater static, handler should be static else memory leaking.
     * */
    private static MapStorage mapStorage;

    private static DataUpdater dataUpdater;

    public static DataUpdater getDataUpdater() {
        return dataUpdater;
    }

    public static MapStorage getMapStorage() {
        return mapStorage;
    }


    @Override
    public boolean add(MapPartState mapPartState)
    {
        /**
         * Some recursive calls, the All identifiers signal that ALL states of that part should be added.
         * Here we detect if the all identifier is used.
         * */
        switch(mapPartState.getMapPart())
        {
            case All:
                add(new MapPartState(MapPart.Vossen, mapPartState.getTeamPart(), mapPartState.getShow(), mapPartState.update()));
                add(new MapPartState(MapPart.Hunters, TeamPart.None, mapPartState.getShow(), mapPartState.update()));
                add(new MapPartState(MapPart.ScoutingGroepen, TeamPart.None, mapPartState.getShow(), mapPartState.update()));
                add(new MapPartState(MapPart.FotoOpdrachten, TeamPart.None, mapPartState.getShow(), mapPartState.update()));
                return true;
            case Vossen:
                if(mapPartState.getTeamPart() == TeamPart.All)
                {
                    TeamPart[] parts = new TeamPart[] {
                            TeamPart.Alpha, TeamPart.Bravo, TeamPart.Charlie,
                            TeamPart.Charlie, TeamPart.Delta, TeamPart.Echo,
                            TeamPart.Foxtrot, TeamPart.XRay };
                    for(int x = 0; x < parts.length; x++)
                    {
                        this.add(new MapPartState(MapPart.Vossen, parts[x], mapPartState.getShow(), mapPartState.update()));
                    }
                    return true;
                }
        }

        /**
         * Checks if the state collection already has the spefic state, if so there's no point in adding it.
         * */
        if(!this.contains(mapPartState))
        {
            return super.add(mapPartState);
        }

        /**
         * This code should not be reached if the adding was successful, so return false to indicate a failure or a duplicate.
         * */
        return false;
    }

    public void update()
    {
        /**
         * Loops trough each, state and updates it.
         * */
        for(int i = 0; i < this.size(); i++)
        {
            MapPartState current = this.get(i);

            /**
             * Checks if the state needs updating.
             * */
            if(current.update())
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

    @Override
    /**
     * Removes the state from the collection.
     * NOTE: Data will be kept at the storage.
     * TODO: Remove MapItems from the map.
     * */
    public boolean remove(Object object)
    {
        return super.remove(object);
    }

    /**
     * Syncs the storage with the Map with help of the MapBinder.
     * */
    private void sync(MapPartState mapPartState)
    {
        mapBinder.add(mapPartState, mapStorage.getAssociatedStorageObject(mapPartState), MapBinder.MapBinderAddOptions.MAP_BINDER_ADD_OPTIONS_CLEAR);
    }

    @Override
    /**
     * Checks if a state is present or a similar state is present.
     * */
    public boolean contains(Object object) {
        /**
         * Checks if the object is a MapPartState.
         * */
        if(super.contains(object))
        {
            /**
             * Checks if a similar state does exist, if so return true.
             * */
            MapPartState state = (MapPartState)object;
            for(int i = 0; i < this.size(); i++)
            {
                MapPartState current = this.get(i);
                if(current.getMapPart() == state.getMapPart() && current.getTeamPart() == state.getTeamPart() && current.getAccessor().matches(state.getAccessor()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Finds a state.
     * */
    public MapPartState findState(MapPart mapPart, TeamPart teamPart, String accessor)
    {
        for(int i = 0; i < this.size(); i++)
        {
            MapPartState state = this.get(i);
            if(state.getMapPart() == mapPart && state.getTeamPart() == teamPart && state.getAccessor().matches(accessor))
            {
                return state;
            }
        }
        return null;
    }

    @Override
    /**
     * Gets invoked when new data is available -> update the states and sync them.
     * */
    public void onNewDataAvailable(ArrayList<MapPartState> newStates) {
        if(!this.operable) return;

        if(newStates != null)
        {
            this.addAll(newStates);
        }

        for(int i = 0; i < this.size(); i++)
        {
            MapPartState current = this.get(i);
            if(current.isPending())
            {
                sync(current);
                current.setPending(false);
            }
        }
    }

}
