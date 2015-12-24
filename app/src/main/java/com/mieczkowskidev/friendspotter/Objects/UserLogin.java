package com.mieczkowskidev.friendspotter.Objects;

/**
 * Created by Patryk Mieczkowski on 2015-12-24
 */
public class UserLogin {

    private String username;
    private String email;
    private String password;

    public UserLogin() {
    }

    public UserLogin(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public UserLogin(String username, String password) {
        this.username = username;
        this.password = password;
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

    @Override
    public String toString() {
        return "UserLogin{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
