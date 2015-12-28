package com.mieczkowskidev.friendspotter.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mieczkowskidev.friendspotter.Adapters.EventHistoryAdapter;
import com.mieczkowskidev.friendspotter.MainActivity;
import com.mieczkowskidev.friendspotter.R;
import com.mieczkowskidev.friendspotter.Utils.LoginManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit.mime.TypedFile;

/**
 * Created by Patryk Mieczkowski on 2015-12-27
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private TextView usernameText, eventCounterText, noHistoryFoundText;
    private ImageView profileImage;
    private RecyclerView eventHistoryRecycler;
    private ProgressBar progressBar;

    public static ProfileFragment newInstance() {
        ProfileFragment myFragment = new ProfileFragment();
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (appCompatActivity.getSupportActionBar() != null) {
            appCompatActivity.getSupportActionBar().setTitle(getString(R.string.drawer_profile));
        }

        initViews(view);
        setListeners();
        prepareView();

        return view;
    }

    private void initViews(View view) {

        usernameText = (TextView) view.findViewById(R.id.profile_username_text);
        profileImage = (ImageView) view.findViewById(R.id.profile_image);
        eventHistoryRecycler = (RecyclerView) view.findViewById(R.id.event_history_recycler);
        progressBar = (ProgressBar) view.findViewById(R.id.profile_event_list_progress);
        eventCounterText = (TextView) view.findViewById(R.id.event_counter_text);
        noHistoryFoundText = (TextView) view.findViewById(R.id.profile_no_event_history_found);

    }

    private void setListeners() {

    }

    private void prepareView() {

        usernameText.setText(LoginManager.getUserUsername(getActivity()));

        Picasso.with(getActivity()).load(LoginManager.getUserImageUrl(getActivity())).into(profileImage);

        setEventHistoryRecycler();
    }

    private void setEventHistoryRecycler() {

        List<String> stringList = new ArrayList<>();
        stringList.add("Fabryka");
        stringList.add("Fabryka Egodrop");
        stringList.add("Studio Uber Party");
        stringList.add("Dzikosc Serc");
        stringList.add("Anakonda");
        stringList.add("Kiedy bylem malym chlopcem hej");
        stringList.add("Everybody dance now aaaaaaaaaaaaaaaaaaaaaaaaaaaa aaa");


        if (stringList != null && stringList.size() != 0) {

            String eventSize = String.valueOf(stringList.size());
            eventCounterText.setText(eventSize);

            progressBarAction(false);

            eventHistoryRecycler.setVisibility(View.VISIBLE);

            EventHistoryAdapter eventHistoryAdapter = new EventHistoryAdapter(getActivity(), stringList);
            eventHistoryRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
            eventHistoryRecycler.setAdapter(eventHistoryAdapter);
        } else {
            progressBarAction(false);
            noHistoryFoundText.setVisibility(View.VISIBLE);
        }
    }

    private void progressBarAction(boolean action) {

        progressBar.setVisibility(action ? View.VISIBLE : View.GONE);

    }
}
