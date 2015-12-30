package com.mieczkowskidev.friendspotter.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mieczkowskidev.friendspotter.API.RestAPI;
import com.mieczkowskidev.friendspotter.Config;
import com.mieczkowskidev.friendspotter.LoginActivity;
import com.mieczkowskidev.friendspotter.Utils.LoginManager;
import com.mieczkowskidev.friendspotter.Objects.User;
import com.mieczkowskidev.friendspotter.Objects.UserLogin;
import com.mieczkowskidev.friendspotter.R;
import com.mieczkowskidev.friendspotter.Utils.GenericConverter;
import com.mieczkowskidev.friendspotter.gcm.GCMManager;

import rx.functions.Action1;

/**
 * Created by Patryk Mieczkowski on 2015-12-07
 */
public class LoginFragment extends Fragment {
    private static final String TAG = LoginFragment.class.getSimpleName();

    private Button loginButton, registerButton;
    private EditText emailEditText, passwordEditText;
    private TextInputLayout emailInputLayout, passwordInputLayout;
    private ProgressBar loginProgressBar;
    private LoginActivity loginActivity;
    private ImageView logoImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginActivity = ((LoginActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        getViews(view);
        setListeners();

        if (LoginManager.getUserLoginStatus(getActivity())) {
            emailEditText.setText(LoginManager.getUserLoginUsername(getActivity()));
            passwordEditText.setText(LoginManager.getUserLoginPass(getActivity()));
        }

        return view;
    }

    private void getViews(View view) {

        emailEditText = (EditText) view.findViewById(R.id.email_edit_text);
        passwordEditText = (EditText) view.findViewById(R.id.password_edit_text);

        emailInputLayout = (TextInputLayout) view.findViewById(R.id.email_edit_text_input_layout);
        passwordInputLayout = (TextInputLayout) view.findViewById(R.id.password_edit_text_input_layout);

        registerButton = (Button) view.findViewById(R.id.register_fragment_button);
        loginButton = (Button) view.findViewById(R.id.login_button);

        loginProgressBar = (ProgressBar) view.findViewById(R.id.login_progress_bar);

        logoImage = (ImageView) view.findViewById(R.id.login_logo);

//        Typeface myTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/timeless_bold.ttf");
//        titleText.setTypeface(myTypeface);
//        titleText.setShadowLayer(1, 0, 0, Color.BLACK);
    }

    private void setListeners() {

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginFlow();
            }
        });

//        logoImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loginActivity.startMainActivity();
//            }
//        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActivity.startRegisterFragment();
            }
        });

        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (LoginManager.isValidEmail(emailEditText.getText().toString())) {
                        Log.d(TAG, "gites, valid email");
                        emailInputLayout.setErrorEnabled(false);
                    } else {
                        emailInputLayout.setError("This is not a proper email address");
                    }
                } else {
                    emailInputLayout.setErrorEnabled(false);
                }
            }
        });

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (LoginManager.isPasswordValid(passwordEditText.getText().toString())) {
                        Log.d(TAG, "gites, valid password");
                        passwordInputLayout.setErrorEnabled(false);
                    } else {
                        passwordInputLayout.setError("Password to short");
                    }
                } else {
                    passwordInputLayout.setErrorEnabled(false);
                }
            }
        });

    }

    private void loginFlow() {
        Log.d(TAG, "loginFlow()");

        loginActivity.hideKeyboard();
        emailEditText.clearFocus();
        passwordEditText.clearFocus();
        getDataFromFormula();
    }

    private void getDataFromFormula() {
        Log.d(TAG, "getDataFromFormula()");

        boolean cancel = false;
        View focusView = null;

        if (LoginManager.getEditTextText(passwordEditText)) {
            Log.i(TAG, "Password is empty");
            passwordInputLayout.setError(getString(R.string.formula_empty_error));
            cancel = true;
            focusView = passwordEditText;
        }

        if (LoginManager.getEditTextText(emailEditText)) {
            Log.i(TAG, "Email is empty");
            emailInputLayout.setError(getString(R.string.formula_empty_error));
            cancel = true;
            focusView = emailEditText;
        }

        if (cancel) {
            focusView.requestFocus();

        } else {
            Log.d(TAG, "check password match");
            View focusView2 = null;

            if (!LoginManager.isPasswordValid(passwordEditText.getText().toString())) {
                Log.i(TAG, "Password to short");
                passwordInputLayout.setError("Password to short");
                cancel = true;
                focusView2 = passwordEditText;
            }

            if (!LoginManager.isValidEmail(emailEditText.getText().toString())) {
                Log.i(TAG, "Bad Email");
                emailInputLayout.setError("This is not a proper email address");
                cancel = true;
                focusView2 = emailEditText;
            }

            if (cancel) {
                focusView2.requestFocus();
            } else {
                Log.d(TAG, "formula is valid!");
                loginUser();
            }
        }
    }

    private void loginUser() {
        Log.d(TAG, "loginUser()");

        String username = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        UserLogin userLogin = new UserLogin(username, password, GCMManager.getGcmKeyString(getActivity()));

        loginUserOnServer(userLogin);
    }

    private void startLoginLoading() {

        if (registerButton.getVisibility() == View.VISIBLE) {
            registerButton.setVisibility(View.GONE);
        }
        if (loginButton.getVisibility() == View.VISIBLE) {
            loginButton.setVisibility(View.GONE);
        }
        if (loginProgressBar.getVisibility() == View.GONE) {
            loginProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void stopLoginLoading() {

        if (registerButton.getVisibility() == View.GONE) {
            registerButton.setVisibility(View.VISIBLE);
        }
        if (loginButton.getVisibility() == View.GONE) {
            loginButton.setVisibility(View.VISIBLE);
        }
        if (loginProgressBar.getVisibility() == View.VISIBLE) {
            loginProgressBar.setVisibility(View.GONE);
        }
    }

    private void loginUserOnServer(final UserLogin userLogin) {
        Log.d(TAG, "loginUserOnServer() with " + userLogin.toString());

        startLoginLoading();

        GenericConverter<User> userLoginGenericConverter = new GenericConverter<>(Config.RestAPI, User.class);

        RestAPI restAPI = userLoginGenericConverter.getRestAdapter().create(RestAPI.class);

        restAPI.loginUser(userLogin)
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(final Throwable throwable) {
                        Log.e(TAG, "error :( " + throwable.getMessage());

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (throwable.getMessage().contains("401")) {
                                    ((LoginActivity) getActivity()).showSnackbar("Wrong authentication data!");
                                } else {
                                    ((LoginActivity) getActivity()).showSnackbar("Server error, please check connection and try again!");
                                }
                                stopLoginLoading();
                            }
                        });
                    }
                })
                .subscribe(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        Log.d(TAG, "call() called with: " + "user = [" + user.toString() + "]");

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(loginActivity.getString(R.string.shared_preferences_user), Context.MODE_PRIVATE);
                        sharedPreferences.edit().putString(Config.TOKEN, user.getAuthToken()).apply();
                        sharedPreferences.edit().putString(Config.IMAGE, user.getImage()).apply();
                        sharedPreferences.edit().putString(Config.USERNAME, user.getUsername()).apply();

                        LoginManager.saveDataToSharedPreferences(getActivity(), userLogin.getUsername(), userLogin.getPassword(), true);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loginActivity.startMainActivity();
                            }
                        });
                    }
                });

    }

    private void showSnackbarInLoginActivity(String message) {

        loginActivity.showSnackbar(message);

    }

}


