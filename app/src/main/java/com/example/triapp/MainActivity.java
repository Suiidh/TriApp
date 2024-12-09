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

public class MainActivity extends AppCompatActivity {

    private List<Dechet> listeDechets;
    private ListView listViewDechets;
    private TextView dechetTextView, poubelleTextView;
    private ImageView poubelleImageView;
    private LinearLayout infoLayout;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialiser les vues
        listViewDechets = findViewById(R.id.listViewDechets);
        dechetTextView = findViewById(R.id.dechetTextView);
        poubelleTextView = findViewById(R.id.poubelleTextView);
        poubelleImageView = findViewById(R.id.poubelleImageView);
        infoLayout = findViewById(R.id.infoLayout);
        searchEditText = findViewById(R.id.searchEditText);

        // Charger les données depuis le fichier CSV
        listeDechets = CSVReader.readDechetsFromCSV(getResources(), R.raw.dechets);

        // Extraire les noms des déchets
        List<String> nomsDechets = new ArrayList<>();
        for (Dechet dechet : listeDechets) {
            nomsDechets.add(dechet.getNom());
        }

        // Configurer l'adaptateur pour la liste des noms
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nomsDechets);
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

        // Gestion de l'événement "Entrée" sur le champ de recherche
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                String query = searchEditText.getText().toString().trim();

                // Vérifier si le déchet existe
                for (Dechet dechet : listeDechets) {
                    if (dechet.getNom().equalsIgnoreCase(query)) {
                        afficherDechet(dechet);
                        return true;
                    }
                }

                // Si aucun déchet correspondant n'est trouvé
                afficherDechetIntrouvable();
                return true;
            }
            return false;
        });

        // Gérer la sélection dans la liste
        listViewDechets.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            Dechet dechetSelectionne = listeDechets.get(position);
            afficherDechet(dechetSelectionne);
        });
    }

    private void afficherDechet(Dechet dechet) {
        // Mettre à jour les informations
        dechetTextView.setText("Déchet : " + dechet.getNom());
        poubelleTextView.setText("Details : " + dechet.getPoubelle());
        poubelleImageView.setImageResource(getImageResourceForDechet(dechet.getPoubelle()));

        // Afficher la section avec une animation
        infoLayout.setVisibility(View.VISIBLE);
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        infoLayout.startAnimation(animation);
    }

    private void afficherDechetIntrouvable() {
        // Afficher un message pour un déchet introuvable
        dechetTextView.setText("Déchet introuvable");
        poubelleTextView.setText("");
        poubelleImageView.setImageResource(0); // Pas d'image pour un déchet introuvable
        infoLayout.setVisibility(View.VISIBLE);

        // Animation pour la visibilité
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        infoLayout.startAnimation(animation);
    }

    // Méthode pour récupérer l'image associée à une poubelle
    private int getImageResourceForDechet(String typePoubelle) {
        switch (typePoubelle.toLowerCase()) {
            case "poubelle verte":
                return R.drawable.poubelle_verte;
            case "poubelle jaune":
                return R.drawable.poubelle_jaune;
            case "poubelle marron":
                return R.drawable.poubelle_marron;
            case "poubelle bleue":
                return R.drawable.poubelle_bleue;
            default:
                return R.drawable.poubelle_jaune;
        }
    }
}
