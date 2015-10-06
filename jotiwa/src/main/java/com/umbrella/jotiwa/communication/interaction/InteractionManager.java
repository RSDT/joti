package com.umbrella.jotiwa.communication.interaction;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stesi on 22-9-2015.
 * Manager for sending and receiving data.
 */
public class InteractionManager extends Handler {

    /**
     *
     */
    public InteractionManager() {
        this.queued = new ArrayList<>();
        this.pending = new ArrayList<>();
        this.completed = new ArrayList<>();
    }


    private OnRequestTaskCompleted onRequestTaskCompletedListener;

    /**
     * @param onRequestTaskCompletedListener
     */
    public void setOnRequestTaskCompletedListener(OnRequestTaskCompleted onRequestTaskCompletedListener) {
        this.onRequestTaskCompletedListener = onRequestTaskCompletedListener;
    }

    private List<InteractionRequest> queued;

    private List<InteractionRequest> pending;

    private List<InteractionResult> completed;

    /**
     * @return
     */
    public List<InteractionRequest> getQueued() {
        return queued;
    }

    /**
     * @return
     */
    public List<InteractionResult> getCompleted() {
        return completed;
    }

    /**
     * @return
     */
    public List<InteractionRequest> getPending() {
        return pending;
    }


    /**
     * @param request
     */
    public void queue(InteractionRequest request) {
        request.setHandler(this);
        this.queued.add(request);
    }

    /**
     *
     */
    public void interact() {
        pending.addAll(queued);
        new AsyncInteractionTask().execute(queued.toArray(new InteractionRequest[queued.size()]));
        queued.clear();
    }

    /**
     * @param msg
     */
    @Override
    public void handleMessage(Message msg) {
        /**
         * Get the results out of the message.
         * */
        ArrayList<InteractionResult> results = (ArrayList<InteractionResult>) msg.obj;
        for (int i = 0; i < results.size(); i++) {
            pending.remove(results.get(i));
            completed.add(results.get(i));
        }
        onRequestTaskCompletedListener.onRequestTaskCompleted(results);
        super.handleMessage(msg);
    }

}
