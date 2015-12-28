package com.mieczkowskidev.friendspotter.Fragments;

import android.app.FragmentManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mieczkowskidev.friendspotter.MainActivity;
import com.mieczkowskidev.friendspotter.R;
import com.trnql.smart.base.SmartFragment;
import com.trnql.smart.people.PersonEntry;
import com.trnql.smart.places.PlaceEntry;

import java.util.HashMap;

/**
 * Created by Patryk Mieczkowski on 26.12.15
 */
public class SpotterFragment extends SmartFragment {

    public static final String TAG = SpotterFragment.class.getSimpleName();

    private MapFragment mapFragment;
    private GoogleMap googleMap;
    private ProgressBar mapProgressBar;
    private FloatingActionButton refreshButton;

    private HashMap<Marker, PersonEntry> markerPersonEntryHashMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spotter, container, false);

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (appCompatActivity.getSupportActionBar() != null) {
            appCompatActivity.getSupportActionBar().setTitle(getString(R.string.drawer_spotter));
        }

        initViews(view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initMap();
        setMapReadyCallback();
    }

    private void initViews(View view) {

        mapProgressBar = (ProgressBar) view.findViewById(R.id.map_progress_bar);
        refreshButton = (FloatingActionButton) view.findViewById(R.id.spotter_floating_refresh_button);
    }

    private void setListeners() {

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.clear();
                zoomMapToLocation();
                drawPeopleMarkers();
                drawPlaceMarkers();
            }
        });
    }

    private void initMap() {
        FragmentManager fm = getChildFragmentManager();

        mapFragment = (MapFragment) fm.findFragmentById(R.id.spotter_map_placeholder);

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().replace(R.id.spotter_map_placeholder, mapFragment).commit();
        }
    }

    private void setMapReadyCallback() {

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMapCallback) {
                googleMap = googleMapCallback;

                doOnMapReady();
            }
        });

    }

    private void doOnMapReady() {
        Log.d(TAG, "doOnMapReady");

        if (googleMap != null) {
            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {

                    googleMap.setMyLocationEnabled(true);

                    zoomMapToLocation();
                    drawPeopleMarkers();
                    drawPlaceMarkers();

                    setListeners();
                    setInfoWindowAdapter();

                }
            });
        }
    }

    private void drawPeopleMarkers() {
        Log.d(TAG, "drawPeopleMarkers()");

        if (MainActivity.personEntryList != null) {
            progressBarAction(false);
            Log.d(TAG, "drawPeopleMarkers() for " + MainActivity.personEntryList.size());

            markerPersonEntryHashMap.clear();

            for (PersonEntry personEntry : MainActivity.personEntryList) {

                Marker marker = googleMap.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(personEntry.getLatitude(), personEntry.getLongitude()))
                                .title(personEntry.getUserToken())
                                .snippet(String.valueOf(personEntry.getDistanceFromUser()) + " m")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_friend2)));

                markerPersonEntryHashMap.put(marker, personEntry);


            }
        }
    }

    private void drawPlaceMarkers() {
        Log.d(TAG, "drawPlaceMarkers()");

        if (MainActivity.placeEntryList != null) {
            progressBarAction(false);
            Log.d(TAG, "drawPlaceMarkers() for " + MainActivity.placeEntryList.size());

            for (PlaceEntry placeEntry : MainActivity.placeEntryList) {

                googleMap.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(placeEntry.getLatitude(), placeEntry.getLongitude()))
                                .title(placeEntry.getName())
                                .snippet(String.valueOf(placeEntry.getDistanceFromUser() + " m"))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_places)));
            }
        }
    }

    private void zoomMapToLocation() {

        if (MainActivity.currentPosition != null) {
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(MainActivity.currentPosition, 14);
            googleMap.animateCamera(update, 700, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {

                }

                @Override
                public void onCancel() {

                }
            });
        }

    }

    private void setInfoWindowAdapter() {

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                if (markerPersonEntryHashMap.containsKey(marker)) {
                    PersonEntry personEntry = markerPersonEntryHashMap.get(marker);
                    View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_marker_people, null);

                    TextView usernameText = (TextView) view.findViewById(R.id.marker_people_username);

                    usernameText.setText(personEntry.getUserToken());
                    return view;
                } else {
                    return null;
                }
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (markerPersonEntryHashMap.containsKey(marker)) {
                    PersonEntry personEntry = markerPersonEntryHashMap.get(marker);

                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).startUserDetailsActivity();
                    }
                }
            }
        });
    }

    private void progressBarAction(boolean action) {

        mapProgressBar.setVisibility(action ? View.VISIBLE : View.GONE);

    }

}
