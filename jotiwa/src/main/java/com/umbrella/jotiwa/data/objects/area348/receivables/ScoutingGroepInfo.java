package com.umbrella.jotiwa.data.objects.area348.receivables;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

/**
 * Created by stesi on 13-9-2015.
 */
public class ScoutingGroepInfo extends BaseInfo {

    /**
     *
     */
    public String naam;

    /**
     *
     */
    public String adres;

    /**
     * @param json
     * @return
     */
    public static ScoutingGroepInfo fromJson(String json) {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, ScoutingGroepInfo.class);
    }

    /**
     * @param json
     * @return
     */
    public static ScoutingGroepInfo[] fromJsonArray(String json) {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, ScoutingGroepInfo[].class);
    }
}
