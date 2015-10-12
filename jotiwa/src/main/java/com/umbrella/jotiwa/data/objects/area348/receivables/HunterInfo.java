package com.umbrella.jotiwa.data.objects.area348.receivables;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.util.Map;

/**
 * Created by stesi on 13-9-2015.
 */
public class HunterInfo extends BaseInfo implements Parcelable {

    /**
     *
     */
    public String datetime;

    /**
     *
     */
    public String gebruiker;

    public HunterInfo()
    {

    }

    protected HunterInfo(Parcel in) {
        super(in);
        datetime = in.readString();
        gebruiker = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(datetime);
        dest.writeString(gebruiker);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HunterInfo> CREATOR = new Creator<HunterInfo>() {
        @Override
        public HunterInfo createFromParcel(Parcel in) {
            return new HunterInfo(in);
        }

        @Override
        public HunterInfo[] newArray(int size) {
            return new HunterInfo[size];
        }
    };

    /**
     * @param json
     * @return
     */
    public static HunterInfo fromJson(String json) {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, HunterInfo.class);
    }

    /**
     * @param json
     * @return
     */
    public static HunterInfo[] fromJsonArray(String json) {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, HunterInfo[].class);
    }

    /**
     * @param json
     * @return
     */
    public static HunterInfo[][] formJsonArrayOfArray(String json) {

        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject) parser.parse(jsonReader);
        HunterInfo[][] buffer = new HunterInfo[object.entrySet().size()][];
        int count = 0;
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            buffer[count] = fromJsonArray(entry.getValue().toString());
            count++;
        }
        return buffer;
    }
}
