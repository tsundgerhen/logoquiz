package com.example.myquiz;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myquiz.databinding.FragmentProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Retrieve user data
        retrieveData();

        // Fetch user rank
        getRank();

        // Logout button click listener
        binding.logoutBtn.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void retrieveData() {
        // Replace "user1Uid" with the actual user ID you want to fetch data for
        String userUid = "HRNEaoJnW5fJO8tLomOVlcMe7zj1";  // You must provide this manually since there's no FirebaseAuth

        databaseReference.child("users").child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    Long coins = snapshot.child("coins").getValue(Long.class);

                    if (name != null) binding.userNameP.setText(name);
                    if (email != null) binding.emailP.setText(email);
                    if (coins != null) binding.coinsP.setText(String.format("%d\nTotal Coins", coins));
                } else {
                    binding.userNameP.setText("User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error fetching user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRank() {
        databaseReference.child("users").orderByChild("coins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int rank = 1; // Start rank at 1
                    String userUid = "user1Uid"; // Replace with the actual user ID

                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        if (userSnapshot.getKey().equals(userUid)) {
                            binding.rankP.setText(String.format("%d\nRank", rank));
                            return; // Stop the loop once the user's rank is found
                        }
                        rank++;
                    }

                    // If user is not found in the ranking
                    binding.rankP.setText("Rank not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error fetching rank: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLogoutConfirmationDialog() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Logout");
            builder.setMessage("Are you sure you want to log out?");
            builder.setPositiveButton("Logout", (dialog, which) -> logout());
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }
    }

    private void logout() {
        // Clear any locally stored user information or session data here
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
