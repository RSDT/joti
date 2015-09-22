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

    public InteractionManager()
    {
        this.queued = new ArrayList<>();
        this.pending = new ArrayList<>();
        this.completed = new ArrayList<>();
    }


    private OnRequestTaskCompleted onRequestTaskCompletedListener;

    public void setOnRequestTaskCompletedListener(OnRequestTaskCompleted onRequestTaskCompletedListener) {
        this.onRequestTaskCompletedListener = onRequestTaskCompletedListener;
    }

    private List<InteractionRequest> queued;

    private List<InteractionRequest> pending;

    private List<InteractionResult> completed;

    public List<InteractionRequest> getQueued() {
        return queued;
    }

    public List<InteractionResult> getCompleted() {
        return completed;
    }

    public List<InteractionRequest> getPending() {
        return pending;
    }


    public void queue(InteractionRequest request)
    {
        request.setHandler(this);
        this.queued.add(request);
    }

    public void interact()
    {
        pending.addAll(queued);
        new AsyncInteractionTask().execute(queued.toArray(new InteractionRequest[queued.size()]));
        queued.clear();
    }

    @Override
    public void handleMessage(Message msg) {
        /**
         * Get the results out of the message.
         * */
        InteractionResult[] results = (InteractionResult[])msg.obj;
        for(int i = 0; i < results.length; i++)
        {
            pending.remove(results[i]);
            completed.add(results[i]);
        }
        onRequestTaskCompletedListener.onRequestTaskCompleted(results);
        super.handleMessage(msg);
    }

}
