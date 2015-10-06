package com.umbrella.jotiwa.communication.enumeration.area348;

import android.graphics.Color;

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

    public static TeamPart parse(String string)
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

    public static int getAssociatedColor(TeamPart teamPart)
    {
        switch (teamPart)
        {
            case Alpha:
                return Color.argb(255, 255, 0, 0);
            case Bravo:
                return Color.argb(255, 0, 255, 0);
            case Charlie:
                return Color.argb(255, 0, 0, 255);
            case Delta:
                return Color.argb(255, 0, 255, 255);
            case Echo:
                return Color.argb(255, 255, 0, 255);
            case Foxtrot:
                return Color.argb(255, 255, 162, 0);
            case XRay:
                return Color.argb(255, 0, 0, 0);
            default:
                return Color.WHITE;
        }
    }

    public static int getAssociatedAlphaColor(TeamPart teamPart, int a)
    {
        switch (teamPart)
        {
            case Alpha:
                return Color.argb(a, 255, 0, 0);
            case Bravo:
                return Color.argb(a, 0, 255, 0);
            case Charlie:
                return Color.argb(a, 0, 0, 255);
            case Delta:
                return Color.argb(a, 0, 255, 255);
            case Echo:
                return Color.argb(a, 255, 0, 255);
            case Foxtrot:
                return Color.argb(a, 255, 162, 0);
            case XRay:
                return Color.argb(a, 0, 0, 0);
            default:
                return Color.WHITE;
        }
    }

}
