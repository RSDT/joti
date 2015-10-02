package com.umbrella.jotiwa.map.area348;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.umbrella.jotiwa.MyApp;
import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;
import com.umbrella.jotiwa.map.area348.storage.MapStorage;

import static com.umbrella.jotiwa.communication.enumeration.area348.MapPart.*;

/**
 * Created by stesi on 25-9-2015.
 * Class for controlling the flow of updating, handling and readding.
 */
public class MapPartState {




    public MapPartState(MapPart mapPart, TeamPart teamPart)
    {
        this.mapPart = mapPart;
        this.teamPart = teamPart;
    }

    public MapPartState(MapPart mapPart, TeamPart teamPart, boolean show, boolean update)
    {
        this.mapPart = mapPart;
        this.teamPart = teamPart;
        this.show = show;
        this.update = update;
    }

    private MapPart mapPart;

    private TeamPart teamPart;

    private boolean pending = false;

    private boolean isOnMap = false;

    private boolean show = false;

    private boolean update = false;

    private boolean hasLocalData = false;

    public MapPart getMapPart() {
        return mapPart;
    }

    public TeamPart getTeamPart() {
        return teamPart;
    }

    public boolean isPending() {
        return pending;
    }

    public boolean isOnMap() {
        return isOnMap;
    }

    public boolean getShow() {
        SharedPreferences sharedpeferences = PreferenceManager.getDefaultSharedPreferences(MyApp.getContext());
        switch (getMapPart()){
            case Vossen:
                return sharedpeferences.getBoolean("pref_" + teamPart.name().toLowerCase().toLowerCase(), show);
            case ScoutingGroepen:
                return sharedpeferences.getBoolean("pref_sc", show);
            case Hunters:
                return sharedpeferences.getBoolean("pref_tail", show);
            case FotoOpdrachten:
                return sharedpeferences.getBoolean("pref_foto", show);
            case Me:
                return sharedpeferences.getBoolean("pref_me", show);
            default:
                return show;
    }
    }

    public boolean isUpdate() {
        return update;
    }

    public boolean hasLocalData(MapStorage storage)
    {
        if(storage.getAssociatedHandlingResult(this) != null)
        {
            return true;
        }
        return false;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }
}
