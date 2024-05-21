package com.example.oldmarket4.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oldmarket4.R;


import java.util.List;

import model.Product;

public class ChangeProductAdapter extends RecyclerView.Adapter<ChangeProductAdapter.ProductViewHolder> {

    private List<Product> products;
    private OnItemClickListener listener;

    public ChangeProductAdapter(List<Product> products, OnItemClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.change_product_row, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvDescription;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvDescription = itemView.findViewById(R.id.tvProductDescription);
        }

        public void bind(final Product product, final OnItemClickListener listener) {
            tvName.setText(product.getName());
            tvDescription.setText(product.getDescription());
            itemView.setOnClickListener(v -> listener.onItemClick(product));
        }
    }
}
