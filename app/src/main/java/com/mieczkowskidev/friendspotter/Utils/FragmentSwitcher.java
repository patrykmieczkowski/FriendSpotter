package com.mieczkowskidev.friendspotter.Utils;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.trnql.smart.base.SmartCompatActivity;

/**
 * Created by Patryk Mieczkowski on 26.12.15
 */
public class FragmentSwitcher {

    public static void switchToFragment(AppCompatActivity activity, Fragment fragment, int placeHolderResource) {

        if(activity.getApplicationContext() != null && fragment != null) {

            activity.getFragmentManager()
                    .beginTransaction()
                    .replace(placeHolderResource, fragment)
                    .commit();
        }

    }
}
