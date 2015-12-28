package com.mieczkowskidev.friendspotter.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.mieczkowskidev.friendspotter.R;

import java.io.IOException;

/**
 * Created by Patryk Mieczkowski on 2015-12-28
 */
public class GCMManager {

    public static final String TAG = GCMManager.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private GoogleCloudMessaging gcm;
    private String registrationId;
    private String projectNumber;
    private Activity activity;

    public static abstract class RegistrationCompletedHandler {
        public abstract void onSuccess(String registrationId, boolean isNewRegistration);

        public void onFailure(String ex) {
            Log.e(TAG, "Registration Handler error: " + ex);
        }
    }

    public GCMManager(Activity activity, String projectNumber) {
        this.activity = activity;
        this.projectNumber = projectNumber;
        this.gcm = GoogleCloudMessaging.getInstance(activity);
    }

    // Register if needed or fetch from local store
    public void registerIfNeeded(final RegistrationCompletedHandler handler) {
        if (checkPlayServices()) {
            registrationId = getRegistrationId(getContext());
            Log.d(TAG, "registrationID: " + registrationId);

            if (registrationId.isEmpty()) {
                registerInBackground(handler);
            } else { // got id from cache
                handler.onSuccess(registrationId, false);
            }
        } else { // no play services
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground(final RegistrationCompletedHandler handler) {
        Log.d(TAG, "no registration found, starting...");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getContext());
                    }
                    InstanceID instanceID = InstanceID.getInstance(getContext());
                    registrationId = instanceID.getToken(projectNumber, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    Log.i(TAG, registrationId);

                    // Persist the regID - no need to register again.
                    storeRegistrationId(getContext(), registrationId);

                } catch (IOException ex) {
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                    handler.onFailure("Error: " + ex.getMessage() + " , cause: " + ex.getCause());
                }
                return registrationId;
            }

            @Override
            protected void onPostExecute(String regId) {
                if (regId != null) {
                    handler.onSuccess(regId, true);
                }
            }
        }.execute(null, null, null);
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(context.getString(R.string.shared_preferences_gcm_registration_id), "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(context.getString(R.string.shared_preferences_gcm_app_version), Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "application version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.shared_preferences_gcm_registration_id), regId);
        editor.putInt(context.getString(R.string.shared_preferences_gcm_app_version), appVersion);
        editor.apply();
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getContext().getSharedPreferences(context.getString(R.string.shared_preferences_gcm_key), Context.MODE_PRIVATE);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    private Context getContext() {
        return activity;
    }

    private Activity getActivity() {
        return activity;
    }
}

