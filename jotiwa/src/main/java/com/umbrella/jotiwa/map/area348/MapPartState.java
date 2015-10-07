package com.umbrella.jotiwa.map.area348;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.umbrella.jotiwa.JotiApp;
import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;

import java.io.Serializable;

/**
 * Class for controlling the flow of updating, handling and reading.
 * This class servers as a holder for the control values.
 *
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 25-9-2015
 */
public class MapPartState implements Serializable {

    /**
     * Initializes a new instance of MapPartState.
     *
     * @param mapPart  The MapPart of the state.
     * @param teamPart The (optional) TeamPart of the state.
     */
    public MapPartState(MapPart mapPart, TeamPart teamPart) {
        this.mapPart = mapPart;
        this.teamPart = teamPart;
        this.accessor = getAccesor(mapPart, teamPart);
    }

    /**
     * Initializes a new instance of MapPartState.
     *
     * @param mapPart  The MapPart of the state.
     * @param teamPart The (optional) TeamPart of the state.
     * @param show     The value indicating if the state should be shown.
     * @param update   The value indicating if the state should be updated.
     */
    public MapPartState(MapPart mapPart, TeamPart teamPart, boolean show, boolean update) {
        this.mapPart = mapPart;
        this.teamPart = teamPart;
        this.show = show;
        this.update = update;

        this.accessor = getAccesor(mapPart, teamPart);
    }

    /**
     * Initializes a new instance of MapPartState.
     *
     * @param mapPart  The MapPart of the state.
     * @param teamPart The (optional) TeamPart of the state.
     * @param accessor The accessor that should be used to access the collections.
     * @param show     The value indicating if the state should be shown.
     * @param update   The value indicating if the state should be updated.
     */
    public MapPartState(MapPart mapPart, TeamPart teamPart, String accessor, boolean show, boolean update, boolean pending) {
        this.mapPart = mapPart;
        this.teamPart = teamPart;
        this.show = show;
        this.update = update;
        this.pending = pending;

        this.accessor = accessor;
    }


    /**
     * Get the accessor of a map part.
     * Defines a standard accessor layout.
     *
     * @param mapPart
     * @param teamPart
     * @return
     */
    public static String getAccesor(MapPart mapPart, TeamPart teamPart) {
        if (mapPart == MapPart.Vossen) {
            return teamPart.getSubChar();
        } else {
            return mapPart.getValue();
        }
    }


    //region values
    /**
     * The MapPart the state represents.
     */
    private final MapPart mapPart;

    /**
     * The (optional) TeamPart the state represents.
     */
    private final TeamPart teamPart;

    /**
     * The collection accessors used to access the storage collections.
     */
    private final String accessor;

    /**
     * Value indicating if there is a pending interaction for this state.
     */
    private boolean pending = false;

    /**
     * Value indicating if the state is present on the map.
     */
    private boolean isOnMap = false;

    /**
     * Value indicating if the state should be shown.
     * NOTE: On the map and visible are different things, the state can be on the map but only not visible.
     */
    private boolean show = false;

    /**
     * Value indicating if the state should be updated.
     */
    private boolean update = false;

    /**
     * Value indicating if the state has local data.
     */
    private boolean hasLocalData = false;

    /**
     * Value indicating if the state has new data availiabe at the storage.
     * */
    private boolean hasNewData = false;

    /**
     * Value indicating if the state is addable to the map.
     * */
    private boolean addable = true;


    //endregion

    //region fields

    /**
     * @return
     */
    //region get
    public MapPart getMapPart() {
        return mapPart;
    }

    /**
     * @return
     */
    public TeamPart getTeamPart() {
        return teamPart;
    }

    /**
     * @return
     */
    public String getAccessor() {
        return accessor;
    }

    /**
     * @return
     */
    public boolean isPending() {
        return pending;
    }

    /**
     * @return Value indicating if the state is on the map, TRUE: is on the map, FALSE: is not on the map.
     */
    public boolean isOnMap() {
        return isOnMap;
    }


    /**
     * @return
     */
    public boolean getShow() {
        SharedPreferences sharedpeferences = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext());
        switch (getMapPart()) {
            case Vossen:
                if (teamPart == TeamPart.All || teamPart == TeamPart.None) return show;
                String key = "pref_vos_" + teamPart.getSubChar();
                return sharedpeferences.getBoolean(key, false);
            case ScoutingGroepen:
                return sharedpeferences.getBoolean("pref_sc", show);
            case Hunters:
                return sharedpeferences.getBoolean("pref_hunter", show);
            case FotoOpdrachten:
                return sharedpeferences.getBoolean("pref_foto", show);
            case Me:
                return sharedpeferences.getBoolean("pref_me", show);
            case All:
                return show;
            default:
                return show;
        }
    }

    /**
     * @return
     */
    public boolean update() {
        return update;
    }

    /**
     * @return
     */
    public boolean hasLocalData() {
        return hasLocalData;
    }

    /**
     * @return Value indicating if the state has new data avaibible at the storage.
     * */
    public boolean hasNewData() {
        return hasNewData;
    }

    /**
     * @return Value indicating if the state is addable to the map.
     * */
    public boolean isAddable() {
        return addable;
    }

    //endregion

    //region set

    /**
     * @param pending
     */
    public void setPending(boolean pending) {
        this.pending = pending;
    }


    public void setIsOnMap(boolean isOnMap) {
        this.isOnMap = isOnMap;
    }

    /**
     * @param hasLocalData
     */
    public void setHasLocalData(boolean hasLocalData) {
        this.hasLocalData = hasLocalData;
    }

    public void setHasNewData(boolean hasNewData) {
        this.hasNewData = hasNewData;
    }

    public void setAddable(boolean addable) {
        this.addable = addable;
    }

    //endregion

    //endregion


    /**
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MapPartState))
            return false;

        MapPartState mapPartState = (MapPartState) o;
        if (this.getMapPart() == mapPartState.getMapPart() && this.getTeamPart() == mapPartState.getTeamPart())
            return true;

        return false;
    }

    /**
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.mapPart != null ? this.mapPart.hashCode() : 0);
        hash = 53 * hash + (this.teamPart != null ? this.teamPart.hashCode() : 0);
        return hash;
    }
}
