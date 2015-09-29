package com.umbrella.jotiwa.map.area348;

import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;

/**
 * Created by stesi on 29-9-2015.
 */
public class HunterMapPartState extends MapPartState {

    public HunterMapPartState(String[] accessors)
    {
        super(MapPart.Hunters, TeamPart.None);
        this.accessors = accessors;

    }

    public HunterMapPartState(boolean show, boolean update, String[] accessors)
    {
        super(MapPart.Hunters, TeamPart.None, show, update);
        this.accessors = accessors;
    }

    private String[] accessors;

    private boolean useAccesors = true;

    public String[] getAccessors() {
        return accessors;
    }

    public void setAccessors(String[] accessors) {
        this.accessors = accessors;
    }
}
