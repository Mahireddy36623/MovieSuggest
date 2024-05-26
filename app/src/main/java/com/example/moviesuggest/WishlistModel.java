package com.example.moviesuggest;

public class WishlistModel {
    private String userId;
    private String movieId;

    public WishlistModel() {
    }

    public WishlistModel(String userId, String movieId) {
        this.userId = userId;
        this.movieId = movieId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }
}
