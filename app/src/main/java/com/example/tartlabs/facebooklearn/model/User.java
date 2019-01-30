package com.example.tartlabs.facebooklearn.model;

import java.io.Serializable;
import java.util.HashMap;

public class User implements Serializable {
    private String email;
    private String name;
    private String profile_image;
    private String user_id;
    private HashMap<String, Object> feed;

    public HashMap<String, Object> getFeed() {
        return feed;
    }

    public void setFeed(HashMap<String, Object> feed) {
        this.feed = feed;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}
