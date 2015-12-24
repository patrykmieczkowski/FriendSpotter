package com.mieczkowskidev.friendspotter.Objects;

/**
 * Created by Patryk Mieczkowski on 2015-12-24
 */
public class User {

    private String username;
    private String AuthToken;
    private String image;

    public User() {
    }

    public User(String username, String authToken, String image) {
        this.username = username;
        AuthToken = authToken;
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthToken() {
        return AuthToken;
    }

    public void setAuthToken(String authToken) {
        AuthToken = authToken;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", AuthToken='" + AuthToken + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
