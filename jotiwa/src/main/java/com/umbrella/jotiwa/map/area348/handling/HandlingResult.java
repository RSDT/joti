package com.umbrella.jotiwa.map.area348.handling;

import android.os.Handler;

import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;

/**
 * Created by stesi on 22-9-2015.
 */
public class HandlingResult {

    private Handler handler;

    private MapPart mapPart;

    private TeamPart teamPart;

    private Object[] objects;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Handler getHandler() {
        return handler;
    }

    public MapPart getMapPart() {
        return mapPart;
    }

    public Object[] getObjects() {
        return objects;
    }

    public TeamPart getTeamPart() {
        return teamPart;
    }

    public void setMapPart(MapPart mapPart) {
        this.mapPart = mapPart;
    }

    public void setObjects(Object[] objects) {
        this.objects = objects;
    }

    public void setTeamPart(TeamPart teamPart) {
        this.teamPart = teamPart;
    }
}
