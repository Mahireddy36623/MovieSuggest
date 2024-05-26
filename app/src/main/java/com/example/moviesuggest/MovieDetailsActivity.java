package com.example.moviesuggest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView movieImage, wishlistButton;
    private TextView movieTitle, movieGenre, movieDescription, movieDirector, movieStarring1, movieStarring2, movieMusicDirector, movieImdbRating;
    private String movieId;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);

        movieImage = findViewById(R.id.movieImage);
        movieTitle = findViewById(R.id.movieTitle);
        movieGenre = findViewById(R.id.movieGenre);
        movieDescription = findViewById(R.id.movieDescription);
        movieDirector = findViewById(R.id.movieDirector);
        movieStarring1 = findViewById(R.id.movieStarring1);
        movieStarring2 = findViewById(R.id.movieStarring2);
        movieMusicDirector = findViewById(R.id.movieMusicDirector);
        movieImdbRating = findViewById(R.id.movieImdbRating);
        wishlistButton = findViewById(R.id.wishlistButton);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Get data from intent
        movieId = getIntent().getStringExtra("movieId");
        String imageUrl = getIntent().getStringExtra("imageURL");
        String title = getIntent().getStringExtra("title");
        String genre = getIntent().getStringExtra("genre");
        String description = getIntent().getStringExtra("description");
        String director = getIntent().getStringExtra("director");
        String starring1 = getIntent().getStringExtra("starring1");
        String starring2 = getIntent().getStringExtra("starring2");
        String musicDirector = getIntent().getStringExtra("musicDirector");
        double imdbRating = getIntent().getDoubleExtra("imdbRating", 0);

        // Set data to views
        Picasso.get().load(imageUrl).into(movieImage);
        movieTitle.setText(title);
        movieGenre.setText(genre);
        movieDescription.setText(description);
        movieDirector.setText(director);
        movieStarring1.setText(starring1);
        movieStarring2.setText(starring2);
        movieMusicDirector.setText(musicDirector);
        movieImdbRating.setText(String.valueOf(imdbRating));

        // Set click listener for wishlist button
        wishlistButton.setOnClickListener(v -> {
            if (currentUser != null) {
                addToWishlist(currentUser.getUid(), movieId);
            } else {
                Toast.makeText(this, "Please log in to add to wishlist", Toast.LENGTH_SHORT).show();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Create a map of item IDs to actions
        Map<Integer, Runnable> navActions = new HashMap<>();
        navActions.put(R.id.navigation_home, () -> showToast("Home Selected"));
        navActions.put(R.id.navigation_dashboard, () -> showToast("Dashboard Selected"));
        navActions.put(R.id.navigation_notifications, () -> showToast("Notifications Selected"));
        navActions.put(R.id.navigation_notifications, this::navigateToProfile); // Add profile navigation
        navActions.put(R.id.navigation_dashboard, this::navigateToWishlist); // Add profile navigation
        navActions.put(R.id.navigation_home, this::navigateToHome); // Add profile navigation


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Runnable action = navActions.get(item.getItemId());
            if (action != null) {
                action.run();
                return true;
            }
            return false;
        });
    }

    private void addToWishlist(String userId, String movieId) {
        DatabaseReference wishlistReference = FirebaseDatabase.getInstance().getReference("wishlists");
        String wishlistId = wishlistReference.push().getKey();

        if (wishlistId != null) {
            WishlistModel wishlistModel = new WishlistModel(userId, movieId);
            wishlistReference.child(wishlistId).setValue(wishlistModel)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MovieDetailsActivity.this, "Added to Wishlist", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MovieDetailsActivity.this, WishlistActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MovieDetailsActivity.this, "Failed to add to Wishlist", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void showToast(String message) {
        Toast.makeText(MovieDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToProfile() {
        Intent intent = new Intent(MovieDetailsActivity.this, ProfilePage.class);
        startActivity(intent);
    }

    private void navigateToHome() {
        Intent intent = new Intent(MovieDetailsActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void navigateToWishlist() {
        Intent intent = new Intent(MovieDetailsActivity.this, WishlistActivity.class);
        startActivity(intent);
    }
}
