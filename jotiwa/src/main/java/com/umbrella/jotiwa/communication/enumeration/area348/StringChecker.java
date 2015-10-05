package com.umbrella.jotiwa.communication.enumeration.area348;

import com.umbrella.jotiwa.JotiApp;

/**
 * Created by stesi on 5-10-2015.
 */
public class StringChecker {

    public static String makeSafe(String string)
    {
        string = string.replace("\\", "");
        string = string.replace("\"", "");
        string = string.replace("\n", "");
        string = string.replace("/", "");
        string = string.replace("\t", "");
        string = string.replace(" ", "");
        string = string.replace("-", "");
        string = string.replace("*", "");
        string = string.replace("'", "");
        string = string.replace("%", "");
        if (string.isEmpty()) {
            string = JotiApp.getNoUsername();
        }
        return string.toLowerCase();
    }
}
