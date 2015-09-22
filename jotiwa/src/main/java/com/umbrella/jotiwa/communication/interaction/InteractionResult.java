package com.umbrella.jotiwa.communication.interaction;

/**
 * Created by stesi on 22-9-2015.
 * Class for containing the result of the AsyncInteractionTask.
 */
public class InteractionResult extends HandlerDependent {

    private InteractionRequest request;

    private String receivedData;

    private InteractionResultState resultState;


    public InteractionRequest getRequest() {
        return request;
    }

    public InteractionResultState getResultState() {
        return resultState;
    }

    public String getReceivedData() {
        return receivedData;
    }

    public void setReceivedData(String receivedData) {
        this.receivedData = receivedData;
    }

    public void setRequest(InteractionRequest request) {
        this.request = request;
    }

    public void setResultState(InteractionResultState resultState) {
        this.resultState = resultState;
    }

}
