package com.example.techlearn.Model;

public class PlayListModel {

    private String title, videoUrl, enabled, key;

    public PlayListModel() {
    }

    public PlayListModel(String title, String key, String enabled, String videoUrl) {
        this.title = title;
        this.key = key;
        this.enabled = enabled;
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
