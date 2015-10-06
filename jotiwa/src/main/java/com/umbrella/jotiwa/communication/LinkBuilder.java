package com.umbrella.jotiwa.communication;

import java.net.URL;

/**
 * Created by stesi on 22-9-2015.
 */
public class LinkBuilder {

    private static String root;

    /**
     * @return
     */
    public static String getRoot() {
        return root;
    }

    /**
     * @param root
     */
    public static void setRoot(String root) {
        LinkBuilder.root = root;
    }

    /**
     * @param args
     * @return
     */
    public static URL build(String[] args)
    {
        String pasted = root;
        for(int i = 0; i < args.length; i++)
        {
            pasted += "/" + args[i];
        }
        try { return new URL(pasted); } catch (Exception e) { return null; }
    }
}
