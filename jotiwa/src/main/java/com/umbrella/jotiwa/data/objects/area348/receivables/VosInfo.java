package com.umbrella.jotiwa.data.objects.area348.receivables;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

/**
 * Created by stesi on 3-9-2015.
 */
public class VosInfo extends BaseInfo {

    public String datetime;

    public String team;

    public String team_naam;

    public String opmerking;

    public String gebruiker;

    public static VosInfo fromJson(String json) {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, VosInfo.class);
    }

    public static VosInfo[] fromJsonArray(String json) {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, VosInfo[].class);
    }

}
