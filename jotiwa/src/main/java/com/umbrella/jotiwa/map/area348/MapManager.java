package com.umbrella.jotiwa.map.area348;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.umbrella.jotiwa.JotiApp;
import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;
import com.umbrella.jotiwa.communication.interaction.area348.DataUpdater;
import com.umbrella.jotiwa.map.area348.binding.MapBinder;
import com.umbrella.jotiwa.map.area348.handling.OnNewDataAvailable;
import com.umbrella.jotiwa.map.area348.storage.MapStorage;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * The final control unit for map managing.
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 25-9-2015
 */
public class MapManager extends ArrayList<MapPartState> implements OnNewDataAvailable {

    /**
     * @param gMap The google map that the manager should manage on.
     */
    public MapManager(GoogleMap gMap) {
        super();
        this.gMap = gMap;

        /**
         * Create new map binder.
         * */
        this.mapBinder = new MapBinder(gMap);

        /**
         * (Re)create the handler to set the OnNewDataAvailable listener to this class.
         * */
        mapManagerHandler = new MapManagerHandler(this);

        /**
         * Only create a storage once, else data is lost.
         * */
        if (mapStorage == null) {
            mapStorage = new MapStorage();
        }

        /**
         * Only create a data updater once, because it has no references to this class.
         * */
        if (dataUpdater == null) {
            dataUpdater = new DataUpdater();
        }
    }

    /**
     * The reference to the GoogleMap.
     */
    GoogleMap gMap;


    /**
     * The binder that binds items to the map.
     */
    MapBinder mapBinder;


    /**
     * @return
     */
    public MapBinder getMapBinder() {
        return mapBinder;
    }

    /**
     *
     */
    public void cameraToCurrentLocation() {
        Location location = JotiApp.getLastLocation();
        CameraUpdate camera;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext());
        float zoom = Float.parseFloat(preferences.getString("pref_zoom", "10"));
        if (location == null) {
            JotiApp.debug("lastlocation = null");
            JotiApp.toast("Nog geen locatie gevonden");
            camera = CameraUpdateFactory.newLatLngZoom(new LatLng(52.021675, 6.059437), zoom);
        } else {
            JotiApp.debug("lastlocation = " + location.toString());
            camera = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoom);
        }

        gMap.moveCamera(camera);
    }

    private static MapManagerHandler mapManagerHandler;

    /**
     * @return
     */
    public static MapManagerHandler getMapManagerHandler() {
        return mapManagerHandler;
    }

    private static MapStorage mapStorage;

    private static DataUpdater dataUpdater;

    /**
     * @return
     */
    public static DataUpdater getDataUpdater() {
        return dataUpdater;
    }

    /**
     * @return
     */
    public static MapStorage getMapStorage() {
        return mapStorage;
    }


    /**
     * @param mapPartState
     * @return true if something was added. false if nothing was added.
     */
    @Override
    public boolean add(MapPartState mapPartState) {
        /**
         * Some recursive calls, the All identifiers signal that ALL states of that part should be added.
         * Here we detect if the all identifier is used.
         * */
        switch (mapPartState.getMapPart()) {
            case All:
                add(new MapPartState(MapPart.Vossen, mapPartState.getTeamPart(), mapPartState.getShow(), mapPartState.update()));
                add(new MapPartState(MapPart.Hunters, TeamPart.None, mapPartState.getShow(), mapPartState.update()));
                add(new MapPartState(MapPart.ScoutingGroepen, mapPartState.getTeamPart(), mapPartState.getShow(), mapPartState.update()));
                add(new MapPartState(MapPart.FotoOpdrachten, TeamPart.None, mapPartState.getShow(), mapPartState.update()));
                return true;
            case Vossen:
                if (mapPartState.getTeamPart() == TeamPart.All) {
                    TeamPart[] parts = new TeamPart[]{
                            TeamPart.Alpha, TeamPart.Bravo, TeamPart.Charlie,
                            TeamPart.Charlie, TeamPart.Delta, TeamPart.Echo,
                            TeamPart.Foxtrot, TeamPart.XRay};
                    boolean return_value = false;
                    for (int x = 0; x < parts.length; x++) {
                        if(this.add(new MapPartState(MapPart.Vossen, parts[x], mapPartState.getShow(), mapPartState.update())))
                            return_value = true;
                    }
                    return return_value;
                }
            case ScoutingGroepen:
                if (mapPartState.getTeamPart() == TeamPart.All) {
                    TeamPart[] parts = new TeamPart[]{
                            TeamPart.Alpha, TeamPart.Bravo, TeamPart.Charlie,
                            TeamPart.Charlie, TeamPart.Delta, TeamPart.Echo,
                            TeamPart.Foxtrot, TeamPart.XRay};
                    boolean return_value = false;
                    for (int x = 0; x < parts.length; x++) {
                        if(this.add(new MapPartState(MapPart.ScoutingGroepen, parts[x], mapPartState.getShow(), mapPartState.update())))
                            return_value = true;
                    }
                    return return_value;
                }
        }

        /**
         * Checks if the state collection already has the spefic state, if so there's no point in adding it.
         * */
        if (!this.contains(mapPartState)) {
            return super.add(mapPartState);
        }

        /**
         * This code should not be reached if the adding was successful, so return false to indicate a failure or a duplicate.
         * */
        return false;
    }

    /**
     * @param collection
     * @return
     */
    @Override
    public boolean addAll(Collection<? extends MapPartState> collection) {

        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            this.add((MapPartState) iterator.next());
        }
        return true;
    }


    /**
     * Updates all the states storages by requesting the data from the server.
     */
    public void update() {
        /**
         * Loops trough each, state and updates it.
         * */
        for (int i = 0; i < this.size(); i++) {
            MapPartState current = this.get(i);

            /**
             * Checks if the state needs updating.
             * */
            if (current.update()) {
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


    /**
     * Removes the state from the collection.
     * NOTE: Data will be kept at the storage.
     * TODO: Remove MapItems from the map.
     *
     * @param object
     * @return
     */
    @Override
    public boolean remove(Object object) {
        mapBinder.getAssociatedMapBindObject((MapPartState)object).remove();
        return super.remove(object);
    }

    /**
     * Syncs the specific state's storage with the Map with help of the MapBinder.
     *
     * @param mapPartState The state that should be synced.
     */
    public void sync(MapPartState mapPartState) {
        mapBinder.add(mapPartState, mapStorage.getAssociatedStorageObject(mapPartState), MapBinder.MapBinderAddOptions.MAP_BINDER_ADD_OPTIONS_CLEAR);
    }

    /**
     * Syncs each state's storage with the Map with help of the MapBinder.
     *
     * @param states The states that should be synced.
     */
    public void sync(ArrayList<MapPartState> states) {
        for (int i = 0; i < states.size(); i++) {
            if (this.get(i).hasLocalData()) {
                sync(states.get(i));
            }
        }
    }

    /**
     * Syncs each state's storage with the Map with help of the MapBinder.
     */
    public void syncAll() {
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).hasLocalData()) {
                sync(this.get(i));
            }
        }
    }


    /**
     * Checks if a state is present or a similar state is present.
     *
     * @param object
     * @return
     */
    @Override
    public boolean contains(Object object) {
        /**
         * Checks if the object is a MapPartState.
         * */
        if (super.contains(object)) {
            /**
             * Checks if a similar state does exist, if so return true.
             * */
            MapPartState state = (MapPartState) object;
            for (int i = 0; i < this.size(); i++) {
                MapPartState current = this.get(i);
                if (current.getMapPart() == state.getMapPart() && current.getTeamPart() == state.getTeamPart() && current.getAccessor().matches(state.getAccessor())) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Finds a state.
     *
     * @param mapPart The MapPart of the state.
     * @param teamPart The TeamPart of the state.
     * @param accessor The accessor of the state.
     * @return The founded state, return null if no state is found.
     */
    public MapPartState findState(MapPart mapPart, TeamPart teamPart, String accessor) {
        for (int i = 0; i < this.size(); i++) {
            MapPartState state = this.get(i);
            if (state.getMapPart() == mapPart && state.getTeamPart() == teamPart && state.getAccessor().matches(accessor)) {
                return state;
            }
        }
        return null;
    }

    /**
     * Gets invoked when new data is available -> update the states and sync them.
     *
     * @param newStates The array list of new states that should be added.
     */
    @Override
    public void onNewDataAvailable(ArrayList<MapPartState> newStates) {

        /**
         * Checks if there are new states, if so add them.
         * */
        if (newStates != null) {
            this.addAll(newStates);
        }

        /**
         * Loop trough each state.
         * */
        for (int i = 0; i < this.size(); i++) {
            MapPartState current = this.get(i);

            /**
             * Check if the state is pending, so thereby is expecting a update.
             * */
            if (current.isPending()) {
                current.setHasLocalData(true);
                current.setHasNewData(true);
                current.setPending(false);
                sync(current);
            }
        }
    }

    /**
     * The handler that receives messages and thereby executes the associated UI code on the main thread.
     */
    public static class MapManagerHandler extends Handler {

        public MapManagerHandler(OnNewDataAvailable onNewDataAvailable)
        {
            this.onNewDataAvailableWeakReference = new WeakReference<>(onNewDataAvailable);
        }

        private WeakReference<OnNewDataAvailable> onNewDataAvailableWeakReference;

        @Override
        public void handleMessage(Message msg) {

            OnNewDataAvailable onNewDataAvailableRef = this.onNewDataAvailableWeakReference.get();
            if(onNewDataAvailableRef != null)
            {
                onNewDataAvailableRef.onNewDataAvailable((ArrayList<MapPartState>)msg.obj);
            }
            super.handleMessage(msg);
        }
    }

}
