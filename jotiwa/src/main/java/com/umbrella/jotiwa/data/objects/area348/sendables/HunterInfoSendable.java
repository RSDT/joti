package com.umbrella.jotiwa.data.objects.area348.sendables;

import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.umbrella.jotiwa.JotiApp;
import com.umbrella.jotiwa.communication.enumeration.area348.StringChecker;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 5-10-2015
 * Serializable object for sending data to the area348 server in the correct format.
 * @see @link https://docs.google.com/document/d/1yfxXJFEXIl5Zpi148vRngNmCMT5YfEBrU-rAstbE-A0/edit#heading=h.xcn9egdzhw1o
 */
public class HunterInfoSendable {

    /**
     * Initializes a new instance of HunterInfoSendable.
     * */
    public HunterInfoSendable(){}

    /**
     * Initializes a new instance of HunterInfoSendable.
     * @param gebruiker The username of the hunter.
     * @param latitude  The position's latitude value.
     * @param longitude The position's longitude value.
     * */
    public HunterInfoSendable(String gebruiker, double latitude, double longitude)
    {
        this.gebruiker = gebruiker;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * The user of the sendable.
     * */
    public String gebruiker;

    /**
     * The latitude of the sendable.
     * */
    public double latitude;

    /**
     * The longitude of the sendable.
     * */
    public double longitude;

    /**
     * Gets the current state of this hunter in the sendable form.
     * */
    public static HunterInfoSendable get()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext());
        HunterInfoSendable buffer = new HunterInfoSendable();
        buffer.gebruiker = StringChecker.makeSafe(sharedPreferences.getString("pref_username", JotiApp.getNoUsername()));
        Location location = JotiApp.getLastLocation();
        if(location != null)
        {
            buffer.latitude = location.getLatitude();
            buffer.longitude = location.getLongitude();
        }
        return buffer;
    }

}
