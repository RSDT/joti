package com.umbrella.jotiwa.map.area348;


import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;
import com.umbrella.jotiwa.map.area348.storage.MapStorage;

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
        return show;
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
