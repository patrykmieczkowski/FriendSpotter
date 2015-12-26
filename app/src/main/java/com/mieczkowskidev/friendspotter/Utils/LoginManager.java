package com.mieczkowskidev.friendspotter.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

import com.mieczkowskidev.friendspotter.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Patryk Mieczkowski on 2015-12-07
 */
public class LoginManager {

    public static boolean getEditTextText(EditText editText) {

        return editText.getText().toString().trim().equalsIgnoreCase("");
    }

    public static boolean isPasswordValid(String password) {

        return password.length() > 5;
    }

    public static boolean isValidEmail(String enteredEmail) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(enteredEmail);
        return matcher.matches();
    }

    public static String getTokenFromShared(Context context){

        return "Token " + context.getSharedPreferences
                (context.getString(R.string.shared_preferences_user), Context.MODE_PRIVATE)
                .getString("token", "");
    }
}
