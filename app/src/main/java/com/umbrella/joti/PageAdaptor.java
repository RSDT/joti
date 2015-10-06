package com.umbrella.joti;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by stesi on 22-9-2015.
 */
public class PageAdaptor extends FragmentPagerAdapter {

    /**
     *
     */
    String[] pages;

    int currentPage = 0;

    /**
     * @return
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * @param fm
     */
    public PageAdaptor(FragmentManager fm) {
        super(fm);
    }


    /**
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case AppPage.HOME:
                fragment = new InfoFragment();
                break;
            case AppPage.MAP:
                fragment = new MapFragment();
                break;
            case AppPage.SETTINGS:
                fragment = new InfoFragment();
                break;
        }
        return fragment;
    }

    /**
     * @return
     */
    @Override
    public int getCount() {
        return 3;
    }
}
