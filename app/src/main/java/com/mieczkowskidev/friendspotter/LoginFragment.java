package com.mieczkowskidev.friendspotter;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mieczkowskidev.friendspotter.Objects.UserLogin;

/**
 * Created by Patryk Mieczkowski on 2015-12-07
 */
public class LoginFragment extends Fragment {
    private static final String TAG = LoginFragment.class.getSimpleName();

    private Button loginButton, registerButton;
    private EditText emailEditText, passwordEditText;
    private TextInputLayout emailInputLayout, passwordInputLayout;
    private ProgressBar loginProgressBar;
    private TextView titleText;
    private LoginActivity loginActivity;

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
        titleText = (TextView) view.findViewById(R.id.title_text_login);

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

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActivity.startRegisterFragment();
            }
        });

//        emailEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                emailInputLayout.setErrorEnabled(false);
//            }
//        });
//        passwordEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                passwordInputLayout.setErrorEnabled(false);
//            }
//        });

        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (LoginManager.isValidEmail(emailEditText.getText().toString())) {
                        Log.e(TAG, "gites, valid email");
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
                        Log.e(TAG, "gites, valid password");
                        passwordInputLayout.setErrorEnabled(false);
                    } else {
                        passwordInputLayout.setError("Password to short");
                    }
                } else {
                    passwordInputLayout.setErrorEnabled(false);
                }
            }
        });

//        emailEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (LoginManager.isValidEmail(s.toString())){
//                    Log.e(TAG, "gites, valid email");
//                    emailInputLayout.setErrorEnabled(false);
//                } else {
//                    emailInputLayout.setError("zly mail kolego");
//                }
//            }
//        });

    }

    private void loginFlow() {
        Log.d(TAG, "loginFlow()");

        loginActivity.hideKeyboard();
        emailEditText.clearFocus();
        passwordEditText.clearFocus();
        getDataFromFormula();
    }

//    private void getDataFromFormula() {
//        Log.d(TAG, "getDataFromFormula()");
//
//
//        loginUser();
//
//
//    }
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

        UserLogin userLogin = new UserLogin(username, password);

//        loginUserOnServer(user);
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

//    private void loginUserOnServer(User user) {
//        Log.d(TAG, "loginUserOnServer()");
//
//        startLoginLoading();
//
//        RestClient restClient = new RestClient();
//
//        ServerInterface serverInterface = restClient.getRestAdapter().create(ServerInterface.class);
//
//        serverInterface.loginUser(user, new Callback<JsonElement>() {
//            @Override
//            public void success(JsonElement jsonElement, Response response) {
//                Log.d(TAG, "loginUserOnServer success(): " + response.getStatus() + ", " + response.getReason());
//
//                Log.d(TAG, jsonElement.toString());
//                JsonObject mainObject = jsonElement.getAsJsonObject();
//                String status = mainObject.get("status").toString();
//                String token = mainObject.get("token").toString();
//                Log.d(TAG, "status: " + status + ", token: " + token);
//
//                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getActivity().getString(R.string.shared_preferences_user), Context.MODE_PRIVATE);
//                sharedPreferences.edit().putString("token", token.replaceAll("\"","")).apply();
//
//                ((LoginActivity) getActivity()).startMainActivity();
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                if (error.getKind() == RetrofitError.Kind.NETWORK || error.getResponse() == null) {
//                    Log.e(TAG, "error register with null");
//                    showSnackbarInLoginActivity(getString(R.string.connection_error));
//                } else {
//                    Log.e(TAG, "loginUserOnServer failure() called with: " + "error = [" + error.getUrl() + "]");
//                    String errorString = String.valueOf(error.getResponse().getStatus())
//                            + ", " + String.valueOf(error.getResponse().getReason());
//                    Log.e(TAG, "loginUserOnServer failure() called with: " + errorString);
//                    showSnackbarInLoginActivity(getString(R.string.connection_error));
//                }
//                stopLoginLoading();
//            }
//        });
//
//    }

    private void showSnackbarInLoginActivity(String message) {

        loginActivity.showSnackbar(message);

    }

}


