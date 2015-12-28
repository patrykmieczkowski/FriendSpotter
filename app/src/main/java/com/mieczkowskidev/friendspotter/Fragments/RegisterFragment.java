package com.mieczkowskidev.friendspotter.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.mieczkowskidev.friendspotter.API.RestAPI;
import com.mieczkowskidev.friendspotter.Config;
import com.mieczkowskidev.friendspotter.LoginActivity;
import com.mieczkowskidev.friendspotter.Utils.LoginManager;
import com.mieczkowskidev.friendspotter.Objects.User;
import com.mieczkowskidev.friendspotter.Objects.UserLogin;
import com.mieczkowskidev.friendspotter.R;
import com.mieczkowskidev.friendspotter.Utils.GenericConverter;

import retrofit.RetrofitError;
import rx.Subscriber;

/**
 * Created by Patryk Mieczkowski on 2015-12-13
 */
public class RegisterFragment extends Fragment {

    private static final String TAG = RegisterFragment.class.getSimpleName();

    private Button registerButton;
    private EditText loginEditText, emailEditText, passwordEditText, rePasswordEditText;
    private ProgressBar registerProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        getViews(view);
        setListeners();
        return view;
    }

    private void getViews(View view) {

        loginEditText = (EditText) view.findViewById(R.id.login_edit_text_reg);
        emailEditText = (EditText) view.findViewById(R.id.email_edit_text_reg);
        passwordEditText = (EditText) view.findViewById(R.id.password_edit_text_reg);
        rePasswordEditText = (EditText) view.findViewById(R.id.re_password_edit_text_reg);

        registerButton = (Button) view.findViewById(R.id.register_button);
        registerProgressBar = (ProgressBar) view.findViewById(R.id.register_progress_bar);
    }

    private void setListeners() {

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerFlow();
            }
        });

        loginEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginEditText.setError(null);
            }
        });
        emailEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailEditText.setError(null);
            }
        });
        passwordEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordEditText.setError(null);
            }
        });
        rePasswordEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rePasswordEditText.setError(null);
            }
        });
    }

    private void registerFlow() {
        Log.d(TAG, "registerFlow()");

        getDataFromFormula();
    }

    private void getDataFromFormula() {
        Log.d(TAG, "getDataFromFormula()");

        boolean cancel = false;
        View focusView = null;

        if (LoginManager.getEditTextText(rePasswordEditText)) {
            Log.i(TAG, "Re password is empty");
            rePasswordEditText.setError(getString(R.string.formula_empty_error));
            cancel = true;
            focusView = rePasswordEditText;
        }

        if (LoginManager.getEditTextText(passwordEditText)) {
            Log.i(TAG, "Password is empty");
            passwordEditText.setError(getString(R.string.formula_empty_error));
            cancel = true;
            focusView = passwordEditText;
        }

        if (LoginManager.getEditTextText(emailEditText)) {
            Log.i(TAG, "Email is empty");
            emailEditText.setError(getString(R.string.formula_empty_error));
            cancel = true;
            focusView = emailEditText;
        }

        if (LoginManager.getEditTextText(loginEditText)) {
            Log.i(TAG, "Login is empty");
            loginEditText.setError(getString(R.string.formula_empty_error));
            cancel = true;
            focusView = loginEditText;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            Log.d(TAG, "check password match");
            View focusView2 = null;

            if (!passwordEditText.getText().toString().equals(rePasswordEditText.getText().toString())) {
                Log.i(TAG, "Different passwords");
                passwordEditText.setError(getString(R.string.formula_password_error));
                rePasswordEditText.setError(getString(R.string.formula_password_error));
                cancel = true;
                focusView2 = passwordEditText;
            }

            if (!LoginManager.isPasswordValid(passwordEditText.getText().toString())) {
                Log.i(TAG, "Password to short");
                passwordEditText.setError("Password to short");
                cancel = true;
                focusView2 = passwordEditText;
            }

            if (!LoginManager.isValidEmail(emailEditText.getText().toString())) {
                Log.i(TAG, "Bad Email");
                emailEditText.setError("This is not a proper email address");
                cancel = true;
                focusView2 = emailEditText;
            }

            if (cancel) {
                focusView2.requestFocus();
            } else {
                Log.d(TAG, "formula is valid!");
                createUser();
            }
        }
    }

    private void createUser() {
        Log.d(TAG, "createUser()");

        String username = loginEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        UserLogin userLogin = new UserLogin(username, email, password);

        registerUserOnServer(userLogin);
    }

    private void startRegisterLoading() {

        if (registerButton.getVisibility() == View.VISIBLE) {
            registerButton.setVisibility(View.GONE);
        }
        if (registerProgressBar.getVisibility() == View.GONE) {
            registerProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void stopRegisterLoading() {

        if (registerButton.getVisibility() == View.GONE) {
            registerButton.setVisibility(View.VISIBLE);
        }
        if (registerProgressBar.getVisibility() == View.VISIBLE) {
            registerProgressBar.setVisibility(View.GONE);
        }
    }

    private void registerUserOnServer(UserLogin userLogin) {
        Log.d(TAG, "registerUserOnServer() " + userLogin.toString());

        UserLogin userLogin1 = new UserLogin();
        startRegisterLoading();

//        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Config.RestAPI).build();

        GenericConverter<User> userRegisterGenericConverter = new GenericConverter<>(Config.RestAPI, User.class);

        RestAPI restAPI = userRegisterGenericConverter.getRestAdapter().create(RestAPI.class);

//        RestAPI restAPI = restAdapter.create(RestAPI.class);

        restAPI.registerUser(userLogin)
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted() called with: " + "");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((LoginActivity) getActivity()).startLoginFragment();
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "error :( " + e.getMessage());

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stopRegisterLoading();
                            }
                        });

                        if (e instanceof RetrofitError) {
                            Log.e(TAG, "call1: " + ((RetrofitError) e).getUrl());
                            Log.e(TAG, "call2: " + ((RetrofitError) e).getResponse().getStatus());
                        }
                    }

                    @Override
                    public void onNext(User user) {
                        Log.d(TAG, "onNext() called with: " + "user = [" + user + "]");

                    }
                });
//        restAPI.registerUser(userLogin)
//                .doOnError(new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        Log.e(TAG, "error :( " + throwable.getMessage());
//
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                stopRegisterLoading();
//                            }
//                        });
//
//                        if (throwable instanceof RetrofitError) {
//                            Log.e(TAG, "call: " + ((RetrofitError) throwable).getUrl());
//                            Log.e(TAG, "call: " + ((RetrofitError) throwable).getBody().toString());
//
//                        }
//                    }
//                })
//                .subscribe(new Action1<User>() {
//                    @Override
//                    public void call(User user) {
//                        Log.d(TAG, "call() called with: " + "user = [" + user + "]");
//
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                stopRegisterLoading();
//                            }
//                        });
//                    }
//                });
    }

    private void showSnackbarInLoginActivity(String message) {

        ((LoginActivity) getActivity()).showSnackbar(message);

    }
}


