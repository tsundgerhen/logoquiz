package com.example.myquiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context context;
    private ArrayList<CategoryModel> categoryModels;
    private OnCategoryClickListener onCategoryClickListener;

    // Constructor
    public CategoryAdapter(Context context, ArrayList<CategoryModel> categoryModels) {
        this.context = context;
        this.categoryModels = categoryModels;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the category item layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryModel model = categoryModels.get(position);

        // Set category name
        holder.textView.setText(model.getCategoryName());

        // Load category image using Glide
        Glide.with(context)
                .load(model.getCategoryImage())  // Get the image URL
                .into(holder.imageView);

        // Handle category selection
        holder.itemView.setOnClickListener(v -> {
            if (onCategoryClickListener != null) {
                onCategoryClickListener.onCategoryClick(model);  // Pass the selected category to the listener
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();  // Return the number of categories
    }

    // Set the listener for category click events
    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.onCategoryClickListener = listener;
    }

    // Interface for category click events
    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryModel category);
    }

    // ViewHolder for each category item
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.categoryImage);
            textView = itemView.findViewById(R.id.category);
        }
    }
}
