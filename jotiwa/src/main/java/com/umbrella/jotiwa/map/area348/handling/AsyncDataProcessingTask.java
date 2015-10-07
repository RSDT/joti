package com.umbrella.jotiwa.map.area348.handling;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.umbrella.jotiwa.communication.enumeration.area348.Area348_Linker;
import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;
import com.umbrella.jotiwa.communication.interaction.InteractionResult;
import com.umbrella.jotiwa.data.objects.area348.receivables.FotoOpdrachtInfo;
import com.umbrella.jotiwa.data.objects.area348.receivables.HunterInfo;
import com.umbrella.jotiwa.data.objects.area348.receivables.ScoutingGroepInfo;
import com.umbrella.jotiwa.data.objects.area348.receivables.VosInfo;
import com.umbrella.jotiwa.map.area348.storage.MapStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class AsyncDataProcessingTask extends AsyncTask<InteractionResult, Integer, HandlingResult[]> {

    /**
     * @param params
     * @return
     */
    @Override
    protected HandlingResult[] doInBackground(InteractionResult... params) {
        HandlingResult[] results = new HandlingResult[params.length];
        for (int i = 0; i < params.length; i++) {
            if (params[i].getRequest().getHandler() != null) {
                switch (Area348_Linker.parseMapPart(params[i].getRequest().getUrl())) {
                    case Vossen:
                        results[i] = handleVossen(params[i]);
                        break;
                    case Hunters:
                        results[i] = handleHunters(params[i]);
                        break;
                    case ScoutingGroepen:
                        results[i] = handleScoutingGroepen(params[i]);
                        break;
                    case FotoOpdrachten:
                        results[i] = handleFotoOpdrachten(params[i]);
                        break;
                }
            }
        }
        return results;
    }

    /**
     * @param iResult
     * @return
     */
    private HandlingResult handleVossen(InteractionResult iResult) {
        /**
         * Deserialize the json into a VosInfo array.
         * */
        VosInfo[] vossen = VosInfo.fromJsonArray(iResult.getReceivedData());

        /**
         * Create a result and set it.
         * */
        HandlingResult result = new HandlingResult();
        result.setHandler(iResult.getHandler());
        result.setMapPart(MapPart.Vossen);
        result.setTeamPart(TeamPart.parse(vossen[0].team));

        /**
         * Gets the collection associated with the team value.
         * */
        List<MarkerOptions> markers = new ArrayList<>();

        /**
         * Load the icon once.
         * */
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromAsset("vos_pointers/dots/" + vossen[0].team + "-Vossen-15x15.png");

        /**
         * Creates and configures a polyine, to show the path of the vos.
         * */
        PolylineOptions pOptions = new PolylineOptions();
        pOptions.width(5);
        pOptions.color(TeamPart.getAssociatedColor(result.getTeamPart()));

        /**
         * Setup the preset circle.
         * */
        CircleOptions cOptions = new CircleOptions();
        cOptions.fillColor(TeamPart.getAssociatedAlphaColor(result.getTeamPart(), 128));
        cOptions.strokeColor(Color.BLACK);
        cOptions.strokeWidth(1);
        cOptions.radius(100);

        /**
         * Loop trough each vos info and add it to the map.
         * */
        for (int i = 0; i < vossen.length; i++) {
            /**
             * Setup marker with the current VosInfo data.
             * */
            MarkerOptions mOptions = new MarkerOptions();
            mOptions.anchor(0.5f, 0.5f);
            mOptions.position(new LatLng(vossen[i].latitude, vossen[i].longitude));
            mOptions.title("vos;" + vossen[0].team + ";" + ((Integer) vossen[i].id).toString());

            /**
             * Add a point to the points of the vos line.
             * */
            pOptions.add(new LatLng(vossen[i].latitude, vossen[i].longitude));

            /**
             * Checks if this is the first vos, if so use different marker.
             * */
            if (i == 0) {
                mOptions.icon(BitmapDescriptorFactory.fromAsset("vos_pointers/markers/" + vossen[0].team + "-Vossen-30x30.png"));

                /**
                 * Sets the center of the circle to the latest vos location.
                 * */
                cOptions.center(new LatLng(vossen[i].latitude, vossen[i].longitude));

            } else {
                mOptions.icon(descriptor);
            }

            /**
             * Add the marker to the marker collection.
             * */
            markers.add(mOptions);
        }
        result.setObjects(new Object[]{markers, pOptions, cOptions, vossen});
        return result;
    }


    /**
     * TODO:The older locations, only have to be retrieved once. They are stored offline. Only the newest location should be retrieved.
     *
     * @param iResult
     * @return
     */
    private HandlingResult handleHunters(InteractionResult iResult) {
        HunterInfo[][] hunterInfos = HunterInfo.formJsonArrayOfArray(iResult.getReceivedData());

        /**
         * Create result and configure it.
         * */
        HandlingResult result = new HandlingResult();
        result.setMapPart(MapPart.Hunters);
        result.setTeamPart(TeamPart.None);
        result.setHandler(iResult.getHandler());

        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromAsset("auto.png");

        HashMap<String, HunterObject> entries = new HashMap<>();

        for (int h = 0; h < hunterInfos.length; h++) {
            entries.put(hunterInfos[h][0].gebruiker, new HunterObject());
            HunterObject current = entries.get(hunterInfos[h][0].gebruiker);
            HunterInfo last = hunterInfos[h][0];
            for (int i = 0; i < hunterInfos[h].length; i++) {
                /**
                 * Checks if the current info is the last, if it is the info is the newest.
                 * TRUE: Creates
                 * */
                if (hunterInfos[h][i].id > last.id) {
                    last = hunterInfos[h][0];
                }
                current.getPositions().add(new LatLng(hunterInfos[h][i].latitude, hunterInfos[h][i].longitude));
            }
            MarkerOptions mOptions = new MarkerOptions();
            mOptions.title("hunter" + ";" + last.gebruiker + ";" + ((Integer) last.id).toString());
            mOptions.position(new LatLng(last.latitude, last.longitude));
            mOptions.icon(descriptor);
            current.setMarker(mOptions);
            current.getHunterInfo().addAll(Arrays.asList(hunterInfos[h]));
        }
        result.setObjects(new Object[]{entries, hunterInfos});
        return result;
    }

    /**
     * @param iResult
     * @return
     */
    private HandlingResult handleScoutingGroepen(InteractionResult iResult) {
        /**
         * Deserializes the json into a array of ScoutingGroepInfo.
         * */
        ScoutingGroepInfo[] groepen = ScoutingGroepInfo.fromJsonArray(iResult.getReceivedData());

        HandlingResult result = new HandlingResult();
        result.setMapPart(MapPart.ScoutingGroepen);
        result.setTeamPart(TeamPart.All);
        result.setHandler(iResult.getHandler());

        List<MarkerOptions> markers = new ArrayList<MarkerOptions>();

        List<CircleOptions> circles = new ArrayList<CircleOptions>();

        /**
         * Loads the icon once.
         * */
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromAsset("scouting_groep_formats/scouting_groep_icon_50x36.png");

        /**
         * Loops through each ScoutingGroepInfo, adding a marker and circle for each one.
         * */
        for (int i = 0; i < groepen.length; i++) {

            /**
             * Setups the marker.
             * */
            MarkerOptions mOptions = new MarkerOptions();
            mOptions.position(new LatLng(groepen[i].latitude, groepen[i].longitude));
            mOptions.title("sc;" + ((Integer) groepen[i].id).toString() + ";" + groepen[i].deelgebied.toLowerCase());
            mOptions.anchor(0.5f, 0.5f);
            mOptions.icon(descriptor);

            /**
             * Setups the preset circle.
             * */
            CircleOptions cOptions = new CircleOptions();
            cOptions.fillColor(TeamPart.getAssociatedAlphaColor(TeamPart.parse(groepen[i].deelgebied.toLowerCase()), 10));
            cOptions.radius(500);
            cOptions.strokeColor(Color.BLACK);
            cOptions.strokeWidth(2);
            cOptions.center(new LatLng(groepen[i].latitude, groepen[i].longitude));


            /**
             * Add the circle and the marker to the offline collection and to the map.
             * */
            markers.add(mOptions);
            circles.add(cOptions);
        }

        result.setObjects(new Object[]{markers, circles, groepen});
        return result;
    }

    /**
     * @param iResult
     * @return
     */
    private HandlingResult handleFotoOpdrachten(InteractionResult iResult) {
        FotoOpdrachtInfo[] fotoOpdrachten = FotoOpdrachtInfo.fromJsonArray(iResult.getReceivedData());

        HandlingResult result = new HandlingResult();
        result.setMapPart(MapPart.FotoOpdrachten);
        result.setTeamPart(TeamPart.None);
        result.setHandler(iResult.getHandler());

        List<MarkerOptions> markers = new ArrayList<MarkerOptions>();

        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromAsset("foto_indicators/camera_20x20.png");

        BitmapDescriptor descriptorDone = BitmapDescriptorFactory.fromAsset("foto_indicators/camera_20x20_klaar.png");
        /**
         * Loops through each FotoOpdrachtInfo.
         * */
        for (int i = 0; i < fotoOpdrachten.length; i++) {
            MarkerOptions mOptions = new MarkerOptions();
            mOptions.icon(descriptor);
            mOptions.anchor(0.5f, 0.5f);
            if (fotoOpdrachten[i].klaar == 1) {
                mOptions.icon(descriptorDone);
            }
            mOptions.position(new LatLng(fotoOpdrachten[i].latitude, fotoOpdrachten[i].longitude));
            mOptions.title("foto;" + ((Integer) fotoOpdrachten[i].id).toString());
            markers.add(mOptions);
        }
        result.setObjects(new Object[]{markers, fotoOpdrachten});
        return result;
    }

    /**
     * @param handlingResults
     */
    @Override
    protected void onPostExecute(HandlingResult handlingResults[]) {
        Message message = new Message();
        message.obj = handlingResults;
        ((Handler) MapStorage.getStorageHandler()).sendMessage(message);
    }
}

