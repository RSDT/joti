package com.umbrella.joti;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by stesi on 22-9-2015.
 */
public class PageAdaptor extends FragmentPagerAdapter {

    String[] pages;

    int currentPage = 0;

    public int getCurrentPage() {
        return currentPage;
    }

    MapFragment mapFragment;

    public PageAdaptor(FragmentManager fm) {
        super(fm);
        mapFragment = new MapFragment();
    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch(position)
        {
            case AppPage.HOME:
                fragment = new InfoFragment();
                break;
            case AppPage.MAP:
                fragment = mapFragment;
                break;
            case AppPage.SETTINGS:
                fragment = new InfoFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
