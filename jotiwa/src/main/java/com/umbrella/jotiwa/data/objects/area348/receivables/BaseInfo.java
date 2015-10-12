package com.umbrella.jotiwa.data.objects.area348.receivables;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stesi on 22-9-2015.
 * The absolute base for a info object each object has this info.
 */
public class BaseInfo implements Parcelable {

    /**
     *
     */
    public int id;

    /**
     *
     */
    public double latitude;

    /**
     *
     */
    public double longitude;

    public BaseInfo()
    {

    }

    protected BaseInfo(Parcel in) {
        id = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<BaseInfo> CREATOR = new Creator<BaseInfo>() {
        @Override
        public BaseInfo createFromParcel(Parcel in) {
            return new BaseInfo(in);
        }

        @Override
        public BaseInfo[] newArray(int size) {
            return new BaseInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
