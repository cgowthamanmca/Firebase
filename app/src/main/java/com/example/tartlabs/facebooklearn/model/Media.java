package com.example.tartlabs.facebooklearn.model;

import android.net.Uri;

;

public class Media  {
    private String type;
    private String url;
    private Uri mediaUri;

    public Uri getMediaUri() {
        return mediaUri;
    }

    public void setMediaUri(Uri mediaUri) {
        this.mediaUri = mediaUri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
