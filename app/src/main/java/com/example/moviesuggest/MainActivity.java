package com.example.moviesuggest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private LinearLayout moviesLayout;
    private DatabaseReference moviesReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userName, userEmail, userUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userEmail = currentUser.getEmail();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
            Query query = reference.orderByChild("email").equalTo(userEmail);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        userName = userSnapshot.child("name").getValue(String.class);
                        userUsername = userSnapshot.child("username").getValue(String.class);
                    }
                    // Display user details in a Toast (for demonstration purposes)
                    Toast.makeText(MainActivity.this, "Welcome, " + userName, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Error fetching user details", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // No user is signed in, redirect to login activity
            Intent intent = new Intent(MainActivity.this, login.class);
            startActivity(intent);
            finish();
        }

        moviesLayout = findViewById(R.id.moviesLayout);
        moviesReference = FirebaseDatabase.getInstance().getReference("movies");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Create a map of item IDs to actions
        Map<Integer, Runnable> navActions = new HashMap<>();
        navActions.put(R.id.navigation_home, () -> showToast("Home Selected"));
        navActions.put(R.id.navigation_dashboard, () -> showToast("Dashboard Selected"));
        navActions.put(R.id.navigation_notifications, () -> showToast("Notifications Selected"));
        navActions.put(R.id.navigation_notifications, this::navigateToProfile); // Add profile navigation
        navActions.put(R.id.navigation_dashboard, this::navigateToWishlist); // Add profile navigation


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Runnable action = navActions.get(item.getItemId());
            if (action != null) {
                action.run();
                return true;
            }
            return false;
        });

        loadMovies();
    }

    private void loadMovies() {
        moviesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot movieSnapshot : dataSnapshot.getChildren()) {
                    MoviesModel movie = movieSnapshot.getValue(MoviesModel.class);
                    movie.setMovieId(movieSnapshot.getKey()); // Set the movie ID
                    addMovieCard(movie);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void addMovieCard(MoviesModel movie) {
        View movieCard = LayoutInflater.from(this).inflate(R.layout.movie_card, moviesLayout, false);

        ImageView movieImage = movieCard.findViewById(R.id.movieImage);
        TextView movieTitle = movieCard.findViewById(R.id.movieTitle);
        TextView movieGenre = movieCard.findViewById(R.id.movieGenre);
        TextView movieDescription = movieCard.findViewById(R.id.movieDescription);

        Picasso.get().load(movie.getImageURL()).into(movieImage);
        movieTitle.setText(movie.getName());
        movieGenre.setText(movie.getGenre());
        movieDescription.setText(movie.getDescription());

        // Set click listener on the movie card
        movieCard.setOnClickListener(v -> {
            // Navigate to MovieDetailsActivity
            Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
            intent.putExtra("movieId", movie.getMovieId());
            intent.putExtra("imageURL", movie.getImageURL());
            intent.putExtra("title", movie.getName());
            intent.putExtra("genre", movie.getGenre());
            intent.putExtra("description", movie.getDescription());
            intent.putExtra("director", movie.getDirector());
            intent.putExtra("starring1", movie.getStarring1());
            intent.putExtra("starring2", movie.getStarring2());
            intent.putExtra("musicDirector", movie.getMusicDirector());
            intent.putExtra("imdbRating", movie.getImdbRating());
            startActivity(intent);
        });

        moviesLayout.addView(movieCard);
    }

    private void navigateToProfile() {
        Intent intent = new Intent(MainActivity.this, ProfilePage.class);
        intent.putExtra("name", userName);
        intent.putExtra("email", userEmail);
        intent.putExtra("username", userUsername);
        startActivity(intent);
    }

    private void navigateToWishlist() {
        Intent intent = new Intent(MainActivity.this, WishlistActivity.class);
        startActivity(intent);
    }


    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
