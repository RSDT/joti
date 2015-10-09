package com.umbrella.jotiwa;

import android.location.Location;

/**
 * Created by mattijn on 9-10-15.
 */
public interface RealTimeTracker {
    void onNewLocation(Location location);
}
