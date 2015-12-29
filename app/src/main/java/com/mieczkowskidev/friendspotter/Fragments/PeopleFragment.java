package com.mieczkowskidev.friendspotter.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mieczkowskidev.friendspotter.Adapters.EventHistoryAdapter;
import com.mieczkowskidev.friendspotter.Adapters.PeopleListAdapter;
import com.mieczkowskidev.friendspotter.Config;
import com.mieczkowskidev.friendspotter.R;
import com.trnql.smart.people.PersonEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patryk Mieczkowski on 2015-12-27
 */
public class PeopleFragment extends Fragment {

    private static final String TAG = PeopleFragment.class.getSimpleName();
    private TextView noPeopleFoundText;
    private RecyclerView peopleRecycler;
    private ProgressBar progressBar;

    public static PeopleFragment newInstance() {
        PeopleFragment myFragment = new PeopleFragment();
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (appCompatActivity.getSupportActionBar() != null) {
            appCompatActivity.getSupportActionBar().setTitle(getString(R.string.drawer_people));
        }

        initViews(view);
        prepareView();

        return view;
    }

    private void initViews(View view) {

        progressBar = (ProgressBar) view.findViewById(R.id.people_progress_bar);
        noPeopleFoundText = (TextView) view.findViewById(R.id.no_people_found_text);
        peopleRecycler = (RecyclerView) view.findViewById(R.id.people_recycler);

    }

    private void prepareView() {

        setPeopleRecyclerView();
    }

    private void setPeopleRecyclerView() {

        List<PersonEntry> personEntryList = Config.personEntryList;

        if (personEntryList != null && personEntryList.size() != 0) {

            progressBarAction(false);

            peopleRecycler.setVisibility(View.VISIBLE);

            PeopleListAdapter peopleListAdapter = new PeopleListAdapter(getActivity(), personEntryList);
            peopleRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
            peopleRecycler.setAdapter(peopleListAdapter);
        } else {
            progressBarAction(false);
            noPeopleFoundText.setVisibility(View.VISIBLE);
        }
    }

    private void progressBarAction(boolean action) {

        progressBar.setVisibility(action ? View.VISIBLE : View.GONE);

    }
}
