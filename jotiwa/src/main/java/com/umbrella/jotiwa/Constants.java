package com.umbrella.jotiwa;

public class Constants {
    //drawables
    public static final int hunter = R.drawable.car;
    public static final int foto_todo = R.drawable.camera_rood;
    public static final int foto_klaar = R.drawable.camera_groen;
    public static final int scouting_groep = R.drawable.scouting_groep;
    public static final int me = R.drawable.arrow_blauw;

    //scale bijvoorbeeld 2 is de helft van de orginele size en 3 eenderde enz.
    public static final int scaleMe = 2;
    public static final int scaleTarget = 1;
    public static final int scaleFoto = 2;
    public static final int scaleScoutinggroepen = 1;
    public static final int scaleHunter = 2;
    public static final int scaleDots = 4;

    //drawables theme
    public static final int hunterTheme = R.drawable.x_wing_hunter;
    public static final int foto_todoTheme = R.drawable.r2d2_foto_wit;
    public static final int foto_klaarTheme = R.drawable.r2d2_foto_groen;
    public static final int scouting_groepTheme = R.drawable.at_at_vos;
    public static final int meTheme = R.drawable.arrow_oranje;

    //scale bijvoorbeeld 2 is de helft van de orginele size en 3 eenderde enz.
    public static final int scaleMeTheme = 2;
    public static final int scaleTargetTheme = 1;
    public static final int scaleFotoTheme = 2;
    public static final int scaleScoutinggroepenTheme = 2;
    public static final int scaleHunterTheme = 2;
    public static final int scaleDotsTheme = 4;

    //lineThickness in pixels
    public static final int lineThicknessVos = JotiApp.getContext().getResources().getInteger(R.integer.lineWidthVos);
    public static final int lineThicknessHunter = JotiApp.getContext().getResources().getInteger(R.integer.lineWidthHunter);
    public static final int lineThicknessMe = JotiApp.getContext().getResources().getInteger(R.integer.lineWidthMe);
    public static final int lineThicknesScoutinggroepCircle = JotiApp.getContext().getResources().getInteger(R.integer.lineWidthScoutinggroepCircle);
    public static final int lineThicknesVosCircle = JotiApp.getContext().getResources().getInteger(R.integer.lineWidthVosCircle);
    public static final int lineThicknessDeelgebieden = JotiApp.getContext().getResources().getInteger(R.integer.lineWidthDeelgebieden);
    public static final float lineThicknessMeCircle = JotiApp.getContext().getResources().getInteger(R.integer.lineWidthMeCircle);

    //radius in m
    public static final int radiusScoutingroup = 500;

    //alfa transparantie getal 1-255, 1 is meest transparant
    public static final int alfaScoutingroepCirkel = 10;
    public static final int alfaVosCircle = 96;
    public static final int alfaDeelgebieden = 50;
    public static final int alfaMeCircle = 10;

    //Color
    public static final int meColorRed = 0;
    public static final int meColorGreen = 153;
    public static final int meColorBlue = 153;


    //TODO preference keys toevoegen
}