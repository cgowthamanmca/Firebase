package com.example.tartlabs.facebooklearn.model;

public class Snap {
    String key;
    boolean value;

    public  Snap(){

    }
    public Snap(String key, boolean value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public boolean getValue() {
        return value;
    }
}
