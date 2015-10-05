package com.umbrella.jotiwa.communication.interaction;

import java.net.URL;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.1
 * @since 22-9-2015
 * Class that represents a interaction request.
 */
public class InteractionRequest extends HandlerDependent {

    /**
     * Creates a new instance of InteractionRequest.
     * @param url  The url of the request.
     * @param data The data that should be send.
     * */
    public InteractionRequest(URL url, String data)
    {
        this.url = url;
        this.data = data;
    }

    /**
     * Creates a new instance of InteractionRequest.
     * @param url  The url of the request.
     * @param data The data that should be send.
     * @param needsHandling The value indicating if the request should be handled after completion.
     * */
    public InteractionRequest(URL url, String data, boolean needsHandling)
    {
        this.url = url;
        this.data = data;
        this.needsHandling = needsHandling;
    }

    /**
     * The url of the request.
     * */
    private URL url;

    /**
     * The data of the request that should be send.
     * */
    private String data;

    /**
     * Value indicating if the request needs handling after completion.
     * */
    private boolean needsHandling = true;

    public boolean needsHandling() {
        return needsHandling;
    }

    public URL getUrl() {
        return url;
    }

    public String getData() {
        return data;
    }


}
