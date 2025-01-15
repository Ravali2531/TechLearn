package com.example.techlearn.Model;

import java.util.Map;

public class CourseModel {

    private String title, duration,  description;
    private double rating;
    private long price;

    private String thumbnail, introVideo,postId, postedBy, enable;


    public CourseModel() {
    }

    public CourseModel(String title, String duration, double rating, String description, long price, String thumbnail, String introVideo, String postedBy, String enable) {
        this.title = title;
        this.duration = duration;
        this.rating = rating;
        this.description = description;
        this.price = price;
        this.thumbnail = thumbnail;
        this.introVideo = introVideo;
        this.postedBy = postedBy;
        this.enable = enable;
    }

    public CourseModel(String title, String description, long price, String duration) {

        this.title = title;
        this.duration = duration;
        this.description = description;
        this.price = price;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public double getRating() {
        return rating;
    }

//    public void setRating(double rating) {
//        this.rating = rating;
//    }
//
//    public void setRating(String rating) {
//        try {
//            this.rating = Double.parseDouble(rating);
//        } catch (NumberFormatException e) {
//            this.rating = 0.0;
//        }
//    }

    public void setRating(Object rating) {
        // Safely handle both String and Double values
        if (rating instanceof String) {
            try {
                this.rating = Double.parseDouble((String) rating);
            } catch (NumberFormatException e) {
                this.rating = 0.0;
            }
        } else if (rating instanceof Double) {
            this.rating = (Double) rating;
        } else {
            this.rating = 0.0;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getIntroVideo() {
        return introVideo;
    }

    public void setIntroVideo(String introVideo) {
        this.introVideo = introVideo;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

}
