package com.mieczkowskidev.friendspotter.Objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patryk Mieczkowski on 2015-12-28
 */
public class Event {

    private String title;
    private String location;
    private String description;
    private Double lat;
    private Double lon;
    private String image;
    private String thumbnail;
    private String created;
    private List<String> members = new ArrayList<>();

    public Event(String title, String location, String description, Double lat, Double lon, List<String> members) {
        this.title = title;
        this.location = location;
        this.description = description;
        this.lat = lat;
        this.lon = lon;
        this.members = members;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", members=" + members +
                '}';
    }
}
