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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mieczkowskidev.friendspotter.MainActivity;
import com.mieczkowskidev.friendspotter.R;
import com.mieczkowskidev.friendspotter.Utils.LoginManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit.mime.TypedFile;

/**
 * Created by Patryk Mieczkowski on 2015-12-27
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private TextView usernameText;
    private ImageView profileImage;

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

    }

    private void setListeners() {

    }

    private void prepareView() {

        usernameText.setText(LoginManager.getUserUsername(getActivity()));

        Picasso.with(getActivity()).load(LoginManager.getUserImageUrl(getActivity())).into(profileImage);
    }
}
