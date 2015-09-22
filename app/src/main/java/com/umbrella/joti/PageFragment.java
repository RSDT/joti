package com.umbrella.joti;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PageFragment extends android.support.v4.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if(bundle != null)
        {
            String page = bundle.getString("page");
            switch(page)
            {
                case "map":
                    return inflater.inflate(R.layout.activity_maps, container, false);
                case "hello":
                    return inflater.inflate(R.layout.activity_settings, container, false);
            }
        }
        return null;
    }
}
