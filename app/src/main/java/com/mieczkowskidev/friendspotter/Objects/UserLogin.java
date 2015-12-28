package com.mieczkowskidev.friendspotter.Objects;

/**
 * Created by Patryk Mieczkowski on 2015-12-24
 */
public class UserLogin {

    private String username;
    private String email;
    private String password;
    private String gcmKey;

    public UserLogin() {
    }

    public UserLogin(String username, String email, String password, String gcmKey) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.gcmKey = gcmKey;
    }

    public UserLogin(String username, String password, String gcmKey) {
        this.username = username;
        this.password = password;
        this.gcmKey = gcmKey;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getGcmKey() {
        return gcmKey;
    }

    @Override
    public String toString() {
        return "UserLogin{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", gcmKey='" + gcmKey + '\'' +
                '}';
    }
}
