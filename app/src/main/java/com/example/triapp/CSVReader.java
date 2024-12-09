package com.example.triapp;

import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

public class CSVReader {

    public static Map<String, List<String>> readDechetsFromCSV(Resources resources, int resourceId) {
        Map<String, List<String>> dechetsMap = new HashMap<>();
        InputStream inputStream = resources.openRawResource(resourceId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line;
            boolean isFirstLine = true; // Ignorer l'en-tête
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Ignore l'en-tête
                }

                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (parts.length > 7) {
                    String typeDechet = parts[8].trim(); // Colonne "type_dechet"
                    String adresse = parts[5].trim();   // Colonne "adresse"
                    String commune = parts[3].trim();  // Colonne "commune"

                    String pointCollecte = "Adresse : " + adresse + ", Commune : " + commune;

                    // Ajouter le point de collecte au type de déchet
                    dechetsMap.putIfAbsent(typeDechet, new ArrayList<>());
                    dechetsMap.get(typeDechet).add(pointCollecte);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return dechetsMap;
    }
}

