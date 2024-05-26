package com.example.moviesuggest;

public class MoviesModel {
    private String movieId;

    private String name;
    private String imageURL;
    private String genre;
    private String description;
    private String director;
    private String starring1;
    private String starring2;
    private String musicDirector;
    private double imdbRating;

    // No-argument constructor required for Firebase
    public MoviesModel() {
    }

    // Parameterized constructor
    public MoviesModel(String movieId, String name, String imageURL, String genre, String description, String director, String starring1, String starring2, String musicDirector, double imdbRating) {
        this.movieId = movieId;
        this.name = name;
        this.imageURL = imageURL;
        this.genre = genre;
        this.description = description;
        this.director = director;
        this.starring1 = starring1;
        this.starring2 = starring2;
        this.musicDirector = musicDirector;
        this.imdbRating = imdbRating;
    }

    // Getters and Setters
    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getStarring1() {
        return starring1;
    }

    public void setStarring1(String starring1) {
        this.starring1 = starring1;
    }

    public String getStarring2() {
        return starring2;
    }

    public void setStarring2(String starring2) {
        this.starring2 = starring2;
    }

    public String getMusicDirector() {
        return musicDirector;
    }

    public void setMusicDirector(String musicDirector) {
        this.musicDirector = musicDirector;
    }

    public double getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(double imdbRating) {
        this.imdbRating = imdbRating;
    }
}
