package com.jaison.app_android.auth;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jaison on 06/11/17.
 */

public class AuthResponse {

    @SerializedName("hasura_id")
    int hasuraId;

    @SerializedName("auth_token")
    String authToken;

    @SerializedName("roles")
    String[] roles;

    public int getHasuraId() {
        return hasuraId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String[] getRoles() {
        return roles;
    }
}
