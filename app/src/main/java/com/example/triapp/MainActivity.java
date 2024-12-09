package com.example.triapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Map<String, List<String>> dechetsMap;
    private ListView listViewDechets;
    private TextView pointsCollecteTextView;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialiser les vues
        listViewDechets = findViewById(R.id.listViewDechets);
        pointsCollecteTextView = findViewById(R.id.pointsCollecteTextView);
        searchEditText = findViewById(R.id.searchEditText);

        // Charger les données depuis le fichier CSV
        dechetsMap = CSVReader.readDechetsFromCSV(getResources(), R.raw.dechets);

        // Extraire les types de déchets
        List<String> typesDechets = new ArrayList<>(dechetsMap.keySet());

        // Configurer l'adaptateur pour la liste des types de déchets
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, typesDechets);
        listViewDechets.setAdapter(adapter);

        // Ajout d'un écouteur pour rechercher dynamiquement
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Gérer la sélection dans la liste
        listViewDechets.setOnItemClickListener((parent, view, position, id) -> {
            String typeDechet = typesDechets.get(position);
            afficherPointsCollecte(typeDechet);
        });
    }

    private void afficherPointsCollecte(String typeDechet) {
        // Obtenir les points de collecte pour ce type de déchet
        List<String> pointsCollecte = dechetsMap.get(typeDechet);

        // Afficher les points de collecte
        if (pointsCollecte != null && !pointsCollecte.isEmpty()) {
            StringBuilder sb = new StringBuilder("Points de collecte pour ").append(typeDechet).append(":\n\n");
            for (String point : pointsCollecte) {
                sb.append("- ").append(point).append("\n");
            }
            pointsCollecteTextView.setText(sb.toString());
        } else {
            pointsCollecteTextView.setText("Aucun point de collecte trouvé pour ce type de déchet.");
        }
    }
}
