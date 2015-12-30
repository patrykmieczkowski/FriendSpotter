package com.mieczkowskidev.friendspotter.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mieczkowskidev.friendspotter.Adapters.EventHistoryAdapter;
import com.mieczkowskidev.friendspotter.MainActivity;
import com.mieczkowskidev.friendspotter.R;
import com.mieczkowskidev.friendspotter.Utils.LoginManager;
import com.squareup.picasso.Picasso;
import com.trnql.smart.base.SmartFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patryk Mieczkowski on 2015-12-27
 */
public class SettingsFragment extends SmartFragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();
    private SeekBar seekBar;
    private TextView seekBarValueText;
    private String globalProgress;
    private int searchRadius;
    private FloatingActionButton logoutButton;

    public static SettingsFragment newInstance() {
        SettingsFragment myFragment = new SettingsFragment();
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (appCompatActivity.getSupportActionBar() != null) {
            appCompatActivity.getSupportActionBar().setTitle(getString(R.string.drawer_settings));
        }

        initViews(view);
        setListeners();
        prepareView();

        return view;
    }

    private void initViews(View view) {

        seekBar = (SeekBar) view.findViewById(R.id.settings_seek_bar);
        seekBarValueText = (TextView) view.findViewById(R.id.seetings_seek_value);
        logoutButton = (FloatingActionButton) view.findViewById(R.id.logout_floating_button);
    }

    private void setListeners() {

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

    }

    private void prepareView() {

        searchRadius = getPeopleManager().getSearchRadius();
        seekBar.setProgress(searchRadius + 200);
        globalProgress = String.valueOf(searchRadius) + " m";
        seekBarValueText.setText(globalProgress);

        Log.d(TAG, "SmartPeople Data for users within " + searchRadius + " meters\n");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                globalProgress = String.valueOf(progress + 200) + " m";
                searchRadius = progress + 200;

                seekBarValueText.setText(globalProgress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch() called with progress: " + searchRadius);

                getPeopleManager().setSearchRadius(searchRadius);

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("people_api", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("seek_radius", searchRadius).apply();

    }

    private void logout() {

        getActivity().getSharedPreferences("login", Context.MODE_PRIVATE).edit().putBoolean("is_user_logged", false).apply();

        ((MainActivity) getActivity()).startLoginActivity();
    }
}
