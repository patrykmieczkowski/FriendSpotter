package com.mieczkowskidev.friendspotter;

import android.widget.Toast;

import com.trnql.zen.core.AppData;

/**
 * Created by Patryk Mieczkowski on 26.12.15
 */
public class FSpotter extends AppData {

    @Override
    public void onCreate() {
        super.onCreate();
         Toast.makeText(this, "APPDATA OVERRIDDEN", Toast.LENGTH_LONG).show();
    }
}
