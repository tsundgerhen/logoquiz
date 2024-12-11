package com.example.myquiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myquiz.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    FirebaseDatabase database;
    DatabaseReference userRef;
    ProgressDialog dialog;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users");  // Reference to the "users" node

        dialog = new ProgressDialog(this);
        dialog.setMessage("We are creating your account...");

        binding.createNewbtn.setOnClickListener(view -> {
            String email = binding.emailbox.getText().toString();
            String password = binding.passwordBox.getText().toString();
            String cpassword = binding.confirmpasswordBox.getText().toString();
            String name = binding.nameBox.getText().toString();

            if (email.isEmpty() || password.isEmpty() || name.isEmpty() || cpassword.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please enter your details completely", Toast.LENGTH_SHORT).show();
            } else if (!cpassword.equals(password)) {
                Toast.makeText(SignupActivity.this, "Please confirm your password correctly", Toast.LENGTH_SHORT).show();
            } else {
                dialog.show();

                // Firebase Authentication to create user
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            dialog.dismiss();
                            if (task.isSuccessful()) {
                                String userId = FirebaseAuth.getInstance().getUid(); // Get UID from FirebaseAuth

                                // Create a User object with default coins = 50 and stability = 0
                                User user = new User(name, email, password, 0, 0);

                                // Save user data to Firebase under the UID
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                                userRef.setValue(user).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(SignupActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                        finish();  // Close SignupActivity after success
                                    } else {
                                        Toast.makeText(SignupActivity.this, "Error saving user data: " + task1.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(SignupActivity.this, "Error creating account: " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Navigate to login screen if already have an account
        binding.loginBtn.setOnClickListener(view -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });
    }
}
