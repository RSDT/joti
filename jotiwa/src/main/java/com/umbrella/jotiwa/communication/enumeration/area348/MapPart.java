package com.umbrella.jotiwa.communication.enumeration.area348;

/**
 * Created by stesi on 13-9-2015.
 * Contains all the different map parts.
 * And some helper functions to improve flow.
 */
public enum MapPart
{
    Vossen("vos"),

    Hunters("hunter"),

    ScoutingGroepen("sc"),

    FotoOpdrachten("foto"),

    Me("me"),

    All("all"),

    None("none");

    private final String value;

    public String getValue() {
        return value;
    }

    MapPart(String string)
    {
     this.value = string;
    }

    public static MapPart parse(String string)
    {
        switch(string)
        {
            case "vos":
                return MapPart.Vossen;
            case "hunters":
                return MapPart.Hunters;
            case "sc":
                return MapPart.ScoutingGroepen;
            case "foto":
                return MapPart.FotoOpdrachten;
            case "me":
                return MapPart.Me;
            case "all":
                return MapPart.All;
            case "none":
                return MapPart.None;
            default:
                return MapPart.None;
        }
    }

}
