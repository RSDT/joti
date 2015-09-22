package com.umbrella.jotiwa.communication.interaction;

import android.os.Handler;

import java.net.URL;

/**
 * Created by stesi on 22-9-2015.
 * Class for containing the request.
 */
public class InteractionRequest extends HandlerDependent {

    public InteractionRequest(URL url, String data)
    {
        this.url = url;
        this.data = data;
    }

    private URL url;

    private String data;

    public URL getUrl() {
        return url;
    }

    public String getData() {
        return data;
    }
}
