package com.umbrella.jotiwa.data.objects.area348.receivables;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;

/**
 * Created by stesi on 13-9-2015.
 */
public class ScoutingGroepInfo extends BaseInfo implements Parcelable {

    /**
     *
     */
    public String naam;

    /**
     *
     */
    public String adres;

    /**
     *
     */
    public String deelgebied;

    public ScoutingGroepInfo()
    {

    }

    protected ScoutingGroepInfo(Parcel in) {
        super(in);
        naam = in.readString();
        adres = in.readString();
        deelgebied = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(naam);
        dest.writeString(adres);
        dest.writeString(deelgebied);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ScoutingGroepInfo> CREATOR = new Creator<ScoutingGroepInfo>() {
        @Override
        public ScoutingGroepInfo createFromParcel(Parcel in) {
            return new ScoutingGroepInfo(in);
        }

        @Override
        public ScoutingGroepInfo[] newArray(int size) {
            return new ScoutingGroepInfo[size];
        }
    };

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
