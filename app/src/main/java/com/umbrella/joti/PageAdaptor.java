package com.umbrella.joti;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by stesi on 22-9-2015.
 */
public class PageAdaptor extends FragmentPagerAdapter {

    String[] pages;

    public PageAdaptor(FragmentManager fm) {
        super(fm);
        pages = new String[2];
        pages[0] = "hello";
        pages[1] = "map";
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("page", pages[position]);
        PageFragment pageFragment = new PageFragment();
        pageFragment.setArguments(bundle);
        return pageFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pages[position];
    }

    @Override
    public int getCount() {
        return pages.length;
    }
}
