package com.jaison.app_android.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.jaison.app_android.BaseActivity;
import com.jaison.app_android.Hasura;
import com.jaison.app_android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthActivity extends BaseActivity {


    public static void startActivity(Activity startingActivity) {
        startingActivity.startActivity(new Intent(startingActivity, AuthActivity.class));
    }

    EditText username, password;
    LinearLayout loginLayout, logoutLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        loginLayout = findViewById(R.id.loginLayout);
        logoutLayout = findViewById(R.id.logoutLayout);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogout();
            }
        });

        findViewById(R.id.signInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkIfFormIsValid()) {
                    performAuthentication(username.getText().toString(), password.getText().toString(), false);
                }
            }
        });

        findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkIfFormIsValid()) {
                    performAuthentication(username.getText().toString(), password.getText().toString(), true);
                }
            }
        });

        refreshLayout();
    }

    //if logged in, show logout layout
    private void refreshLayout() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logoutLayout.setVisibility(View.GONE);
                loginLayout.setVisibility(View.VISIBLE);
                if (Hasura.getSessionToken(AuthActivity.this) != null) {
                    loginLayout.setVisibility(View.GONE);
                    logoutLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private Boolean checkIfFormIsValid() {
        if (username.getText().toString().isEmpty()) {
            showToast("Username cannot be left empty");
            return false;
        }
        if (password.getText().toString().isEmpty()) {
            showToast("Password cannot be left empty");
            return false;
        }
        return true;
    }

    private void performLogout() {
        showProgressDialog("Logging out...");
        String url = Hasura.Config.AUTH_URL + "user/logout";
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        //Username Provider
        JSONObject jsonObject = new JSONObject();
        RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                //Add authorization header as only authenticated users can logout
                .addHeader("Authorization", "Bearer " + Hasura.getSessionToken(AuthActivity.this))
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                dismissProgressDialog();
                //Handle failure
                showToast("Logout Failed: " + e.getLocalizedMessage());

            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                dismissProgressDialog();
                //Handle success
                String jsonString = response.body().string();
                if (response.isSuccessful()) {
                    showToast("Logout successful");
                    //Delete the saved response
                    Hasura.saveSessionToken(null, AuthActivity.this);
                    refreshLayout();
                } else {
                    showToast("Logout Failed: " + jsonString);
                }
            }
        });
    }

    private void performAuthentication(String username, String password, boolean signUp) {
        String url = Hasura.Config.AUTH_URL + (signUp ? "signup" : "login");
        showProgressDialog("Please wait");
        try {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            //Username Provider
            JSONObject jsonObject = new JSONObject()
                    .put("provider", "username")
                    .put("data", new JSONObject()
                            .put("username", username)
                            .put("password", password)
                    );

            RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    dismissProgressDialog();
                    //Handle failure
                    showToast("Authentication failed: " + e.getLocalizedMessage());
                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    dismissProgressDialog();
                    //Handle success
                    String jsonString = response.body().string();
                    if (response.isSuccessful()) {
                        AuthResponse authResponse = new Gson().fromJson(jsonString, AuthResponse.class);
                        //Save the response offline to be used later
                        Hasura.saveSessionToken(authResponse.authToken, AuthActivity.this);
                        showToast("Authenticated successfully!");
                        refreshLayout();
                    } else {
                        showToast("Authentication failed: " + jsonString);
                    }
                }
            });

        } catch (JSONException e) {
            dismissProgressDialog();
            e.printStackTrace();
            showToast("Authentication failed: " + e.getLocalizedMessage());
        }
    }
}
