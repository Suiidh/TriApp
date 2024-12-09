package com.example.triapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollecteUtils {

    public static Map<String, List<String>> organiserParCommune(List<String> pointsCollecte) {
        Map<String, List<String>> collecteParCommune = new HashMap<>();

        for (String point : pointsCollecte) {
            String[] parts = point.split(","); // Supposons que "Commune, Adresse" est le format
            if (parts.length == 2) {
                String commune = parts[1].trim();
                String adresse = parts[0].trim();

                collecteParCommune.computeIfAbsent(commune, k -> new ArrayList<>()).add(adresse);
            }
        }
        return collecteParCommune;
    }
}
