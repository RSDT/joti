package com.umbrella.jotiwa.map.area348.storage;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;
import com.umbrella.jotiwa.data.objects.area348.receivables.BaseInfo;
import com.umbrella.jotiwa.data.objects.area348.receivables.FotoOpdrachtInfo;
import com.umbrella.jotiwa.data.objects.area348.receivables.HunterInfo;
import com.umbrella.jotiwa.data.objects.area348.receivables.ScoutingGroepInfo;
import com.umbrella.jotiwa.data.objects.area348.receivables.VosInfo;
import com.umbrella.jotiwa.map.area348.MapManager;
import com.umbrella.jotiwa.map.area348.MapPartState;
import com.umbrella.jotiwa.map.area348.handling.HandlingResult;
import com.umbrella.jotiwa.map.area348.handling.HunterObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for storing map data.
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 22-9-2015
 */
public class MapStorage extends HashMap<String, StorageObject> implements Extractor {

    /**
     * Initializes a new instance of MapStorage.
     */
    public MapStorage() {
        storageHandler = new StorageHandler(this);
    }

    /**
     * Gets the associated StorageObject from a id.
     *
     * @param mapPartState The state the storage object is associated with.
     * @return
     */
    public StorageObject getAssociatedStorageObject(MapPartState mapPartState) {
        check(mapPartState.getAccessor());
        return this.get(mapPartState.getAccessor());
    }


    /**
     * Gets the last info of the storage object.
     * */
    public BaseInfo getLastInfo(StorageObject storageObject)
    {
        ArrayList<BaseInfo> info = storageObject.getAssociatedInfo();
        BaseInfo greatestInfo = new BaseInfo();
        for(int i = 0; i < info.size(); i++)
        {
            BaseInfo baseInfo = info.get(i);
            if(baseInfo.id > greatestInfo.id)
            {
                greatestInfo = baseInfo;
            }
        }
        return greatestInfo;
    }


    /**
     * Gets the value indicating if the info is the last.
     * */
    public boolean isLastInfo(MapPartState mapPartState, BaseInfo info)
    {
        BaseInfo last = getLastInfo(getAssociatedStorageObject(mapPartState));
        if(last.id == info.id) return true;
        return false;
    }

    /**
     * Gets a info from a id.
     *
     * @param storageObject The storage object that should be search through for the matching id.
     * @param id The id that identifies the info.
     * @return The id associated info. Returns a empty info if no match was found.
     */
    public BaseInfo getAssociatedInfoFromId(StorageObject storageObject, int id) {

        ArrayList<BaseInfo> info = storageObject.getAssociatedInfo();
        for (int i = 0; i < info.size(); i++) {
            if (info.get(i).id == id) return info.get(i);
        }
        return new BaseInfo();
    }

    /**
     * Finds a spefic info with it's id.
     *
     * @param mapPartState The state where the info should be located.
     * @param id The id that identifies the info.
     * @return The associated info.
     */
    public BaseInfo findInfo(MapPartState mapPartState, int id) {
        return this.getAssociatedInfoFromId(this.getAssociatedStorageObject(mapPartState), id);
    }


    /**
     * @param results
     */
    public void extract(HandlingResult[] results) {
        Thread thread = new Thread(new ExtractionTask(results));
        thread.start();
    }

    /**
     * Checks if the collection exists if not, create one with the given accessor.
     *
     * @param accessor
     */
    public void check(String accessor) {
        if (this.get(accessor) == null) this.put(accessor, new StorageObject());
    }

    /**
     * The storage handler.
     */
    private static StorageHandler storageHandler;

    /**
     * @return
     */
    public static StorageHandler getStorageHandler() {
        return storageHandler;
    }


    /**
     * Class that serves as a encapsulation for the extraction task.
     */
    class ExtractionTask implements Runnable {
        /**
         * @param results
         */
        public ExtractionTask(HandlingResult[] results) {
            this.results = results;
        }

        /**
         *
         */
        final HandlingResult[] results;

        /**
         *
         */
        @Override
        public void run() {
            ArrayList<MapPartState> newStates = new ArrayList<>();
            for (int i = 0; i < results.length; i++) {
                HandlingResult current = results[i];

                if (current.getMapPart() == MapPart.Hunters) {
                    HunterInfo[][] hunterInfos = (HunterInfo[][]) current.getObjects()[1];
                    int count = hunterInfos.length;
                    for (Map.Entry<String, HunterObject> entry : ((HashMap<String, HunterObject>) current.getObjects()[0]).entrySet()) {
                        check(entry.getKey());
                        StorageObject storageObjectHunter = get(entry.getKey());
                        storageObjectHunter.getMarkers().clear();
                        storageObjectHunter.getMarkers().add(entry.getValue().getMarker());
                        if (storageObjectHunter.getPolylines().size() > 0) {
                            PolylineOptions options = (PolylineOptions) storageObjectHunter.getPolylines().get(0);
                            options.addAll(entry.getValue().getPositions());
                        } else {
                            PolylineOptions pOptions = new PolylineOptions();
                            pOptions.addAll(entry.getValue().getPositions());
                            pOptions.color(Color.GRAY);
                            pOptions.width(5);
                            storageObjectHunter.getPolylines().add(pOptions);
                        }
                        storageObjectHunter.setAssociatedInfo(entry.getValue().getHunterInfo());

                        /**
                         * Add a state to the new state list, so for each hunter a state is created.
                         * */
                        newStates.add(new MapPartState(MapPart.Hunters, TeamPart.None, entry.getKey(), true, true, true));
                        count--;
                    }

                } else {
                    /**
                     * Gets the associated map part state accessor.
                     * */
                    String accessor = MapPartState.getAccesor(current.getMapPart(), current.getTeamPart());

                    /**
                     * Check if the collection exists if not create one.
                     * */
                    check(accessor);

                    /**
                     * Get the storage object associated with the accessor.
                     * */
                    StorageObject storageObject = get(accessor);

                    /**
                     * Create lists to hold data.
                     * */
                    ArrayList<BaseInfo> info = new ArrayList<>();
                    ArrayList<MarkerOptions> markers = new ArrayList<>();
                    ArrayList<PolylineOptions> polylines = new ArrayList<>();
                    ArrayList<CircleOptions> circles = new ArrayList<>();

                    /**
                     * Each map part is handled differently so use a switch.
                     * */
                    switch (current.getMapPart()) {
                        case Vossen:
                            markers.addAll((ArrayList<MarkerOptions>) current.getObjects()[0]);
                            polylines.add((PolylineOptions) current.getObjects()[1]);
                            circles.add((CircleOptions) current.getObjects()[2]);
                            info.addAll(Arrays.asList((VosInfo[]) current.getObjects()[3]));
                            break;

                        case ScoutingGroepen:
                            markers.addAll((ArrayList<MarkerOptions>) current.getObjects()[0]);
                            circles.addAll((ArrayList<CircleOptions>) current.getObjects()[1]);
                            info.addAll(Arrays.asList((ScoutingGroepInfo[]) current.getObjects()[2]));
                            break;

                        case FotoOpdrachten:
                            markers.addAll((ArrayList<MarkerOptions>) current.getObjects()[0]);
                            info.addAll(Arrays.asList((FotoOpdrachtInfo[]) current.getObjects()[1]));
                            break;
                    }

                    /**
                     * Set the markers, polylines and circles of the storage object.
                     * */
                    storageObject.setMarkers(markers);
                    storageObject.setPolylines(polylines);
                    storageObject.setCircles(circles);
                    storageObject.setAssociatedInfo(info);
                }
            }

            /**
             * Signal the listener that new data is available.
             * */
            Message message = new Message();
            message.obj = newStates;
            MapManager.getMapManagerHandler().sendMessage(message);
        }

    }

    /**
     * Static handler to prevent memory leaking.
     *
     * @see @link {http://stackoverflow.com/questions/11407943/this-handler-class-should-be-static-or-leaks-might-occur-incominghandler}
     */
    private static class StorageHandler extends Handler {
        /**
         * Weak reference so that no memory leak occur.
         */
        WeakReference<Extractor> extractor;

        /**
         * Constructor for the handler.
         *
         * @param extractor
         */
        StorageHandler(Extractor extractor) {
            this.extractor = new WeakReference<Extractor>(extractor);
        }

        /**
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case StorageHandlerMessageType.EXTRACT_DATA:
                    Extractor extractorRef = extractor.get();
                    extractorRef.extract((HandlingResult[]) msg.obj);
                    break;
            }
            super.handleMessage(msg);
        }
    }

}
