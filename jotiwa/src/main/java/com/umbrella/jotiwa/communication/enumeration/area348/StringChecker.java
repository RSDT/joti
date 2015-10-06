package com.umbrella.jotiwa.communication.enumeration.area348;

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
            string = "unknown";
        }
        return string.toLowerCase();
    }
}
