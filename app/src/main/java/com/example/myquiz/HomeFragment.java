package com.example.myquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.myquiz.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private User user;
    private FirebaseDatabase database;
    private DatabaseReference userRef, categoriesRef;
    private CategoryModel selectedCategory;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users");
        categoriesRef = database.getReference("categories");

        // Fetch user data based on UID
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            // If not logged in, show message
            Toast.makeText(getContext(), "Please log in to view your profile", Toast.LENGTH_SHORT).show();
        } else {
            userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        binding.userName.setText("Hi, " + user.getName());
                    } else {
                        Toast.makeText(getContext(), "Error: User data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Error fetching user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Fetch categories from Firebase
        ArrayList<CategoryModel> categories = new ArrayList<>();
        CategoryAdapter adapter = new CategoryAdapter(getContext(), categories);
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categories.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CategoryModel model = snapshot.getValue(CategoryModel.class);
                    if (model != null) {
                        model.setCategoryId(snapshot.getKey());
                        categories.add(model);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error fetching categories: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set up RecyclerView
        binding.categoryList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.categoryList.setAdapter(adapter);

        // Set category click listener
        adapter.setOnCategoryClickListener(category -> {
            selectedCategory = category;
            Intent intent = new Intent(getActivity(), QuizActivity.class);
            intent.putExtra("catId", selectedCategory.getCategoryId());
            intent.putExtra("userUid", userId);
            startActivity(intent);
        });

        return binding.getRoot();
    }
}
