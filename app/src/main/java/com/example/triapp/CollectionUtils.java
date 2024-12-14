package com.example.triapp;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionUtils {

    public static Map<String, List<String>> organizeByCity(List<String> collectionPoints) {
        Map<String, List<String>> collectionByCity = new HashMap<>();
        for (String point : collectionPoints) {
            String[] parts = point.split(",");
            // parts[0] = address, parts[1] = city
            if (parts.length == 2) {
                String city = parts[1].trim();
                String address = parts[0].trim();
                collectionByCity.computeIfAbsent(city, k -> new ArrayList<>()).add(address);
            }
        }
        return collectionByCity;
    }

    public static int getWasteBinImageForType(String wasteType) {
        if (TextUtils.isEmpty(wasteType)) {
            return R.drawable.bin_yellow; // default
        }

        switch (wasteType.trim().toLowerCase()) {
            case "verre":
                return R.drawable.bin_green;
            case "papier":
                return R.drawable.bin_blue;
            case "emballages":
                return R.drawable.bin_yellow;
            case "ordures ménagères résiduelles":
                return R.drawable.bin_brown;
            default:
                return R.drawable.bin_grey;
        }
    }
}
