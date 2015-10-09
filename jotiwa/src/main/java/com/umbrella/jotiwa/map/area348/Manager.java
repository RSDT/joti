package com.umbrella.jotiwa.map.area348;

import android.location.Location;

import java.util.ArrayList;

/**
 * Created by stesi on 9-10-2015.
 */
public interface Manager {

    void onNewDataAvailable(ArrayList<MapPartState> newStates);

    void onLocationChanged(Location location);
}
