package com.umbrella.jotiwa.communication.interaction.area348;

import com.umbrella.jotiwa.communication.LinkBuilder;
import com.umbrella.jotiwa.communication.enumeration.area348.*;
import com.umbrella.jotiwa.communication.interaction.InteractionManager;
import com.umbrella.jotiwa.communication.interaction.InteractionRequest;
import com.umbrella.jotiwa.communication.interaction.InteractionResult;
import com.umbrella.jotiwa.communication.interaction.InteractionResultState;
import com.umbrella.jotiwa.communication.interaction.OnRequestTaskCompleted;
import com.umbrella.jotiwa.map.area348.handling.AsyncDataProcessingTask;
import com.umbrella.jotiwa.map.area348.storage.MapStorage;

import java.util.ArrayList;

/**
 * Created by stesi on 22-9-2015.
 * Class for creating the request and queing it.
 */
public class DataUpdater extends InteractionManager implements OnRequestTaskCompleted {

    public DataUpdater()
    {
        super();
        setOnRequestTaskCompletedListener(this);
    }


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
                        super.queue(new InteractionRequest(LinkBuilder.build(new String[] { mapPart.getValue(), teamChars[i], Keywords.All } ), null));
                    }
                    return;
                }
                super.queue(new InteractionRequest(LinkBuilder.build(new String[] { mapPart.getValue(), teamPart.getSubChar(), Keywords.All } ), null));
                break;
            case Hunters:
                super.queue(new InteractionRequest(LinkBuilder.build(new String[] { mapPart.getValue(), Keywords.All }), null));
                break;
            case ScoutingGroepen:
                super.queue(new InteractionRequest(LinkBuilder.build(new String[] { mapPart.getValue(), Keywords.All }), null));
                break;
            case FotoOpdrachten:
                super.queue(new InteractionRequest(LinkBuilder.build(new String[] { mapPart.getValue(), Keywords.All}), null));
                break;
        }
    }

    @Override
    public void onRequestTaskCompleted(InteractionResult[] results) {
        ArrayList<InteractionResult> successful = new ArrayList<>();
        /**
         * Loop through each result.
         * */
        for(int i = 0; i < results.length; i++)
        {
            /**
             * Check if the request was succesfull.
             * */
            if(results[i].getResultState() == InteractionResultState.INTERACTION_RESULT_STATE_SUCCESS)
            {
                successful.add(results[i]);
            }
            else
            {
                /**
                 * Unsuccessful.
                 * TODO: Add UI notifier. The user should know there was a error.
                 * */
            System.out.print("Interaction was unsuccessful.");
            }
        }
        InteractionResult[] successfulArray = new InteractionResult[successful.size()];
        successful.toArray(successfulArray);
        new AsyncDataProcessingTask().execute(successfulArray);
    }
}
