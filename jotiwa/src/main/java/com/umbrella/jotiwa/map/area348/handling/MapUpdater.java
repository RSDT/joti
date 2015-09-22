package com.umbrella.jotiwa.map.area348.handling;

import com.umbrella.jotiwa.communication.LinkBuilder;
import com.umbrella.jotiwa.communication.enumeration.area348.*;
import com.umbrella.jotiwa.communication.interaction.InteractionManager;
import com.umbrella.jotiwa.communication.interaction.InteractionRequest;

/**
 * Created by stesi on 22-9-2015.
 * Class for creating the request and queing it.
 */
public class MapUpdater extends InteractionManager {

    public void update(MapPart mapPart)
    {
        update(mapPart, TeamPart.None);
    }

    /**
     * Updates a certain map part.
     * TODO: Interaction is now instant, is this smart?
     * TODO: Implement hunter interaction.
     * */
    public void update(MapPart mapPart, TeamPart teamPart)
    {
        /**
         * Make sure the root of the LinkBuilder is set to the Area348 one.
         * */
        LinkBuilder.setRoot(Area348_API.root);
        switch(mapPart)
        {
            case All:
                this.update(MapPart.Vossen, teamPart);
                this.update(MapPart.Hunters, TeamPart.None);
                this.update(MapPart.ScoutingGroepen, TeamPart.None);
                this.update(MapPart.FotoOpdrachten, TeamPart.None);
                break;
            case Vossen:
                if(teamPart == TeamPart.All) {
                    String[] teamChars = new String[] {"a", "b", "c", "d", "e", "f", "x"};
                    for(int i = 0; i < teamChars.length; i++)
                    {
                        super.queue(new InteractionRequest(LinkBuilder.build(new String[] { mapPart.getValue(), teamPart.getSubChar(), Keywords.All } ), null));
                    }
                    super.interact();
                    return;
                }
                super.queue(new InteractionRequest(LinkBuilder.build(new String[] { mapPart.getValue(), teamPart.getSubChar(), Keywords.All } ), null));
                break;
            case Hunters:

                break;
            case ScoutingGroepen:
                super.queue(new InteractionRequest(LinkBuilder.build(new String[] { mapPart.getValue(), Keywords.All }), null));
                break;
            case FotoOpdrachten:
                super.queue(new InteractionRequest(LinkBuilder.build(new String[] { mapPart.getValue(), Keywords.All}), null));
                break;
        }
        super.interact();
    }
}
