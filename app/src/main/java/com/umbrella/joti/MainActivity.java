package com.umbrella.joti;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.communication.enumeration.area348.TeamPart;
import com.umbrella.jotiwa.communication.interaction.area348.DataUpdater;

public class MainActivity extends FragmentActivity {

    PageAdaptor pageAdaptor;

    ViewPager pager;

    DataUpdater updater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pageAdaptor = new PageAdaptor(getSupportFragmentManager());

        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(pageAdaptor);

        DataUpdater dataUpdater = new DataUpdater();
        dataUpdater.update(MapPart.Vossen, TeamPart.All);
    }




}
