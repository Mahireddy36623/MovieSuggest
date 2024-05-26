package com.example.moviesuggest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfilePage extends AppCompatActivity {

    private TextView profileName, profileEmail, profileUsername;
    private Button viewWishlistButton, logoutButton;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference("users");

        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profileUsername = findViewById(R.id.profileUsername);
        viewWishlistButton = findViewById(R.id.viewWishlistButton);
        logoutButton = findViewById(R.id.logoutButton);

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            if (userEmail != null) {
                userReference.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String name = userSnapshot.child("name").getValue(String.class);
                            String username = userSnapshot.child("username").getValue(String.class);
                            profileName.setText("Name: " + name);
                            profileEmail.setText("Email: " + userEmail);
                            profileUsername.setText("Username: " + username);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ProfilePage.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            // No user is signed in, redirect to login activity
            Intent intent = new Intent(ProfilePage.this, login.class);
            startActivity(intent);
            finish();
        }

        // Set button click listeners
        viewWishlistButton.setOnClickListener(v -> {
            Intent wishlistIntent = new Intent(ProfilePage.this, WishlistActivity.class);
            startActivity(wishlistIntent);
        });

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent logoutIntent = new Intent(ProfilePage.this, login.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(logoutIntent);
            finish();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Create a map of item IDs to actions
        Map<Integer, Runnable> navActions = new HashMap<>();
        navActions.put(R.id.navigation_home, this::navigateToHome);
        navActions.put(R.id.navigation_dashboard, this::navigateToWishlist);
        navActions.put(R.id.navigation_notifications, this::navigateToNotifications);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Runnable action = navActions.get(item.getItemId());
            if (action != null) {
                action.run();
                return true;
            }
            return false;
        });
    }

    private void navigateToHome() {
        Intent homeIntent = new Intent(ProfilePage.this, MainActivity.class);
        startActivity(homeIntent);
    }

    private void navigateToWishlist() {
        Intent homeIntent = new Intent(ProfilePage.this, WishlistActivity.class);
        startActivity(homeIntent);
    }

    private void navigateToNotifications() {
        // Implement notifications navigation
    }
}
