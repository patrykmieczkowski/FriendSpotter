package com.mieczkowskidev.friendspotter.Objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patryk Mieczkowski on 2015-12-30
 */
public class MyProfile {

    String username;
    String email;
    String image;
    String thumbnail;

    List<Event> events = new ArrayList<>();

    public MyProfile() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
