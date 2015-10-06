package com.umbrella.jotiwa.communication.enumeration.area348;


import java.net.URL;

/**
 * Created by stesi on 22-9-2015.
 * Class that servers as a tool, to retrieve info from a url.
 */
public class Area348_Linker {
    public static MapPart parseMapPart(URL url) {
        String s = url.toString().split("/")[3];
        return MapPart.parse(s);
    }
}
