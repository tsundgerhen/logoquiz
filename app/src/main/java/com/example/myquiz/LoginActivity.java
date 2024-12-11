package com.example.myquiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myquiz.databinding.ActivityLoginBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users");

        dialog = new ProgressDialog(this);
        dialog.setMessage("Logging in...");

        // Login button click listener
        binding.submitBtn.setOnClickListener(view -> {
            String email = binding.emailbox.getText().toString();
            String password = binding.passwordBox.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            } else {
                dialog.show();

                // Check if the entered email and password match any user in the database
                userRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean userFound = false;

                        // Loop through all users to find the one that matches the email and password
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            if (user != null && user.getPassword().equals(password)) {
                                userFound = true;

                                // Successful login, pass user data to the next activity
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("userUid", snapshot.getKey()); // Pass user UID to the next activity
                                intent.putExtra("userName", user.getName()); // Pass user name if needed
                                intent.putExtra("userCoins", user.getCoins()); // Pass user coins if needed
                                startActivity(intent);
                                finish();  // Close the login activity
                                break;
                            }
                        }

                        // If no user was found or password doesn't match
                        if (!userFound) {
                            Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Error logging in: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Navigate to SignupActivity if the user doesn't have an account
        binding.createNewBtn.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });
    }
}
