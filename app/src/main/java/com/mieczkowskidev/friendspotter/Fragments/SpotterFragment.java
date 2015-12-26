package com.mieczkowskidev.friendspotter.Fragments;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapFragment;
import com.mieczkowskidev.friendspotter.R;
import com.trnql.smart.base.SmartFragment;

/**
 * Created by Patryk Mieczkowski on 26.12.15
 */
public class SpotterFragment extends SmartFragment {

    private MapFragment mapFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spotter, container, false);

        initMap();

        return view;
    }

    private void initMap() {
        FragmentManager fm = getChildFragmentManager();

        mapFragment = (MapFragment) fm.findFragmentById(R.id.spotter_map_placeholder);

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().replace(R.id.spotter_map_placeholder, mapFragment).commit();
        }
    }
}
