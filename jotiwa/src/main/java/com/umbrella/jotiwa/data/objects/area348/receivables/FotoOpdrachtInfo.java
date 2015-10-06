package com.umbrella.jotiwa.data.objects.area348.receivables;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

/**
 * Created by stesi on 13-9-2015.
 */
public class FotoOpdrachtInfo extends BaseInfo {

    public String naam;

    public String info;

    public String extra;

    public int klaar;

    public static FotoOpdrachtInfo fromJson(String json) {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, FotoOpdrachtInfo.class);
    }

    public static FotoOpdrachtInfo[] fromJsonArray(String json) {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, FotoOpdrachtInfo[].class);
    }

}
