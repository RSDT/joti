package com.umbrella.jotiwa.data.objects.area348.receivables;

import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.umbrella.jotiwa.JotiApp;
import com.umbrella.jotiwa.data.objects.area348.sendables.HunterInfoSendable;

import java.util.Date;
import java.util.Map;

/**
 * Created by stesi on 13-9-2015.
 */
public class HunterInfo extends BaseInfo {

    public String datetime;

    public String gebruiker;

    public static HunterInfo fromJson(String json)
    {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, HunterInfo.class);
    }

    public static HunterInfo[] fromJsonArray(String json)
    {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, HunterInfo[].class);
    }

    public static HunterInfo[][] formJsonArrayOfArray(String json)
    {

        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject)parser.parse(jsonReader);
        HunterInfo[][] buffer = new HunterInfo[object.entrySet().size()][];
        int count = 0;
        for (Map.Entry<String,JsonElement> entry : object.entrySet()) {
            buffer[count] = fromJsonArray(entry.getValue().toString());
            count++;
        }
        return buffer;
    }
}
