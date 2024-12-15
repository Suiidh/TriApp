package com.example.triapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WasteAdapter extends BaseAdapter {

    private final Context context;
    private final List<String> wasteTypes;
    private final Map<String, List<String>> wasteMap;

    public WasteAdapter(Context context, List<String> wasteTypes, Map<String, List<String>> wasteMap) {
        this.context = context;
        this.wasteTypes = wasteTypes;
        this.wasteMap = wasteMap;
    }

    @Override
    public int getCount() {
        return wasteTypes.size();
    }

    @Override
    public Object getItem(int position) {
        return wasteTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Shows each waste type in the list
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_waste, parent, false);
            holder = new ViewHolder();
            holder.wasteTypeTextView = convertView.findViewById(R.id.wasteTypeTextView);
            holder.seeMoreButton = convertView.findViewById(R.id.seeMoreButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String wasteType = wasteTypes.get(position);
        holder.wasteTypeTextView.setText(wasteType);

        holder.seeMoreButton.setOnClickListener(v -> showCitiesDialog(wasteType));
        return convertView;
    }

    @SuppressLint("SetTextI18n")
    private void showCitiesDialog(String wasteType) {
        List<String> collectionPoints = wasteMap.get(wasteType);
        if (collectionPoints == null || collectionPoints.isEmpty()) {
            Toast.makeText(context, "Aucun point de collecte trouvé pour ce type de déchet.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, List<String>> collectionByCity = CollectionUtils.organizeByCity(collectionPoints);
        List<String> cities = new ArrayList<>(collectionByCity.keySet());

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_cities, null);

        ImageView dialogImage = dialogView.findViewById(R.id.dialogImage);
        int imageResId = CollectionUtils.getWasteBinImageForType(wasteType);
        dialogImage.setImageResource(imageResId);

        TextView nearestAddressTextView = dialogView.findViewById(R.id.nearestAddressTextView);

        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerViewCities);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        CityAdapter cityAdapter = new CityAdapter(context, cities, collectionByCity);
        recyclerView.setAdapter(cityAdapter);

        EditText searchCitiesEditText = dialogView.findViewById(R.id.searchCitiesEditText);
        searchCitiesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cityAdapter.filterCities(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        double userLat = MainActivity.userLatitude;
        double userLon = MainActivity.userLongitude;
        if (!Double.isNaN(userLat) && !Double.isNaN(userLon)) {
            nearestAddressTextView.setText("Adresse la plus proche : en cours de calcul...");

            new Thread(() -> {
                String nearestAddress = findNearestAddress(userLat, userLon, collectionByCity);
                ((AppCompatActivity)context).runOnUiThread(() -> {
                    if (nearestAddress != null) {
                        nearestAddressTextView.setText("Adresse la plus proche : " + nearestAddress);
                    } else {
                        nearestAddressTextView.setText("Impossible de déterminer l'adresse la plus proche.");
                    }
                });
            }).start();
        } else {
            nearestAddressTextView.setText("Localisation non disponible.");
        }

        new AlertDialog.Builder(context)
                .setTitle("Choisissez une commune")
                .setView(dialogView)
                .setPositiveButton("Fermer", null)
                .show();
    }


    private String findNearestAddress(double userLat, double userLon, Map<String, List<String>> collectionByCity) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String nearestAddress = null;
        double minDistance = Double.MAX_VALUE;

        // Parcours de toutes les adresses
        for (Map.Entry<String, List<String>> entry : collectionByCity.entrySet()) {
            for (String address : entry.getValue()) {
                try {
                    List<Address> results = geocoder.getFromLocationName(address, 1);
                    if (results != null && !results.isEmpty()) {
                        Address addrResult = results.get(0);
                        double addrLat = addrResult.getLatitude();
                        double addrLon = addrResult.getLongitude();
                        double distance = distanceInMeters(userLat, userLon, addrLat, addrLon);
                        if (distance < minDistance) {
                            minDistance = distance;
                            nearestAddress = address;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return nearestAddress;
    }

    private double distanceInMeters(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371e3;
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaPhi = Math.toRadians(lat2 - lat1);
        double deltaLambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2)
                + Math.cos(phi1) * Math.cos(phi2)
                * Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private static class ViewHolder {
        TextView wasteTypeTextView;
        Button seeMoreButton;
    }
}
