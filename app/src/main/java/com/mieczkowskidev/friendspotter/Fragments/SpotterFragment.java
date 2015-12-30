package com.mieczkowskidev.friendspotter.Fragments;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mieczkowskidev.friendspotter.API.RestAPI;
import com.mieczkowskidev.friendspotter.Config;
import com.mieczkowskidev.friendspotter.MainActivity;
import com.mieczkowskidev.friendspotter.Objects.Event;
import com.mieczkowskidev.friendspotter.Objects.User;
import com.mieczkowskidev.friendspotter.R;
import com.mieczkowskidev.friendspotter.Utils.GenericConverter;
import com.mieczkowskidev.friendspotter.Utils.LoginManager;
import com.mieczkowskidev.friendspotter.Utils.PlaceInterface;
import com.trnql.smart.base.SmartFragment;
import com.trnql.smart.people.PersonEntry;
import com.trnql.smart.places.PlaceEntry;
import com.trnql.zen.core.db.DbKVP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Subscriber;

/**
 * Created by Patryk Mieczkowski on 26.12.15
 */
public class SpotterFragment extends SmartFragment {

    public static final String TAG = SpotterFragment.class.getSimpleName();

    private MapFragment mapFragment;
    private GoogleMap googleMap;
    private ProgressBar mapProgressBar;
    private FloatingActionButton refreshButton;
    private List<Event> eventList = new ArrayList<>();

    private HashMap<Marker, PersonEntry> markerPersonEntryHashMap = new HashMap<>();

    public static SpotterFragment newInstance() {
        SpotterFragment myFragment = new SpotterFragment();
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

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

    @Override
    protected void smartPeopleChange(List<PersonEntry> people) {
        super.smartPeopleChange(people);

        if (people != null && people.size() != 0) {

            Log.d(TAG, "smartPeopleChange() called with: " + "people = [" + people.size() + "]");

//            if (Config.personEntryList == null) {
//                Config.personEntryList = new ArrayList<>();
//            }

            if (Config.personEntryList != null) {
                Config.personEntryList.clear();
            }

            Config.personEntryList = people;

            drawMarkers();


//            for (PersonEntry personEntry : people) {
//                Log.d(TAG, "people: " + personEntry.getUserToken() + ", activity: "
//                        + personEntry.getActivityString() + ", distance: " + personEntry.getDistanceFromUser()
//                + "m, datapayload: " + personEntry.getDataPayload());
//
////            person.getUserToken();          // "2948574687"
////            person.getLatitude();           // 36.068821
////            person.getLongitude();          // -112.152823
////            person.getActivityString();     // "Running, Driving, etc."
////            person.getDistanceFromUser();   // 592 meters
////            person.getTimeStamp();          // Date object
////            person.getDataPayload();        // {"name":"Johnny", "status":"busy"} - Your own custom data!
//
//            }
        }
    }

    @Override
    protected void smartPlacesChange(List<PlaceEntry> places) {
        super.smartPlacesChange(places);

        if (places != null && places.size() != 0) {

            Log.d(TAG, "smartPlacesChange() called with: " + "places = [" + places.size() + "]");
//            if (Config.placeEntryList == null) {
//                Config.placeEntryList = new ArrayList<>();
//            }
//            Log.d(TAG, "smartPlacesChange() called with: " + "places = [" + places.toString() + "]");

            if (Config.placeEntryList != null) {
                Config.placeEntryList.clear();
            }

            Config.placeEntryList = places;

            drawMarkers();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_spotter, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.marker_filter:
                filterDialog();
                break;
        }

        return true;
    }

    private void initViews(View view) {

        mapProgressBar = (ProgressBar) view.findViewById(R.id.map_progress_bar);
        refreshButton = (FloatingActionButton) view.findViewById(R.id.spotter_floating_refresh_button);
    }

    private void setListeners() {

//        refreshButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                googleMap.clear();
//                zoomMapToLocation();
//                drawPeopleMarkers();
//                drawPlaceMarkers();
//            }
//        });
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

                    if (Config.personEntryList != null && Config.placeEntryList != null) {
                        Log.d(TAG, "onClick() called with: " + "person: " + Config.personEntryList.size()
                                + ", places: " + Config.placeEntryList.size());
                    }

                    googleMap.setMyLocationEnabled(true);

                    zoomMapToLocation();
                    drawMarkers();

                    setListeners();
                    setInfoWindowAdapter();

                    downloadEventMarkers();

                }
            });
        }
    }

    private void drawMarkers() {

        if (googleMap != null) {
            googleMap.clear();

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("markers", Context.MODE_PRIVATE);

            if (sharedPreferences.getBoolean("people_markers", true)) {
                drawPeopleMarkers();
            }
            if (sharedPreferences.getBoolean("places_markers", true)) {
                drawPlaceMarkers();
            }
            if (sharedPreferences.getBoolean("events_markers", true)) {
                drawEventMarkers();
            }
        }
    }

    private void drawPeopleMarkers() {
        Log.d(TAG, "drawPeopleMarkers()");

        if (Config.personEntryList != null && Config.personEntryList.size() != 0) {
            progressBarAction(false);
            Log.d(TAG, "drawPeopleMarkers() for " + Config.personEntryList.size());

            markerPersonEntryHashMap.clear();

            for (PersonEntry personEntry : Config.personEntryList) {

                if (!personEntry.getUserToken().contains("Anonymous")) {
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
    }

    private void drawPlaceMarkers() {
        Log.d(TAG, "drawPlaceMarkers()");

        if (Config.placeEntryList != null && Config.placeEntryList.size() != 0) {
            progressBarAction(false);
            Log.d(TAG, "drawPlaceMarkers() for " + Config.placeEntryList.size());

            for (PlaceEntry placeEntry : Config.placeEntryList) {

                googleMap.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(placeEntry.getLatitude(), placeEntry.getLongitude()))
                                .title(placeEntry.getName())
                                .snippet(String.valueOf(placeEntry.getDistanceFromUser() + " m"))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_places)));
            }
        } else if (getPlacesManager().getLatestPlaceList() != null && getPlacesManager().getLatestPlaceList().size() != 0) {
            progressBarAction(false);
            Log.d(TAG, "drawPlaceMarkers() for history " + getPlacesManager().getLatestPlaceList().size());

            for (PlaceEntry placeEntry : getPlacesManager().getLatestPlaceList()) {
                googleMap.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(placeEntry.getLatitude(), placeEntry.getLongitude()))
                                .title(placeEntry.getName())
                                .snippet(String.valueOf(placeEntry.getDistanceFromUser() + " m"))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_places)));
            }
        }
    }

    private void drawEventMarkers() {

        if (eventList != null && eventList.size() != 0) {
            Log.d(TAG, "drawEventMarkers() for = [" + eventList.size() + "]");

            for (Event event : eventList) {
                googleMap.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(event.getLat(), event.getLon()))
                                .title(event.getTitle())
                                .snippet(event.getDescription())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_event)));
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
                    TextView activityText = (TextView) view.findViewById(R.id.marker_people_activity);
                    TextView distanceText = (TextView) view.findViewById(R.id.marker_people_distance);

                    activityText.setText(personEntry.getActivityString());
                    String distanceString = String.valueOf(personEntry.getDistanceFromUser()) + " m";
                    distanceText.setText(distanceString);

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

    private void filterDialog() {
        Log.d(TAG, "filterDialog()");

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.filter_dialog_layout);

        final CheckBox peopleCheckBox = (CheckBox) dialog.findViewById(R.id.people_check_box);
        final CheckBox placesCheckBox = (CheckBox) dialog.findViewById(R.id.places_check_box);
        final CheckBox eventsCheckBox = (CheckBox) dialog.findViewById(R.id.events_check_box);
        TextView okText = (TextView) dialog.findViewById(R.id.filter_dialog_ok);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("markers", Context.MODE_PRIVATE);
        peopleCheckBox.setChecked(sharedPreferences.getBoolean("people_markers", true));
        placesCheckBox.setChecked(sharedPreferences.getBoolean("places_markers", true));
        eventsCheckBox.setChecked(sharedPreferences.getBoolean("events_markers", true));

        okText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("markers", Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean("people_markers", peopleCheckBox.isChecked()).apply();
                sharedPreferences.edit().putBoolean("places_markers", placesCheckBox.isChecked()).apply();
                sharedPreferences.edit().putBoolean("events_markers", eventsCheckBox.isChecked()).apply();

                drawMarkers();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void downloadEventMarkers() {
        Log.d(TAG, "downloadEventMarkers()");

        GenericConverter<Event> eventGenericConverter = new GenericConverter<>(Config.RestAPI, Event.class);

        String authToken = LoginManager.getTokenFromShared(getActivity());

        RestAPI restAPI = eventGenericConverter.getRestAdapter().create(RestAPI.class);

        restAPI.getEvents(authToken, MainActivity.currentPosition.latitude, MainActivity.currentPosition.longitude, 10, 24)
                .subscribe(new Subscriber<List<Event>>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted() called with: " + "");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError() called with: " + "e = [" + e.getMessage() + "]");

                        if (e instanceof RetrofitError) {
                            Log.e(TAG, "onError() called with: " + "e = [" + ((RetrofitError) e).getUrl() + "]");
                        }

                    }

                    @Override
                    public void onNext(final List<Event> events) {
                        if (events != null) {
                            Log.d(TAG, "onNext() called with: " + "events size= [" + events.size() + "]");

                            eventList = events;
                        }
                    }
                });
    }
}
