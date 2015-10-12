package com.umbrella.jotiwa.data.objects.area348.receivables;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

/**
 * Created by stesi on 3-9-2015.
 */
public class VosInfo extends BaseInfo implements Parcelable {

    /**
     *
     */
    public String datetime;

    /**
     *
     */
    public String team;

    /**
     *
     */
    public String team_naam;

    /**
     *
     */
    public String opmerking;

    /**
     *
     */
    public String gebruiker;


    public VosInfo()
    {

    }

    protected VosInfo(Parcel in) {
        super(in);
        datetime = in.readString();
        team = in.readString();
        team_naam = in.readString();
        opmerking = in.readString();
        gebruiker = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(datetime);
        dest.writeString(team);
        dest.writeString(team_naam);
        dest.writeString(opmerking);
        dest.writeString(gebruiker);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VosInfo> CREATOR = new Creator<VosInfo>() {
        @Override
        public VosInfo createFromParcel(Parcel in) {
            return new VosInfo(in);
        }

        @Override
        public VosInfo[] newArray(int size) {
            return new VosInfo[size];
        }
    };

    /**
     * @param json
     * @return
     */
    public static VosInfo fromJson(String json) {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, VosInfo.class);
    }

    /**
     * @param json
     * @return
     */
    public static VosInfo[] fromJsonArray(String json) {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, VosInfo[].class);
    }

}
