package com.example.techlearn.Model;

public class CommentModel {
    private String userId;
    private String userName;
    private String comment;
    private Double rating;  // Changed from String to Double

    // Default constructor required for Firebase
    public CommentModel() {
    }

    // Constructor
    public CommentModel(String userId, String userName, String comment, Double rating) {
        this.userId = userId;
        this.userName = userName;
        this.comment = comment;
        this.rating = rating;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName != null ? userName : "Anonymous User";
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment != null ? comment : "No Comment";
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getRating() {
        return rating != null ? rating : 0.0;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
