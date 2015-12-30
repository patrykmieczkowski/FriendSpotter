package com.mieczkowskidev.friendspotter.Fragments;

import android.app.Fragment;
import android.os.Bundle;
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

import com.mieczkowskidev.friendspotter.API.RestAPI;
import com.mieczkowskidev.friendspotter.Adapters.EventHistoryAdapter;
import com.mieczkowskidev.friendspotter.Config;
import com.mieczkowskidev.friendspotter.Objects.Event;
import com.mieczkowskidev.friendspotter.Objects.MyProfile;
import com.mieczkowskidev.friendspotter.R;
import com.mieczkowskidev.friendspotter.Utils.GenericConverter;
import com.mieczkowskidev.friendspotter.Utils.LoginManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import rx.Subscriber;

/**
 * Created by Patryk Mieczkowski on 2015-12-27
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private TextView usernameText, eventCounterText, noHistoryFoundText, emailText;
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
        emailText = (TextView) view.findViewById(R.id.profile_email_text);

    }

    private void setListeners() {

    }

    private void prepareView() {

        usernameText.setText(LoginManager.getUserUsername(getActivity()));
        emailText.setText(LoginManager.getUserLoginUsername(getActivity()));

        Picasso.with(getActivity()).load(LoginManager.getUserImageUrl(getActivity())).into(profileImage);

        downloadProfileData();
    }

    private void downloadProfileData() {
        Log.d(TAG, "downloadProfileData() called with: " + "");

        GenericConverter<MyProfile> eventGenericConverter = new GenericConverter<>(Config.RestAPI, MyProfile.class);

        String authToken = LoginManager.getTokenFromShared(getActivity());

        RestAPI restAPI = eventGenericConverter.getRestAdapter().create(RestAPI.class);

        restAPI.getMyProfile(authToken)
                .subscribe(new Subscriber<MyProfile>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted() called with: " + "");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError() called with: " + "e = [" + e.getMessage() + "]");

                    }

                    @Override
                    public void onNext(final MyProfile myProfile) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setEventHistoryRecycler(myProfile.getEvents());
                            }
                        });

                    }
                });
    }

    private void setEventHistoryRecycler(List<Event> eventList) {

//        List<String> stringList = new ArrayList<>();
//        stringList.add("Fabryka");
//        stringList.add("Fabryka Egodrop");
//        stringList.add("Studio Uber Party");
//        stringList.add("Dzikosc Serc");
//        stringList.add("Anakonda");
//        stringList.add("Kiedy bylem malym chlopcem hej");
//        stringList.add("Everybody dance now aaaaaaaaaaaaaaaaaaaaaaaaaaaa aaa");


        if (eventList != null && eventList.size() != 0) {

            String eventSize = String.valueOf(eventList.size());
            eventCounterText.setText(eventSize);

            progressBarAction(false);

            eventHistoryRecycler.setVisibility(View.VISIBLE);

            EventHistoryAdapter eventHistoryAdapter = new EventHistoryAdapter(getActivity(), eventList);
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
