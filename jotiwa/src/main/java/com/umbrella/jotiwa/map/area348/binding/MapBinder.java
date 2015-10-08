package com.umbrella.jotiwa.map.area348.binding;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.umbrella.jotiwa.JotiApp;
import com.umbrella.jotiwa.map.area348.MapPartState;
import com.umbrella.jotiwa.map.area348.storage.StorageObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by stesi on 25-9-2015.
 */
public class MapBinder extends HashMap<String, MapBindObject> {

    /**
     * @param gMap
     */
    public MapBinder(GoogleMap gMap) {
        this.gMap = gMap;
    }

    private GoogleMap gMap;


    /**
     * @param mapPartState
     * @return
     */
    public MapBindObject getAssociatedMapBindObject(MapPartState mapPartState) {
        check(mapPartState.getAccessor());
        return this.get(mapPartState.getAccessor());
    }

    /**
     * @param mapPartState
     * @param id
     * @return
     */
    public Marker findMarker(MapPartState mapPartState, int id) {
        MapBindObject mapBindObject = this.getAssociatedMapBindObject(mapPartState);
        ArrayList<Marker> markers = mapBindObject.getMarkers();
        for (int i = 0; i < markers.size(); i++) {
            String[] typeCodes = markers.get(i).getTitle().split(";");
            switch (mapPartState.getMapPart()) {
                case Vossen:
                    if (Integer.parseInt(typeCodes[2]) == id) return markers.get(i);
                    break;
                case Hunters:

                    break;
            }

            /**
             * If the type code is equal to the map part. Reduant, all the marker in this binding object should be of one state.
             * */
            if (typeCodes[0] == mapPartState.getMapPart().getValue()) {

            }
        }
        return null;
    }


    /**
     * Adds a state's data to the map.
     * @param mapPartState The state to add to the map.
     * @param storageObject The storage object that contains the data.
     * @param options The options for the adding.
     */
    public void add(MapPartState mapPartState, StorageObject storageObject, MapBinderAddOptions options) {
        if (storageObject == null) {
            JotiApp.debug("ERROR storage object = null in MapBinder.add");
            storageObject = new StorageObject();
        }

        if(!mapPartState.isAddable()) return;

        if(!mapPartState.hasNewData()) return;

        String accessor = mapPartState.getAccessor();
        check(accessor);
        MapBindObject bindObject = this.get(accessor);

        if(mapPartState.isOnMap())
        {
            bindObject.remove();
        }

        if (options == MapBinderAddOptions.MAP_BINDER_ADD_OPTIONS_CLEAR) {
            bindObject.getMarkers().clear();
            bindObject.getPolylines().clear();
            bindObject.getCircles().clear();
        }

        ArrayList<MarkerOptions> markers = storageObject.getMarkers();
        /**
         * Loop through data and add each marker to the map and the bind object.
         * */
        for (int m = 0; m < markers.size(); m++) {
            bindObject.getMarkers().add(gMap.addMarker(markers.get(m)));
        }

        ArrayList<PolylineOptions> polylines = storageObject.getPolylines();
        /**
         * Loop through data and add each polyline to the map and the bind object.
         * */
        for (int l = 0; l < polylines.size(); l++) {
            bindObject.getPolylines().add(gMap.addPolyline(polylines.get(l)));
        }

        ArrayList<CircleOptions> circles = storageObject.getCircles();
        /**
         * Loop through data and add each circle to the map and the bind object.
         * */
        for (int c = 0; c < circles.size(); c++) {
            bindObject.getCircles().add(gMap.addCircle(circles.get(c)));
        }

        /**
         * If the state should not be shown
         * */
        if(!mapPartState.getShow())
        {
            bindObject.setVisiblty(false);
        }

        mapPartState.setIsOnMap(true);
    }

    /**
     * Enumeration for add options.
     */
    public enum MapBinderAddOptions {
        /**
         * Clear the old items.
         */
        MAP_BINDER_ADD_OPTIONS_CLEAR,

        /**
         * Hold the old items.
         */
        MAP_BINDER_ADD_OPTIONS_HOLD
    }

    private void check(String accessor) {
        if (this.get(accessor) == null)
            this.put(accessor, new MapBindObject());
    }

}
