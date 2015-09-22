package com.umbrella.jotiwa.communication.enumeration.area348;

/**
 * Created by stesi on 22-9-2015.
 * Contains the enumeration for each vos team.
 */
public enum TeamPart {
    Alpha("a"),

    Bravo("b"),

    Charlie("c"),

    Delta("d"),

    Echo("e"),

    Foxtrot("f"),

    XRay("x"),

    All("y"),

    None("z");

    private final String subChar;

    public String getSubChar() {
        return subChar;
    }

    TeamPart(String subChar)
    {
        this.subChar = subChar;
    }

    public static TeamPart ParseSub(String string)
    {
        switch(string)
        {
            case "a":
                return TeamPart.Alpha;
            case "b":
                return TeamPart.Bravo;
            case "c":
                return TeamPart.Charlie;
            case "d":
                return TeamPart.Delta;
            case "e":
                return TeamPart.Echo;
            case "f":
                return TeamPart.Foxtrot;
            case "x":
                return TeamPart.XRay;
            default:
                return TeamPart.None;
        }
    }
}
