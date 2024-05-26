package com.example.moviesuggest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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

public class WishlistActivity extends AppCompatActivity {

    private LinearLayout wishlistLayout;
    private DatabaseReference wishlistReference, moviesReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wishlist);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        wishlistLayout = findViewById(R.id.wishlistLayout);
        wishlistReference = FirebaseDatabase.getInstance().getReference("wishlists");
        moviesReference = FirebaseDatabase.getInstance().getReference("movies");

        if (currentUser != null) {
            loadWishlist(currentUser.getUid());
        } else {
            Toast.makeText(this, "Please log in to view your wishlist", Toast.LENGTH_SHORT).show();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Create a map of item IDs to actions
        Map<Integer, Runnable> navActions = new HashMap<>();
        navActions.put(R.id.navigation_home, () -> showToast("Home Selected"));
        navActions.put(R.id.navigation_dashboard, () -> showToast("Dashboard Selected"));
        navActions.put(R.id.navigation_notifications, () -> showToast("Notifications Selected"));
        navActions.put(R.id.navigation_notifications, this::navigateToProfile);
        navActions.put(R.id.navigation_home, this::navigateToHome);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Runnable action = navActions.get(item.getItemId());
            if (action != null) {
                action.run();
                return true;
            }
            return false;
        });
    }

    private void showToast(String message) {
        Toast.makeText(WishlistActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToProfile() {
        Intent intent = new Intent(WishlistActivity.this, ProfilePage.class);
        startActivity(intent);
    }

    private void navigateToHome() {
        Intent intent = new Intent(WishlistActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void loadWishlist(String userId) {
        Query query = wishlistReference.orderByChild("userId").equalTo(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                wishlistLayout.removeAllViews();
                for (DataSnapshot wishlistSnapshot : snapshot.getChildren()) {
                    String movieId = wishlistSnapshot.child("movieId").getValue(String.class);
                    loadMovie(movieId, wishlistSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WishlistActivity.this, "Error loading wishlist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMovie(String movieId, String wishlistEntryId) {
        moviesReference.child(movieId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MoviesModel movie = snapshot.getValue(MoviesModel.class);
                if (movie != null) {
                    addMovieCard(movie, wishlistEntryId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WishlistActivity.this, "Error loading movie details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMovieCard(MoviesModel movie, String wishlistEntryId) {
        View movieCard = LayoutInflater.from(this).inflate(R.layout.wishlist_movie_card, wishlistLayout, false);

        ImageView movieImage = movieCard.findViewById(R.id.movieImage);
        TextView movieTitle = movieCard.findViewById(R.id.movieTitle);
        TextView movieGenre = movieCard.findViewById(R.id.movieGenre);
        TextView movieDescription = movieCard.findViewById(R.id.movieDescription);
        ImageView tickMark = movieCard.findViewById(R.id.tickMark);

        Picasso.get().load(movie.getImageURL()).into(movieImage);
        movieTitle.setText(movie.getName());
        movieGenre.setText(movie.getGenre());
        movieDescription.setText(movie.getDescription());

        tickMark.setVisibility(View.VISIBLE);

        tickMark.setOnClickListener(v -> {
            removeFromWishlist(wishlistEntryId);
        });

        wishlistLayout.addView(movieCard);
    }

    private void removeFromWishlist(String wishlistEntryId) {
        wishlistReference.child(wishlistEntryId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(WishlistActivity.this, "Removed from wishlist", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(WishlistActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(WishlistActivity.this, "Error removing from wishlist", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
