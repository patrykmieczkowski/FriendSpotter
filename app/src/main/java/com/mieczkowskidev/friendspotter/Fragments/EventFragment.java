package com.mieczkowskidev.friendspotter.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
public class EventFragment extends Fragment {

    private static final String TAG = EventFragment.class.getSimpleName();
    private static final int REQUEST_TAKE_PHOTO = 1337;
    private TextView addressText;
    private ImageView eventPhoto;
    private String imagePath;


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

    }

    private void setListeners() {

        eventPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictureFromCamera();
            }
        });
    }

    private void prepareViews() {

        addressText.setText(MainActivity.addressString);
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
        TypedFile photoo = new TypedFile("file:", photoFiles);

        Uri uri = Uri.fromFile(photoFiles);
        eventPhoto.setImageURI(uri);

    }
}
