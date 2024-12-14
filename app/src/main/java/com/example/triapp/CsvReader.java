package com.example.triapp;

import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CsvReader {

    public static Map<String, List<String>> readWasteFromCsv(Resources resources, int resourceId) {
        Map<String, List<String>> wasteMap = new HashMap<>();
        InputStream inputStream = resources.openRawResource(resourceId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Ignore header
                }

                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (parts.length > 7) {
                    String wasteType = cleanString(parts[8]);
                    String address = cleanString(parts[5]);
                    String city = cleanString(parts[3]);

                    String collectionPoint = address + ", " + city;

                    wasteMap.putIfAbsent(wasteType, new ArrayList<>());
                    Objects.requireNonNull(wasteMap.get(wasteType)).add(collectionPoint);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { reader.close(); } catch (IOException e) { e.printStackTrace(); }
        }

        return wasteMap;
    }

    private static String cleanString(String str) {
        if (str == null) return "";
        return str.replace("\"", "").trim();
    }
}
