package com.umbrella.jotiwa.map.area348.storage;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.umbrella.jotiwa.data.objects.area348.receivables.BaseInfo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by stesi on 2-10-2015.
 */
public class StorageObject implements Parcelable {

    /**
     *
     */
    public StorageObject() {
        this.associatedInfo = new ArrayList<>();
        this.markers = new ArrayList<>();
        this.polylines = new ArrayList<>();
        this.circles = new ArrayList<>();
    }


    //region values
    private ArrayList<MarkerOptions> markers;

    private ArrayList<PolylineOptions> polylines;

    private ArrayList<CircleOptions> circles;

    private ArrayList<BaseInfo> associatedInfo;
    //endregion


    //region fields

    protected StorageObject(Parcel in) {
        markers = in.createTypedArrayList(MarkerOptions.CREATOR);
        polylines = in.createTypedArrayList(PolylineOptions.CREATOR);
        circles = in.createTypedArrayList(CircleOptions.CREATOR);
        associatedInfo = in.createTypedArrayList(BaseInfo.CREATOR);
    }

    public static final Creator<StorageObject> CREATOR = new Creator<StorageObject>() {
        @Override
        public StorageObject createFromParcel(Parcel in) {
            return new StorageObject(in);
        }

        @Override
        public StorageObject[] newArray(int size) {
            return new StorageObject[size];
        }
    };

    /**
     * @return
     */
    //region get
    public ArrayList<MarkerOptions> getMarkers() {
        return markers;
    }

    /**
     * @return
     */
    public ArrayList<PolylineOptions> getPolylines() {
        return polylines;
    }

    /**
     * @return
     */
    public ArrayList<CircleOptions> getCircles() {
        return circles;
    }

    /**
     * @return
     */
    public ArrayList<BaseInfo> getAssociatedInfo() {
        return associatedInfo;
    }
    //endregion

    /**
     * @param markers
     */
    //region set
    public void setMarkers(ArrayList<MarkerOptions> markers) {
        this.markers = markers;
    }

    /**
     * @param polylines
     */
    public void setPolylines(ArrayList<PolylineOptions> polylines) {
        this.polylines = polylines;
    }

    /**
     * @param circles
     */
    public void setCircles(ArrayList<CircleOptions> circles) {
        this.circles = circles;
    }

    /**
     * @param associatedInfo
     */
    public void setAssociatedInfo(ArrayList<BaseInfo> associatedInfo) {
        this.associatedInfo = associatedInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(markers);
        dest.writeTypedList(polylines);
        dest.writeTypedList(circles);
        dest.writeTypedList(associatedInfo);
    }
    //endregion

    //endregion


}
