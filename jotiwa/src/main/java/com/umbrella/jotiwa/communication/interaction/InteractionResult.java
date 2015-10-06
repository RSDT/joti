package com.umbrella.jotiwa.communication.interaction;

/**
 * Created by stesi on 22-9-2015.
 * Class for containing the result of the AsyncInteractionTask.
 */
public class InteractionResult extends HandlerDependent {

    private InteractionRequest request;

    private String receivedData;

    private InteractionResultState resultState;


    /**
     * @return
     */
    public InteractionRequest getRequest() {
        return request;
    }

    /**
     * @return
     */
    public InteractionResultState getResultState() {
        return resultState;
    }

    /**
     * @return
     */
    public String getReceivedData() {
        return receivedData;
    }

    /**
     * @param receivedData
     */
    public void setReceivedData(String receivedData) {
        this.receivedData = receivedData;
    }

    /**
     * @param request
     */
    public void setRequest(InteractionRequest request) {
        this.request = request;
    }

    /**
     * @param resultState
     */
    public void setResultState(InteractionResultState resultState) {
        this.resultState = resultState;
    }

}
