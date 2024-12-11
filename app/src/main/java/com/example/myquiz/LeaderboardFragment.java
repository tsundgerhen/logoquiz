package com.example.myquiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myquiz.databinding.FragmentLeaderboardBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LeaderboardFragment extends Fragment {

    private FragmentLeaderboardBinding binding;
    private ArrayList<User> users;
    private LeaderboardAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);

        // Initialize the ArrayList and Adapter
        users = new ArrayList<>();
        adapter = new LeaderboardAdapter(getContext(), users);

        // Set up RecyclerView
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch leaderboard data from Firebase Realtime Database
        fetchLeaderboardData();

        return binding.getRoot();
    }

    private void fetchLeaderboardData() {
        // Get reference to Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        // Fetch all user data from "users" node
        usersRef.orderByChild("coins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear(); // Clear previous data

                // Loop through all users in the "users" node
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        users.add(user); // Add the user to the list
                    }
                }

                // Sort the users by coins in descending order (highest first)
                sortUsersByCoins();

                // Update the RecyclerView
                adapter.notifyDataSetChanged();
                binding.emptyStateText.setVisibility(View.GONE); // Hide "No leaderboard data"
                binding.recyclerView.setVisibility(View.VISIBLE); // Show the RecyclerView
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                Toast.makeText(getContext(), "Error fetching data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                binding.emptyStateText.setVisibility(View.VISIBLE); // Show empty state
                binding.recyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void sortUsersByCoins() {
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                return Integer.compare(user2.getCoins(), user1.getCoins()); // Sort in descending order by coins
            }
        });
    }
}
