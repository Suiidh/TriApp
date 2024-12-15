package com.example.triapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private final Context context;
    private final List<String> originalAddresses;
    private final List<String> filteredAddresses;

    public AddressAdapter(Context context, List<String> addresses) {
        this.context = context;
        this.originalAddresses = new ArrayList<>(addresses);
        this.filteredAddresses = new ArrayList<>(addresses);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.addressText.setText(filteredAddresses.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredAddresses.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterAddresses(String query) {
        filteredAddresses.clear();
        if (query == null || query.trim().isEmpty()) {
            filteredAddresses.addAll(originalAddresses);
        } else {
            String lowerQuery = query.toLowerCase();
            for (String addr : originalAddresses) {
                if (addr.toLowerCase().contains(lowerQuery)) {
                    filteredAddresses.add(addr);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView addressText;

        public ViewHolder(View itemView) {
            super(itemView);
            addressText = itemView.findViewById(R.id.addressText);
        }
    }
}
