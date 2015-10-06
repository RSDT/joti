package com.umbrella.jotiwa.communication.interaction;

import java.util.ArrayList;

/**
 * Created by stesi on 22-9-2015.
 * Class that servers as a callback.
 */
public interface OnRequestTaskCompleted {

    /**
     * @param results
     */
    void onRequestTaskCompleted(ArrayList<InteractionResult> results);
}
