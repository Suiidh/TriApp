package com.example.triapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private final Context context;
    private final List<String> originalCities;
    private final List<String> filteredCities;
    private final Map<String, List<String>> collectionByCity;

    public CityAdapter(Context context, List<String> cities, Map<String, List<String>> collectionByCity) {
        this.context = context;
        this.originalCities = cities;
        this.filteredCities =  new ArrayList<>(cities);
        this.collectionByCity = collectionByCity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_city, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String city = filteredCities.get(position);
        holder.cityName.setText(city);

        holder.itemView.setOnClickListener(v -> showAddressesDialog(city));
    }

    @Override
    public int getItemCount() {
        return filteredCities.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterCities(String query) {
        filteredCities.clear();
        if (query == null || query.trim().isEmpty()) {
            filteredCities.addAll(originalCities);
        } else {
            String lowerQuery = query.toLowerCase();
            for (String city : originalCities) {
                if (city.toLowerCase().contains(lowerQuery)) {
                    filteredCities.add(city);
                }
            }
        }
        notifyDataSetChanged();
    }

    private void showAddressesDialog(String city) {
        List<String> addresses = collectionByCity.get(city);
        if (addresses == null || addresses.isEmpty()) {
            Toast.makeText(context, "Aucune adresse trouvée pour cette commune.", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_addresses, null);
        RecyclerView recyclerViewAddresses = dialogView.findViewById(R.id.recyclerViewAddresses);
        recyclerViewAddresses.setLayoutManager(new LinearLayoutManager(context));

        AddressAdapter addressAdapter = new AddressAdapter(context, addresses);
        recyclerViewAddresses.setAdapter(addressAdapter);

        EditText searchAddressesEditText = dialogView.findViewById(R.id.searchAddressesEditText);
        searchAddressesEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addressAdapter.filterAddresses(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) { }
        });

        new AlertDialog.Builder(context)
                .setTitle("Adresses dans " + city)
                .setView(dialogView)
                .setPositiveButton("Fermer", null)
                .show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cityName;
        public ViewHolder(View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.cityName);
        }
    }
}
