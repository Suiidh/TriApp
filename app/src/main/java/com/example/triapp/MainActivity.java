package com.example.triapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Map<String, List<String>> dechetsMap;
    private ListView listViewDechets;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialiser les vues
        listViewDechets = findViewById(R.id.listViewDechets);
        searchEditText = findViewById(R.id.searchEditText);

        // Charger les données depuis le fichier CSV
        dechetsMap = CSVReader.readDechetsFromCSV(getResources(), R.raw.dechets);

        // Extraire les types de déchets
        List<String> typesDechets = new ArrayList<>(dechetsMap.keySet());

        // Configurer l'adaptateur personnalisé
        DechetAdapter adapter = new DechetAdapter(this, typesDechets, dechetsMap);
        listViewDechets.setAdapter(adapter);

        // Ajout d'un écouteur pour rechercher dynamiquement
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Filtrer les types de déchets
                List<String> filteredDechets = new ArrayList<>();
                for (String typeDechet : typesDechets) {
                    if (typeDechet.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredDechets.add(typeDechet);
                    }
                }

                // Mettre à jour l'adapter
                DechetAdapter filteredAdapter = new DechetAdapter(MainActivity.this, filteredDechets, dechetsMap);
                listViewDechets.setAdapter(filteredAdapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    public void afficherPointsCollecte(String typeDechet) {
        // Obtenir les points de collecte pour ce type de déchet
        List<String> pointsCollecte = dechetsMap.get(typeDechet);

        // Grouper les points de collecte par commune
        Map<String, List<String>> pointsParCommune = grouperParCommune(pointsCollecte);

        // Configurer le RecyclerView dans une boîte de dialogue
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = LayoutInflater.from(this).inflate(R.layout.dialog_poubelle_info, null);
        builder.setView(customView);

        RecyclerView recyclerView = customView.findViewById(R.id.recyclerViewCollecte);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new PointCollecteAdapter(this, pointsParCommune));

        builder.setPositiveButton("Fermer", null);
        builder.show();
    }

    private Map<String, List<String>> grouperParCommune(List<String> pointsCollecte) {
        Map<String, List<String>> map = new HashMap<>();
        for (String point : pointsCollecte) {
            String[] parts = point.split(", Commune : "); // Assurez-vous que ce format correspond à vos données
            String adresse = parts[0].trim();
            String commune = parts.length > 1 ? parts[1].trim() : "Inconnue";

            if (!map.containsKey(commune)) {
                map.put(commune, new ArrayList<>());
            }
            map.get(commune).add(adresse);
        }
        return map;
    }
}
