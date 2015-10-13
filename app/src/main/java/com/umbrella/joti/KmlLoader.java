package com.umbrella.joti;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.kml.KmlContainer;
import com.google.maps.android.kml.KmlLayer;
import com.google.maps.android.kml.KmlPlacemark;
import com.google.maps.android.kml.KmlPolygon;
import com.umbrella.jotiwa.Constants;
import com.umbrella.jotiwa.JotiApp;
import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Mattijn on 11/10/2015.
 */
public class KmlLoader implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final String DEELGEBIEDEN_OVERLAY_KEY = "pref_deelgebieden_overlay";
    private final int kmlfile;
    GoogleMap Gmap;
    HashMap<TeamPart,Polygon> deelgebieden = new HashMap<>();
    KmlLoader(GoogleMap gmap, int KmlFile){
        Gmap=gmap;
        this.kmlfile = KmlFile;
        PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext()).registerOnSharedPreferenceChangeListener(this);
    }
    public void ReadKML(){
        try {
            KmlLayer kmllayer = new KmlLayer(Gmap, kmlfile, JotiApp.getContext());
            for (KmlContainer temp : kmllayer.getContainers()){
                for (KmlContainer temp2: temp.getContainers()){
                    if (temp2.getProperty("name").equals("Deelgebieden")){
                        for (KmlPlacemark deelgebied : temp2.getPlacemarks()){
                            TeamPart teampart = TeamPart.parse(String.valueOf(deelgebied.getProperty("name").toLowerCase().charAt(0)));
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(JotiApp.getContext());
                            boolean show = preferences.getBoolean("pref_vos_" + teampart.toString().toLowerCase().charAt(0), true);
                            KmlPolygon p = (KmlPolygon) deelgebied.getGeometry();
                            deelgebieden.put(teampart, Gmap.addPolygon(new PolygonOptions()
                                    .addAll(p.getOuterBoundaryCoordinates())
                                    .fillColor(TeamPart.getAssociatedAlphaColor(teampart, Constants.alfaDeelgebieden))
                                    .strokeWidth(2)
                                    .visible(show)));
                            System.out.println(deelgebied.getProperty("name"));
                        }
                    }
                }
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (key.equals(DEELGEBIEDEN_OVERLAY_KEY)){
            for (TeamPart teamPart : deelgebieden.keySet()){
                if (preferences.getBoolean(DEELGEBIEDEN_OVERLAY_KEY,true)){
                    boolean show = preferences.getBoolean("pref_vos_" + teamPart.toString().toLowerCase().charAt(0), true);
                    if (show) {
                        deelgebieden.get(teamPart).setVisible(true);
                    }
                }else{
                    deelgebieden.get(teamPart).setVisible(false);
                }
            }
        }
        String[] temp = key.split("_");
        String[] typeCode = new String[3];
        for (int i = 0; i < temp.length && i < 3; i++) {
            typeCode[i] = temp[i];
        }
        MapPart mapPart = MapPart.parse(typeCode[1]);
        if (mapPart == null) return;
        if (mapPart != MapPart.Vossen) return;
        TeamPart teamPart = TeamPart.parse(typeCode[2]);
        if (deelgebieden.containsKey(teamPart)){
            boolean show = preferences.getBoolean(key, true);
            Polygon p = deelgebieden.get(teamPart);
            p.setVisible(show);
        }
    }
}
