package com.umbrella.jotiwa.map.area348.handling;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Message;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.umbrella.jotiwa.communication.enumeration.area348.Area348_Linker;
import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;
import com.umbrella.jotiwa.communication.interaction.InteractionResult;
import com.umbrella.jotiwa.data.objects.area348.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class AsyncDataProcessingTask extends AsyncTask<InteractionResult, Integer, HandlingResult[]> {

    @Override
    protected HandlingResult[] doInBackground(InteractionResult... params)
    {
        HandlingResult[] results = new HandlingResult[params.length];
        for(int i = 0; i < params.length; i++)
        {
            if(params[i].getRequest().getHandler() != null)
            {
                switch(Area348_Linker.parseMapPart(params[i].getRequest().getUrl()))
                {

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

    private HandlingResult handleVossen(InteractionResult iResult)
    {
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
        List<MarkerOptions> markers = new ArrayList<MarkerOptions>();

        /**
         * Load the icon once.
         * */
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromAsset("vos_pointers/dots/" + vossen[0].team + "-Vossen-15x15.png");

        /**
         * Array to hold the points of the line.
         * */
        List<LatLng> points = new ArrayList<LatLng>();

        /**
         * Loop trough each vos info and add it to the map.
         * */
        for(int i = 0; i < vossen.length; i++)
        {
            /**
             * Setup marker with the current VosInfo data.
             * */
            MarkerOptions mOptions = new MarkerOptions();
            mOptions.anchor(0.5f, 0.5f);
            mOptions.position(new LatLng(vossen[i].latitude, vossen[i].longitude));
            mOptions.title("vos");
            mOptions.snippet(vossen[i].datetime);

            /**
             * Add a point to the points of the vos line.
             * */
            points.add(new LatLng(vossen[i].latitude, vossen[i].longitude));

            /**
             * Checks if this is the first vos, if so use different marker.
             * */
            if(i == 0)
            {
                mOptions.icon(BitmapDescriptorFactory.fromAsset("vos_pointers/markers/" + vossen[0].team + "-Vossen-30x30.png"));
            } else {
                mOptions.icon(descriptor);
            }

            /**
             * Add the marker to the marker collection.
             * */
            markers.add(mOptions);
        }
        result.setObjects(new Object[] { markers, points });
        return result;
    }

    /**
     *
     * TODO:The older locations, only have to be retrieved once. They are stored offline. Only the newest location should be retrieved.
     * */
    private HandlingResult handleHunters(InteractionResult iResult)
    {
        HunterInfo[][] hunterInfos = HunterInfo.formJsonArrayOfArray(iResult.getReceivedData());

        /**
         * Create result and configure it.
         * */
        HandlingResult result = new HandlingResult();
        result.setMapPart(MapPart.Hunters);
        result.setTeamPart(TeamPart.None);
        result.setHandler(iResult.getHandler());

        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromAsset("auto.png");

        Map<String, MarkerOptions> markers = new HashMap<>();

        Map<String, List<LatLng>> locations = new HashMap<>();

        for(int h = 0; h < hunterInfos.length; h++)
        {
            ArrayList<LatLng> positions = (ArrayList<LatLng>)locations.put(hunterInfos[h][0].gebruiker, new ArrayList<LatLng>());
            for(int i = 0; i < hunterInfos.length; i++)
            {
                /**
                 * Checks if the current info is the last, if it is the info is the newest.
                 * TRUE: Creates
                 * */
                if(i == hunterInfos.length)
                {
                    MarkerOptions mOptions = new MarkerOptions();
                    mOptions.title(hunterInfos[h][i].gebruiker);
                    mOptions.snippet(hunterInfos[h][i].datetime);
                    mOptions.position(new LatLng(hunterInfos[h][i].latitude, hunterInfos[h][i].longitude));
                    mOptions.icon(descriptor);
                    markers.put(hunterInfos[h][0].gebruiker, mOptions);
                }
                positions.add(new LatLng(hunterInfos[h][i].latitude, hunterInfos[h][i].longitude));
            }
        }
        result.setObjects(new Object[] { markers, locations });
        return result;
    }

    private HandlingResult handleScoutingGroepen(InteractionResult iResult)
    {
        /**
         * Deserializes the json into a array of ScoutingGroepInfo.
         * */
        ScoutingGroepInfo[] groepen = ScoutingGroepInfo.fromJsonArray(iResult.getReceivedData());

        HandlingResult result = new HandlingResult();
        result.setMapPart(MapPart.ScoutingGroepen);
        result.setTeamPart(TeamPart.None);
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
        for(int i = 0; i < groepen.length; i++) {

            /**
             * Setups the marker.
             * */
            MarkerOptions mOptions = new MarkerOptions();
            mOptions.position(new LatLng(groepen[i].latitude, groepen[i].longitude));
            mOptions.title(groepen[i].naam);
            mOptions.snippet(groepen[i].adres);
            mOptions.anchor(0.5f, 0.5f);
            mOptions.icon(descriptor);

            /**
             * Setups the preset circle.
             * */
            CircleOptions cOptions = new CircleOptions();
            cOptions.fillColor(Color.argb(128, 255, 153, 0));
            cOptions.radius(300);
            cOptions.strokeColor(Color.BLACK);
            cOptions.strokeWidth(1);
            cOptions.center(new LatLng(groepen[i].latitude, groepen[i].longitude));


            /**
             * Add the circle and the marker to the offline collection and to the map.
             * */
            markers.add(mOptions);
            circles.add(cOptions);
        }

        result.setObjects(new Object[] { markers, circles });
        return result;
    }

    private HandlingResult handleFotoOpdrachten(InteractionResult iResult)
    {
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
        for (int i = 0; i < fotoOpdrachten.length; i++)
        {
            MarkerOptions mOptions = new MarkerOptions();
            mOptions.icon(descriptor);
            mOptions.anchor(0.5f, 0.5f);
            if(fotoOpdrachten[i].klaar == 1) {
                mOptions.icon(descriptorDone); }
            mOptions.position(new LatLng(fotoOpdrachten[i].latitude, fotoOpdrachten[i].longitude));
            mOptions.title(fotoOpdrachten[i].naam);
            mOptions.snippet(fotoOpdrachten[i].extra);

            markers.add(mOptions);
        }
        result.setObjects(new Object[] { markers });
        return result;
    }

    @Override
    protected void onPostExecute(HandlingResult handlingResults[])
    {
        Message message = new Message();
        message.obj = handlingResults;
        handlingResults[0].getHandler().sendMessage(message);
    }
}

