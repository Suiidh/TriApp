package com.example.triapp;

import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
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

    private void showCitiesDialog(String wasteType) {
        List<String> collectionPoints = wasteMap.get(wasteType);
        if (collectionPoints == null || collectionPoints.isEmpty()) {
            Toast.makeText(context, "No collection point found for this waste type.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, List<String>> collectionByCity = CollectionUtils.organizeByCity(collectionPoints);
        List<String> cities = new ArrayList<>(collectionByCity.keySet());

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_cities, null);

        ImageView dialogImage = dialogView.findViewById(R.id.dialogImage);
        int imageResId = CollectionUtils.getWasteBinImageForType(wasteType);
        dialogImage.setImageResource(imageResId);

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

        new AlertDialog.Builder(context)
                .setTitle("Choisissez une commune")
                .setView(dialogView)
                .setPositiveButton("Fermer", null)
                .show();
    }

    private static class ViewHolder {
        TextView wasteTypeTextView;
        Button seeMoreButton;
    }
}
