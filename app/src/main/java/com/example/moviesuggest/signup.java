package com.example.moviesuggest;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    EditText name, username, password, email;
    TextView loginlink;
    Button signupbtn;

    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users");

        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        loginlink = findViewById(R.id.loginlink);
        signupbtn = findViewById(R.id.signup);

        loginlink.setOnClickListener(view -> {
            Intent intent = new Intent(signup.this, login.class);
            startActivity(intent);
            finish();
        });

        signupbtn.setOnClickListener(view -> {
            String nameuser = name.getText().toString().trim();
            String emailUser = email.getText().toString().trim();
            String usernameUser = username.getText().toString().trim();
            String passUser = password.getText().toString().trim();

            Log.d(TAG, "Signup details - Name: " + nameuser + ", Email: " + emailUser + ", Username: " + usernameUser + ", Password: " + passUser);

            if (nameuser.isEmpty() || emailUser.isEmpty() || usernameUser.isEmpty() || passUser.isEmpty()) {
                Toast.makeText(signup.this, "Fill all the fields", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.createUserWithEmailAndPassword(emailUser, passUser)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    UserModel model = new UserModel(nameuser, emailUser, usernameUser, passUser);
                                    reference.child(user.getUid()).setValue(model)
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    Toast.makeText(signup.this, "Success", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "User registered successfully: " + user.getEmail());
                                                    Intent intent = new Intent(signup.this, MainActivity.class);
                                                    intent.putExtra("name", nameuser);
                                                    intent.putExtra("email", emailUser);
                                                    intent.putExtra("username", usernameUser);
                                                    intent.putExtra("password", passUser);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Log.e(TAG, "Failed to store user data", task1.getException());
                                                    Toast.makeText(signup.this, "Failed to store user data", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                Log.e(TAG, "Registration failed: " + task.getException().getMessage());
                                Toast.makeText(signup.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
