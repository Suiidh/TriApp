package com.example.triapp;

import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

    public static List<Dechet> readDechetsFromCSV(Resources resources, int resourceId) {
        List<Dechet> dechets = new ArrayList<>();
        InputStream inputStream = resources.openRawResource(resourceId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line;
            boolean isFirstLine = true; // Pour ignorer l'en-tête
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Ignore la première ligne (l'en-tête)
                }

                // Diviser la ligne par les virgules, en respectant les champs entre guillemets
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Gère les champs avec des guillemets
                if (parts.length > 8) {
                    String typeDechet = parts[8].trim(); // Colonne "type_dechet"
                    String adresse = parts[5].trim();   // Colonne "adresse"
                    String commune = parts[3].trim();  // Colonne "commune"

                    // Récupérer l'image en fonction du type de déchet
                    int imageResId = getImageResourceForDechet(typeDechet);

                    // Créer un objet Dechet et l'ajouter à la liste
                    dechets.add(new Dechet(typeDechet, "Adresse : " + adresse + ", Commune : " + commune, imageResId));
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

        return dechets;
    }

    // Méthode pour récupérer l'image en fonction du type de déchet
    private static int getImageResourceForDechet(String typeDechet) {
        if (typeDechet == null || typeDechet.isEmpty()) {
            android.util.Log.d("CSVReader", "Type de déchet null ou vide, retour poubelle grise");
            return R.drawable.poubelle_marron; // Par défaut si aucune info
        }

        // Log le type de déchet reçu
        android.util.Log.d("CSVReader", "Type de déchet analysé : " + typeDechet);

        switch (typeDechet.toLowerCase().trim()) {
            case "verre":
                android.util.Log.d("CSVReader", "Poubelle verte assignée pour " + typeDechet);
                return R.drawable.poubelle_verte;
            case "papier":
            case "carton":
                android.util.Log.d("CSVReader", "Poubelle bleue assignée pour " + typeDechet);
                return R.drawable.poubelle_bleue;
            case "plastique":
            case "emballages":
            case "metal":
                android.util.Log.d("CSVReader", "Poubelle jaune assignée pour " + typeDechet);
                return R.drawable.poubelle_jaune;
            case "compost":
            case "déchets organiques":
            case "déchets alimentaires":
                android.util.Log.d("CSVReader", "Poubelle marron assignée pour " + typeDechet);
                return R.drawable.poubelle_marron;
            default:
                android.util.Log.d("CSVReader", "Poubelle grise assignée (type inconnu) pour " + typeDechet);
                return R.drawable.poubelle_marron;
        }
    }
}
