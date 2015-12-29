package com.mieczkowskidev.friendspotter.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mieczkowskidev.friendspotter.API.RestAPI;
import com.mieczkowskidev.friendspotter.API.RestClient;
import com.mieczkowskidev.friendspotter.Config;
import com.mieczkowskidev.friendspotter.MainActivity;
import com.mieczkowskidev.friendspotter.Objects.Event;
import com.mieczkowskidev.friendspotter.R;
import com.mieczkowskidev.friendspotter.Utils.LoginManager;
import com.trnql.smart.people.PersonEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedFile;
import rx.Subscriber;

/**
 * Created by Patryk Mieczkowski on 2015-12-27
 */
public class EventFragment extends Fragment {

    private static final String TAG = EventFragment.class.getSimpleName();
    private static final int REQUEST_TAKE_PHOTO = 1337;
    private TextView addressText;
    private ImageView eventPhoto;
    private EditText eventTitleEdit, eventDescriptionEdit;
    private String imagePath, eventAddress;
    private LatLng eventLatLng;
    private FloatingActionButton addButton;
    private CoordinatorLayout eventCoordinatorLayout;
    private boolean isPhotoMade = false;
    TypedFile typedFileImageSend;

    public static EventFragment newInstance() {
        EventFragment myFragment = new EventFragment();
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (appCompatActivity.getSupportActionBar() != null) {
            appCompatActivity.getSupportActionBar().setTitle(getString(R.string.drawer_events));
        }

        initViews(view);
        setListeners();
        prepareViews();

        return view;
    }

    private void initViews(View view) {

        addressText = (TextView) view.findViewById(R.id.event_address_text);
        eventPhoto = (ImageView) view.findViewById(R.id.event_photo);
        eventTitleEdit = (EditText) view.findViewById(R.id.event_title);
        eventDescriptionEdit = (EditText) view.findViewById(R.id.event_description);
        addButton = (FloatingActionButton) view.findViewById(R.id.event_add_floating_button);
        eventCoordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.event_coordinator_layout);

    }

    private void setListeners() {

        eventPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!isPhotoMade) {
//                    takePictureFromCamera();
//                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (isPhotoMade) {
                sendEvent();
//                }
            }
        });
    }

    private void prepareViews() {

        eventAddress = MainActivity.addressString;
        eventLatLng = MainActivity.currentPosition;

        addressText.setText(eventAddress);

        String titleHint = LoginManager.getUserUsername(getActivity()) + " Event";
        String descriptionHint = "Join and have fun!";
        eventTitleEdit.setHint(titleHint);
        eventDescriptionEdit.setHint(descriptionHint);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == -1) {
            Log.d(TAG, "onActivityResult() called with: " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
            Log.d(TAG, "onActivityResult() called with: " + imagePath);

            resizeAndCompressPhoto();
        }

    }

    private void takePictureFromCamera() {
        Log.d(TAG, "takePictureFromCamera()");

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "error occurred while creating photoFile");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: imagePath for use with ACTION_VIEW intents
        Log.d(TAG, "file:" + image.getAbsolutePath());
        imagePath = image.getAbsolutePath();
        return image;
    }

    private void resizeAndCompressPhoto() {

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        Bitmap b = BitmapFactory.decodeFile(imagePath);
        Bitmap out = Bitmap.createScaledBitmap(b, 300, 400, false);

        File file = new File(dir, "resize.png");

        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            out.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            b.recycle();
            out.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final File photoFiles = new File(file.getAbsolutePath());
        typedFileImageSend = new TypedFile("file:", photoFiles);

        Uri uri = Uri.fromFile(photoFiles);
        eventPhoto.setImageURI(uri);
        isPhotoMade = true;

    }

    private void sendEvent() {
        Log.d(TAG, "sendEvent() called with: " + "");

        final String authToken = LoginManager.getTokenFromShared(getActivity());
        List<String> members = new ArrayList<>();

        if (Config.personEntryList != null) {

            for (PersonEntry personEntry : Config.personEntryList) {
                members.add(personEntry.getUserToken());
            }
        }
        Log.d(TAG, "sendEvent(), members size: " + members.size());

        for (String username : members) {
            Log.d(TAG, "username: " + username);
        }

        RestClient restClient = new RestClient();

        RestAPI restAPI = restClient.getRestAdapter().create(RestAPI.class);

        String eventTile = eventTitleEdit.getText().toString();
        if (eventTile.equals("")) {
            eventTile = eventTitleEdit.getHint().toString();
        }

        String eventDescription = eventDescriptionEdit.getText().toString();
        if (eventDescription.equals("")) {
            eventDescription = eventDescriptionEdit.getHint().toString();
        }

        Event event = new Event(eventTile, eventAddress, eventDescription, eventLatLng.latitude, eventLatLng.longitude, members);

        restAPI.addEvent(authToken, event)
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted()");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showSnackbar("Successfully added an event!");
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError() called with: " + "e = [" + e.getMessage() + "]");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showSnackbar("Server error, please try again!");
                            }
                        });

                        if (e instanceof RetrofitError) {
                            Log.e(TAG, "onError() called with: " + "e = [" + ((RetrofitError) e).getUrl() + "]");
                            Log.e(TAG, "onError() called with: " + "e = [" + ((RetrofitError) e).getBody().toString() + "]");

                        }

                    }

                    @Override
                    public void onNext(Response response) {
                        Log.d(TAG, "onNext() called with: " + "response = [" + response.getBody().toString() + "]");

                    }
                });
    }


    public void showSnackbar(String message) {

        Snackbar snackbar = Snackbar
                .make(eventCoordinatorLayout, message, Snackbar.LENGTH_LONG);

        snackbar.show();
    }
}
