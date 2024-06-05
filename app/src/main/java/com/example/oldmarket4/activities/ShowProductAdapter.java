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

    // set the products, change data because we switch between our products to not our products
    public void setProducts(List<Product> productList) {
        this.productList = productList;
    }

    public List<Product> getProducts() {
        return productList;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    // cliclListener is an object of OnItemClickListener interface, which is implemented in MainActivity.
    // MainActivity calls new on this interface
    public ShowProductAdapter(List<Product> productList, OnItemClickListener clickListener) {
        this.productList = productList;
        this.clickListener = clickListener;
    }

    // create a new view to show from xml, each row has its own view form this xml
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_row, parent, false);
        return new ProductViewHolder(itemView);
    }

    // bind the data to the view, each row has its own view, called multiple times,
    // everytime with different position
    // gets data of relevant product
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    // show the number of products
    @Override
    public int getItemCount() {
        return productList.size();
    }

    // responsible for showing the product name and description in a single row
    // showing information to the users on text views
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
