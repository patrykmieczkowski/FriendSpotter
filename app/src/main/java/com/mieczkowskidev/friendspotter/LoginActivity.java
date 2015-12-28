package com.mieczkowskidev.friendspotter;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mieczkowskidev.friendspotter.Fragments.LoginFragment;
import com.mieczkowskidev.friendspotter.Fragments.RegisterFragment;
import com.mieczkowskidev.friendspotter.gcm.GCMManager;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final int LOGIN = 0;
    private static final int REGISTER = 1;
    //set starting mode to LOGIN
    private static int MODE = LOGIN;

    private static final int VISIBLE = 0;
    private static final int GONE = 1;

    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getViews();
        startLoginFragment();
        registerPushNotificationGcm();

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

//        if (!isOnline()) {
//            showAlertNoInternet();
//        }

        if (isOnline() && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertNoGPS();
        }

    }

    @Override
    public void onBackPressed() {

        if (MODE == REGISTER) {
            MODE = LOGIN;
            startLoginFragment();
        } else {
            super.onBackPressed();
        }
    }

    private void getViews() {

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.login_coordinator_layout);
    }

    public void showSnackbar(String message) {

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    private void changeVisibility(View view, int visibility) {

        switch (visibility) {
            case VISIBLE:
                if (view.getVisibility() == View.GONE) {
                    view.setVisibility(View.VISIBLE);
                }
                break;
            case GONE:
                if (view.getVisibility() == View.VISIBLE) {
                    view.setVisibility(View.GONE);
                }
                break;
        }
    }

    public void startRegisterFragment() {
        MODE = REGISTER;

        RegisterFragment fragment = new RegisterFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.login_placeholder, fragment);
//        transaction.setCustomAnimations(R.anim.abc_shrink_fade_out_from_bottom, R.anim.abc_grow_fade_in_from_bottom);

        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void startLoginFragment() {
        MODE = LOGIN;

        LoginFragment fragment = new LoginFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.login_placeholder, fragment);
//        transaction.setCustomAnimations(R.anim.fab_in, R.anim.fab_out);

        transaction.addToBackStack(null);

        transaction.commit();

    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void showAlertNoInternet() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please turn on internet connection and try again!")
                .setCancelable(false)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAlertNoGPS() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void hideKeyboard() {
        Log.d(TAG, "hideKeyboard()");

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void registerPushNotificationGcm() {

        GCMManager gcmManager = new GCMManager(this, Config.GCM_SENDER_ID);
        gcmManager.registerIfNeeded(new GCMManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {
                Log.d(TAG, "onSuccess() called with: " + "registrationId = [" + registrationId + "], isNewRegistration = [" + isNewRegistration + "]");
            }
        });
    }
}