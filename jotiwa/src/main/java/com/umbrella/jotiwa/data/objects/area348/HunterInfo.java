package com.umbrella.jotiwa.data.objects.area348;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

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
        return new Gson().fromJson(jsonReader, HunterInfo[][].class);
    }

}
