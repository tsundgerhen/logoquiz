package com.example.myquiz;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myquiz.databinding.ActivityQuizBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {

    ActivityQuizBinding binding;
    ArrayList<Questions> questions;
    int index = 0;
    Questions question;
    CountDownTimer timer;
    FirebaseDatabase database;
    DatabaseReference questionsRef;
    int correctAnswers = 0;
    MediaPlayer correctSound;
    MediaPlayer wrongSound;
    private boolean optionSelected = false;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize sound effects
        correctSound = MediaPlayer.create(this, R.raw.winsound);
        wrongSound = MediaPlayer.create(this, R.raw.errorsound);

        questions = new ArrayList<>();
        database = FirebaseDatabase.getInstance();

        // Get the category ID passed from HomeFragment
        String catId = getIntent().getStringExtra("catId");
        userId = getIntent().getStringExtra("userUid");
        // Reference to the questions in Firebase Realtime Database
        questionsRef = database.getReference("categories").child(catId).child("questions");

        // Fetch questions from Firebase Realtime Database
        questionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                questions.clear();  // Clear any previous questions

                if (dataSnapshot.exists()) {
                    // Iterate through all children of dataSnapshot (questions)
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Questions question = snapshot.getValue(Questions.class);  // Convert snapshot to Questions object
                        if (question != null) {
                            questions.add(question);  // Add the question to the list
                        }
                    }
                    // Now that all questions are loaded, proceed to the next question
                    setNextQuestion();
                } else {
                    // Handle case where there are no questions in Firebase
                    Toast.makeText(QuizActivity.this, "No questions available for this category.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(QuizActivity.this, "Error fetching questions.", Toast.LENGTH_SHORT).show();
            }
        });

        restTimer();
    }

    void restTimer() {
        // Initialize countdown timer for each question
        timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                binding.timer.setText(String.valueOf(l / 1000)); // Update timer UI
            }

            @Override
            public void onFinish() {
                setNextQuestion();  // Move to next question when timer finishes
            }
        };
    }

    void showAnswer() {
        // Highlight the correct answer once the user selects an option
        if (question.getAns().equals(binding.option1.getText().toString()))
            binding.option1.setBackground(getResources().getDrawable(R.drawable.option_right));
        else if (question.getAns().equals(binding.option2.getText().toString()))
            binding.option2.setBackground(getResources().getDrawable(R.drawable.option_right));
        else if (question.getAns().equals(binding.option3.getText().toString()))
            binding.option3.setBackground(getResources().getDrawable(R.drawable.option_right));
        else if (question.getAns().equals(binding.option4.getText().toString()))
            binding.option4.setBackground(getResources().getDrawable(R.drawable.option_right));
    }

    void setNextQuestion() {
        optionSelected = false; // Reset option selection flag
        if (timer != null) {
            timer.cancel(); // Cancel previous timer if any
        }

        restTimer();  // Restart the timer
        timer.start(); // Start the countdown

        // Load the next question
        if (index < questions.size()) {
            binding.questionCounter.setText(String.format("%d/%d", (index + 1), questions.size()));
            question = questions.get(index);

            // Load the logo image using Glide
            Glide.with(QuizActivity.this)
                    .load(question.getLogoUrl())
                    .into(binding.logoImageView);

            binding.option1.setText(question.getOpt1());
            binding.option2.setText(question.getOpt2());
            binding.option3.setText(question.getOpt3());
            binding.option4.setText(question.getOpt4());
        } else {
            // If no questions left, show results
            Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
            intent.putExtra("correct", correctAnswers);
            intent.putExtra("total", questions.size());
            intent.putExtra("userUid", userId);

            startActivity(intent);
            finish();
        }
        index++;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.option_1:
            case R.id.option_2:
            case R.id.option_3:
            case R.id.option_4:
                if (!optionSelected) {
                    optionSelected = true;
                    TextView selectedOption = (TextView) view;
                    checkAnswer(selectedOption);
                }
                break;
            case R.id.nextBtn:
                reset();  // Reset the options for the next question
                setNextQuestion();  // Move to the next question
                break;
        }
    }

    void checkAnswer(TextView selectedOption) {
        String selectedAnswer = selectedOption.getText().toString();

        if (selectedAnswer.equals(question.getAns())) {
            correctAnswers++;
            correctSound.start();
            selectedOption.setBackground(getResources().getDrawable(R.drawable.option_right));
            Toast.makeText(this, "CORRECT!", Toast.LENGTH_SHORT).show();
        } else {
            wrongSound.start();
            selectedOption.setBackground(getResources().getDrawable(R.drawable.option_wrong));
            showAnswer();
            Toast.makeText(this, "Wrong answer!", Toast.LENGTH_SHORT).show();
        }
    }

    void reset() {
        // Reset all options to their default state
        binding.option1.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option2.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option3.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        binding.option4.setBackground(getResources().getDrawable(R.drawable.option_unselected));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release media resources when the activity is destroyed
        if (correctSound != null) {
            correctSound.release();
            correctSound = null;
        }
        if (wrongSound != null) {
            wrongSound.release();
            wrongSound = null;
        }
    }
}
