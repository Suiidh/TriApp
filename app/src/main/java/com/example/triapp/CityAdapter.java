package com.example.triapp;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private final Context context;
    private final List<String> cities;
    private final Map<String, List<String>> collectionByCity;

    public CityAdapter(Context context, List<String> cities, Map<String, List<String>> collectionByCity) {
        this.context = context;
        this.cities = cities;
        this.collectionByCity = collectionByCity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_city, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CityAdapter.ViewHolder holder, int position) {
        String city = cities.get(position);
        holder.cityName.setText(city);

        holder.itemView.setOnClickListener(v -> showAddressesDialog(city));
    }

    private void showAddressesDialog(String city) {
        List<String> addresses = collectionByCity.get(city);
        if (addresses == null || addresses.isEmpty()) {
            Toast.makeText(context, "Aucunes adresses trouvée pour cette commune.", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_addresses, null);
        RecyclerView recyclerViewAddresses = dialogView.findViewById(R.id.recyclerViewAddresses);
        recyclerViewAddresses.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewAddresses.setAdapter(new AddressAdapter(context, addresses));

        new AlertDialog.Builder(context)
                .setTitle("Adresses dans " + city)
                .setView(dialogView)
                .setPositiveButton("Fermer", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cityName;

        public ViewHolder(View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.cityName);
        }
    }
}
