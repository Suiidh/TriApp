package com.example.triapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
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
}
