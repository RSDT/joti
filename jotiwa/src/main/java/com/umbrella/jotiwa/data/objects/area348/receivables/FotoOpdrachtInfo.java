package com.umbrella.jotiwa.data.objects.area348.receivables;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;


/**
 *
 */
public class FotoOpdrachtInfo extends BaseInfo implements Parcelable {

    /**
     *
     */
    public String naam;

    /**
     *
     */
    public String info;

    /**
     *
     */
    public String extra;

    /**
     *
     */
    public int klaar;


    public FotoOpdrachtInfo()
    {

    }

    protected FotoOpdrachtInfo(Parcel in) {
        super(in);
        naam = in.readString();
        info = in.readString();
        extra = in.readString();
        klaar = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(naam);
        dest.writeString(info);
        dest.writeString(extra);
        dest.writeInt(klaar);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FotoOpdrachtInfo> CREATOR = new Creator<FotoOpdrachtInfo>() {
        @Override
        public FotoOpdrachtInfo createFromParcel(Parcel in) {
            return new FotoOpdrachtInfo(in);
        }

        @Override
        public FotoOpdrachtInfo[] newArray(int size) {
            return new FotoOpdrachtInfo[size];
        }
    };

    /**
     * @param json
     * @return
     */
    public static FotoOpdrachtInfo fromJson(String json) {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, FotoOpdrachtInfo.class);
    }

    /**
     * @param json
     * @return
     */
    public static FotoOpdrachtInfo[] fromJsonArray(String json) {
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(json));
        jsonReader.setLenient(true);
        return new Gson().fromJson(jsonReader, FotoOpdrachtInfo[].class);
    }

}
