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

        // Initialiser les données
        listeDechets = new ArrayList<>();
        listeDechets.add(new Dechet("Bouteille en plastique", "Poubelle Jaune", R.drawable.poubelle_jaune));
        listeDechets.add(new Dechet("Papier", "Poubelle Bleue", R.drawable.poubelle_bleue));
        listeDechets.add(new Dechet("Verre", "Poubelle Verte", R.drawable.poubelle_verte));
        listeDechets.add(new Dechet("Déchets organiques", "Poubelle Marron", R.drawable.poubelle_marron));

        // Extraire uniquement les noms des déchets pour l'affichage
        List<String> nomsDechets = new ArrayList<>();
        for (Dechet dechet : listeDechets) {
            nomsDechets.add(dechet.getNom());
        }

        // Configurer l'adaptateur pour afficher les noms dans la liste
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nomsDechets);
        listViewDechets.setAdapter(adapter);

        // Ajouter un TextWatcher pour le filtrage de la liste
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Appliquer le filtre sur l'adaptateur
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Ajouter un écouteur pour le bouton "Entrée" (recherche)
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                String query = searchEditText.getText().toString().trim();

                // Vérifier si la recherche correspond à un déchet
                for (Dechet dechet : listeDechets) {
                    if (dechet.getNom().equalsIgnoreCase(query)) {
                        // Mettre à jour les informations
                        dechetTextView.setText("Déchet : " + dechet.getNom());
                        poubelleTextView.setText("Poubelle : " + dechet.getPoubelle());
                        poubelleImageView.setImageResource(dechet.getImageResId());

                        // Afficher la section avec une animation
                        infoLayout.setVisibility(View.VISIBLE);
                        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
                        animation.setDuration(500);
                        infoLayout.startAnimation(animation);
                        return true; // On arrête ici car le déchet a été trouvé
                    }
                }

                // Si aucun déchet n'a été trouvé
                dechetTextView.setText("Déchet introuvable");
                poubelleTextView.setText("");
                poubelleImageView.setImageResource(0);
                infoLayout.setVisibility(View.VISIBLE);

                return true; // Indique que l'action a été gérée
            }
            return false; // Indique que l'action n'a pas été gérée
        });

        // Ajouter un clic pour afficher la poubelle associée
        listViewDechets.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            // Récupérer le nom du déchet sélectionné dans la liste visible
            String nomDechetSelectionne = (String) parent.getItemAtPosition(position);

            // Trouver le déchet correspondant dans la liste complète
            for (Dechet dechet : listeDechets) {
                if (dechet.getNom().equals(nomDechetSelectionne)) {
                    // Mettre à jour les informations
                    dechetTextView.setText("Déchet : " + dechet.getNom());
                    poubelleTextView.setText("Poubelle : " + dechet.getPoubelle());
                    poubelleImageView.setImageResource(dechet.getImageResId());

                    // Afficher la section avec une animation
                    infoLayout.setVisibility(View.VISIBLE);
                    AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
                    animation.setDuration(500);
                    infoLayout.startAnimation(animation);
                    break;
                }
            }
        });
    }
}
