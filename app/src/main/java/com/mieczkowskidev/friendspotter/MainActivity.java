package com.mieczkowskidev.friendspotter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.mieczkowskidev.friendspotter.API.RestAPI;
import com.mieczkowskidev.friendspotter.Fragments.EventFragment;
import com.mieczkowskidev.friendspotter.Fragments.PeopleFragment;
import com.mieczkowskidev.friendspotter.Fragments.ProfileFragment;
import com.mieczkowskidev.friendspotter.Fragments.SettingsFragment;
import com.mieczkowskidev.friendspotter.Fragments.SpotterFragment;
import com.mieczkowskidev.friendspotter.Objects.User;
import com.mieczkowskidev.friendspotter.Utils.FragmentSwitcher;
import com.mieczkowskidev.friendspotter.Utils.GenericConverter;
import com.mieczkowskidev.friendspotter.Utils.LoginManager;
import com.mieczkowskidev.friendspotter.Utils.WeatherManager;
import com.trnql.smart.base.SmartCompatActivity;
import com.trnql.smart.location.AddressEntry;
import com.trnql.smart.location.LocationEntry;
import com.trnql.smart.people.PersonEntry;
import com.trnql.smart.places.PlaceEntry;
import com.trnql.smart.weather.WeatherEntry;
import com.trnql.zen.core.AppData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit.RestAdapter;
import retrofit.client.Response;
import rx.Subscriber;

public class MainActivity extends SmartCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static String addressString;
    public static LatLng currentPosition;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private RelativeLayout drawerHeaderLayout;
    private TextView drawerWeatherCond, drawerTemp;
    private String temperature, weatherConditions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getViews();

        Log.d(TAG, "onCreate starting trnql services");
        getAppData().setApiKey("3bd5eb7e-64c7-4ff7-ad3b-f8e4ceb21e18");

        if (!LoginManager.getUserUsername(this).equals("")) {
            getPeopleManager().setUserToken(LoginManager.getUserUsername(this));
        } else {
            Random random = new Random();
            String username = "Anonymous" + String.valueOf(random.nextInt(1000 - 1) + 1);
            getPeopleManager().setUserToken(username);
        }
        getPeopleManager().setProductName("FSpotter");
//        getPeopleManager().setDataPayload("");
        AppData.startAllServices(this);

        int searchRadius = getPeopleManager().getSearchRadius();
        Log.d(TAG, "SmartPeople Data for users within " + searchRadius + " meters\n");

        prepareNavigationDrawer();

        showStartingFragment();
    }

    private void getViews() {

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerHeaderLayout = (RelativeLayout) findViewById(R.id.nav_drawer_header_layout);
        drawerWeatherCond = (TextView) findViewById(R.id.nav_drawer_weather_cond);
        drawerTemp = (TextView) findViewById(R.id.nav_drawer_weather_temp);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_spotter:
                Log.d(TAG, "onNavigationItemSelected: Spotter");
                FragmentSwitcher.switchToFragment(this, SpotterFragment.newInstance(), R.id.main_activity_placeholder);
                break;
            case R.id.nav_events:
                Log.d(TAG, "onNavigationItemSelected: Events");
                FragmentSwitcher.switchToFragment(this, EventFragment.newInstance(), R.id.main_activity_placeholder);
                break;
            case R.id.nav_people:
                Log.d(TAG, "onNavigationItemSelected: People");
                FragmentSwitcher.switchToFragment(this, PeopleFragment.newInstance(), R.id.main_activity_placeholder);
                break;
            case R.id.nav_profile:
                Log.d(TAG, "onNavigationItemSelected: Profile");
                FragmentSwitcher.switchToFragment(this, ProfileFragment.newInstance(), R.id.main_activity_placeholder);
                break;
            case R.id.nav_settings:
                Log.d(TAG, "onNavigationItemSelected: Settings");
                FragmentSwitcher.switchToFragment(this, SettingsFragment.newInstance(), R.id.main_activity_placeholder);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppData.stopAllServices(this);
    }


    @Override
    protected void smartAddressChange(AddressEntry address) {
        super.smartAddressChange(address);
        Log.d(TAG, "smartAddressChange() called with: " + "address = [" + address.toString() + "]");
        addressString = address.toString();
    }

    @Override
    protected void smartLatLngChange(LocationEntry location) {
        super.smartLatLngChange(location);
        Log.d(TAG, "smartLatLngChange() called with: " + "location = [" + location.getLatLng().toString() + "]");

        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    protected void smartWeatherChange(WeatherEntry weather) {
        super.smartWeatherChange(weather);
        Log.d(TAG, "smartWeatherChange() called with: " + "weather = [" + weather.getWeatherSummaryAsString() + "]");

        temperature = String.valueOf(weather.getCurrentTemp()) + "°C";
        weatherConditions = weather.getCurrentConditionsDescriptionAsString();
        drawerTemp.setText(temperature);
        drawerWeatherCond.setText(weatherConditions);
        drawerHeaderLayout.setBackgroundResource(WeatherManager.getDrawableForWeather(weatherConditions));
    }

//    @Override
//    protected void smartPeopleChange(List<PersonEntry> people) {
//        super.smartPeopleChange(people);
//
//        if (people != null) {
//
//            Log.d(TAG, "smartPeopleChange() called with: " + "people = [" + people.size() + "]");
//
//            if (personEntryList != null) {
//                personEntryList.clear();
//                personEntryList = people;
//            } else {
//                personEntryList = people;
//            }
//
//        }
//    }

//    @Override
//    protected void smartPlacesChange(List<PlaceEntry> places) {
//        super.smartPlacesChange(places);
//
//        if (places != null) {
//
//            Log.d(TAG, "smartPlacesChange() called with: " + "places = [" + places.size() + "]");
//
//            if (placeEntryList != null) {
//                placeEntryList.clear();
//                placeEntryList = places;
//            } else {
//                placeEntryList = places;
//            }
//
//        }
//
//    }

    private void prepareNavigationDrawer() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    private void showStartingFragment() {
        Log.d(TAG, "showStartingFragment()");

        FragmentSwitcher.switchToFragment(this, new SpotterFragment(), R.id.main_activity_placeholder);
    }

    public void startUserDetailsActivity() {

        Intent intent = new Intent(this, UserDetailsActivity.class);
        startActivity(intent);
    }
}
