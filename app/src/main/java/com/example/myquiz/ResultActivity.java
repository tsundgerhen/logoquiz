package com.example.myquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myquiz.databinding.ActivityResultBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResultActivity extends AppCompatActivity {

    ActivityResultBinding binding;
    int POINTS = 10;  // Points per correct answer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get correct answers and total questions from Intent
        int correctAnswers = getIntent().getIntExtra("correct", 0);
        int totalQuestions = getIntent().getIntExtra("total", 0);

        // Calculate points
        long points = correctAnswers * POINTS;

        // Display score and earned coins
        binding.score.setText(String.format("%d/%d", correctAnswers, totalQuestions));
        binding.earnedCoins.setText(String.valueOf(points));

        // Get the userId from the Intent
        String userId = getIntent().getStringExtra("userId");

        if (userId != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference userRef = database.getReference("users").child(userId);

            // Update the user's coins in the database
            userRef.child("coins").setValue(points).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ResultActivity.this, "Coins updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ResultActivity.this, "Error updating coins: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ResultActivity.this, "Error: User ID is missing", Toast.LENGTH_SHORT).show();
        }

        // Restart button logic
        binding.restartBtn.setOnClickListener(view -> {
            startActivity(new Intent(ResultActivity.this, MainActivity.class));
            finish();
        });
    }
}
