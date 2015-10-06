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

    /**
     * @param handler
     */
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    /**
     * @return
     */
    public Handler getHandler() {
        return handler;
    }

    /**
     * @return
     */
    public MapPart getMapPart() {
        return mapPart;
    }

    /**
     * @return
     */
    public Object[] getObjects() {
        return objects;
    }

    /**
     * @return
     */
    public TeamPart getTeamPart() {
        return teamPart;
    }

    /**
     * @param mapPart
     */
    public void setMapPart(MapPart mapPart) {
        this.mapPart = mapPart;
    }

    /**
     * @param objects
     */
    public void setObjects(Object[] objects) {
        this.objects = objects;
    }

    /**
     * @param teamPart
     */
    public void setTeamPart(TeamPart teamPart) {
        this.teamPart = teamPart;
    }
}
