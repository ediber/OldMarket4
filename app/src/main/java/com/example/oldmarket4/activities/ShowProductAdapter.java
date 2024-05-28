package com.example.oldmarket4.activities;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oldmarket4.R;

import java.util.List;

import model.Product;

public class ShowProductAdapter extends RecyclerView.Adapter<ShowProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    public ShowProductAdapter(List<Product> productList, OnItemClickListener clickListener) {
        this.productList = productList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_row, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription;

        ProductViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvProductName);
            tvDescription = view.findViewById(R.id.tvProductDescription);

            view.setOnClickListener(v -> {
                if (clickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.onItemClick(position);
                    }
                }
            });

            view.setOnLongClickListener(v -> {
                if (clickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.onItemLongClick(position);
                        return true;
                    }
                }
                return false;
            });
        }

        void bind(Product product) {
            tvName.setText(product.getName());
            tvDescription.setText(product.getDescription());
            itemView.setBackgroundColor(product.isSelected() ? Color.RED : Color.WHITE);
        }
    }
}
