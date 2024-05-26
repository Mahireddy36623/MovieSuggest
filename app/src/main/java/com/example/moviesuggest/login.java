package com.example.moviesuggest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    EditText loginEmail, loginPassword;
    Button loginButton;
    TextView signupLink;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mAuth = FirebaseAuth.getInstance();

        loginEmail = findViewById(R.id.usernamelogin);
        loginPassword = findViewById(R.id.passwordlogin);
        loginButton = findViewById(R.id.login);
        signupLink = findViewById(R.id.signuplink);

        signupLink.setOnClickListener(view -> {
            Intent intent = new Intent(login.this, signup.class);
            startActivity(intent);
            finish();
        });

        loginButton.setOnClickListener(view -> loginUser());
    }

    private void loginUser() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        Log.d(TAG, "loginUser: Email: " + email);
        Log.d(TAG, "loginUser: Password: " + password);

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(login.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "signInWithEmail:success, User: " + user.getEmail());
                        fetchUserData(user);
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(login.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void fetchUserData(FirebaseUser user) {
        if (user == null) {
            Log.e(TAG, "fetchUserData: User is null");
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        Log.d(TAG, "fetchUserData: Fetching data for user: " + user.getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel != null) {
                        Log.d(TAG, "User data found: " + userModel.getEmail());
                        Intent intent = new Intent(login.this, MainActivity.class);
                        intent.putExtra("name", userModel.getName());
                        intent.putExtra("email", userModel.getEmail());
                        intent.putExtra("username", userModel.getUsername());
                        intent.putExtra("password", userModel.getPass());
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e(TAG, "UserModel is null");
                    }
                } else {
                    Log.e(TAG, "User data not found in database");
                    Toast.makeText(login.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch user data", error.toException());
                Toast.makeText(login.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.d(TAG, "updateUI: User is signed in: " + user.getEmail());
        } else {
            Log.d(TAG, "updateUI: User is null");
        }
    }
}
