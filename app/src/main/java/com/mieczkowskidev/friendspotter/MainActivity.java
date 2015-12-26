package com.mieczkowskidev.friendspotter;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.javadocmd.simplelatlng.util.LengthUnit;
import com.mieczkowskidev.friendspotter.API.RestAPI;
import com.mieczkowskidev.friendspotter.Fragments.SpotterFragment;
import com.mieczkowskidev.friendspotter.Objects.User;
import com.mieczkowskidev.friendspotter.Utils.FragmentSwitcher;
import com.mieczkowskidev.friendspotter.Utils.GenericConverter;
import com.trnql.smart.base.SmartCompatActivity;
import com.trnql.smart.location.AddressEntry;
import com.trnql.smart.people.PersonEntry;
import com.trnql.smart.places.PlaceEntry;
import com.trnql.smart.weather.WeatherEntry;
import com.trnql.zen.core.AppData;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.client.Response;
import rx.Subscriber;

public class MainActivity extends SmartCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d(TAG, "onCreate starting trnql services");
        getAppData().setApiKey("3bd5eb7e-64c7-4ff7-ad3b-f8e4ceb21e18");
        getPeopleManager().setUserToken("McPusz-200");
        getPeopleManager().setProductName("FSpotter");
        getPeopleManager().setDataPayload("Magdusz");
        AppData.startAllServices(this);
        int searchRadius = getPeopleManager().getSearchRadius();
        Log.d(TAG, "SmartPeople Data for users within " + searchRadius + " meters\n");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        showStartingFragment();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_spotter:
                Log.d(TAG, "onNavigationItemSelected: Spotter");
                FragmentSwitcher.switchToFragment(this, new SpotterFragment(), R.id.main_activity_placeholder);
                break;
            case R.id.nav_friends:
                Log.d(TAG, "onNavigationItemSelected: Friends");
                break;
            case R.id.nav_events:
                Log.d(TAG, "onNavigationItemSelected: Events");
                break;
            case R.id.nav_profile:
                Log.d(TAG, "onNavigationItemSelected: Profile");
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

    }

    @Override
    protected void smartWeatherChange(WeatherEntry weather) {
        super.smartWeatherChange(weather);
        Log.d(TAG, "smartWeatherChange() called with: " + "weather = [" + weather.getWeatherSummaryAsString() + "]");

    }

    @Override
    protected void smartPeopleChange(List<PersonEntry> people) {
        super.smartPeopleChange(people);
        
        if(people != null) {
            
            for (PersonEntry personEntry : people) {
                Log.d(TAG, "people: " + personEntry.getUserToken() + ", activity: "
                        + personEntry.getActivityString() + ", distance: " + personEntry.getDistanceFromUser()
                + "m, datapayload: " + personEntry.getDataPayload());
//            person.getUserToken();          // "2948574687"
//            person.getLatitude();           // 36.068821
//            person.getLongitude();          // -112.152823
//            person.getActivityString();     // "Running, Driving, etc."
//            person.getDistanceFromUser();   // 592 meters
//            person.getTimeStamp();          // Date object
//            person.getDataPayload();        // {"name":"Johnny", "status":"busy"} - Your own custom data!

            }
        } else {
            Log.d(TAG, "smartPeopleChange: people is null :(");
        }
    }

    @Override
    protected void smartPlacesChange(List<PlaceEntry> places) {
        super.smartPlacesChange(places);

        if (places != null){

            for (PlaceEntry placeEntry : places){

                Log.i(TAG, "place: " + placeEntry.getName() + ", address: " + placeEntry.getAddress()
                + ", hours: " + placeEntry.getHoursString());
            }
        }
    }

    private void testReq(){
        Log.d(TAG, "testReq()");

        GenericConverter<User> userLoginGenericConverter = new GenericConverter<>(Config.RestAPI, User.class);

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Config.RestAPI).build();
        RestAPI restAPI = restAdapter.create(RestAPI.class);

        restAPI.test()
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted() called with: " + "");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError() called with: " + "e = [" + e.getMessage() + "]");

                    }

                    @Override
                    public void onNext(Response response) {
                        Log.d(TAG, "onNext() called with: " + "response = [" + response.getBody().toString() + "]");

                    }
                });
    }

    private void showStartingFragment(){
        Log.d(TAG, "showStartingFragment()");

        FragmentSwitcher.switchToFragment(this, new SpotterFragment(), R.id.main_activity_placeholder);
    }
}
