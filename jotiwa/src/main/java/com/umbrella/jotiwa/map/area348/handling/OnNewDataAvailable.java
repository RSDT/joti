package com.umbrella.jotiwa.map.area348.handling;

import android.location.Location;

import com.umbrella.jotiwa.map.area348.MapPartState;

import java.util.ArrayList;

/**
 * Created by stesi on 23-9-2015.
 */
public interface OnNewDataAvailable {
    /**
     * @param newStates
     */
    void onNewDataAvailable(ArrayList<MapPartState> newStates);

}
