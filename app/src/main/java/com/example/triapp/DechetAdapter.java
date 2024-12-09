package com.example.triapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DechetAdapter extends BaseAdapter {

    private final Context context;
    private final List<String> typesDechets;
    private final Map<String, List<String>> dechetsMap;

    public DechetAdapter(Context context, List<String> typesDechets, Map<String, List<String>> dechetsMap) {
        this.context = context;
        this.typesDechets = typesDechets;
        this.dechetsMap = dechetsMap;
    }

    @Override
    public int getCount() {
        return typesDechets.size();
    }

    @Override
    public Object getItem(int position) {
        return typesDechets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_dechet, parent, false);
        }

        // Obtenir le type de déchet actuel
        String typeDechet = typesDechets.get(position);

        // Initialiser les vues
        TextView typeDechetTextView = convertView.findViewById(R.id.typeDechetTextView);
        Button voirPlusButton = convertView.findViewById(R.id.voirPlusButton);

        // Mettre à jour les informations de l'élément
        typeDechetTextView.setText(typeDechet);

        // Ajouter un clic sur le bouton
        voirPlusButton.setOnClickListener(v -> {
            List<String> pointsCollecte = dechetsMap.get(typeDechet);

            if (pointsCollecte != null && !pointsCollecte.isEmpty()) {
                // Organiser les données par commune
                Map<String, List<String>> collecteParCommune = CollecteUtils.organiserParCommune(pointsCollecte);
                List<String> communes = new ArrayList<>(collecteParCommune.keySet());

                // Créer la vue du dialogue
                View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_communes, null);

                // Associer l'image en fonction du type de déchet
                ImageView dialogImage = dialogView.findViewById(R.id.dialogImage);
                int imageResId = getPoubelleImageForDechet(typeDechet);
                dialogImage.setImageResource(imageResId);

                // Configurer le RecyclerView pour afficher les communes
                RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerViewCommunes);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(new CommuneAdapter(context, communes, collecteParCommune));

                // Afficher le dialogue
                new AlertDialog.Builder(context)
                        .setTitle("Choisissez une commune")
                        .setView(dialogView)
                        .setPositiveButton("Fermer", null)
                        .show();
            } else {
                Toast.makeText(context, "Aucun point de collecte trouvé pour ce type de déchet.", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    // Méthode pour récupérer l'image de la poubelle en fonction du type de déchet
    private int getPoubelleImageForDechet(String typeDechet) {
        if (typeDechet == null) {
            return R.drawable.poubelle_jaune; // Par défaut
        }

        switch (typeDechet.trim().toLowerCase()) {
            case "verre":
                return R.drawable.poubelle_verte;
            case "papier":
                return R.drawable.poubelle_bleue;
            case "plastique":
                return R.drawable.poubelle_jaune;
            case "déchets organiques":
                return R.drawable.poubelle_marron;
            default:
                return R.drawable.poubelle_jaune; // Par défaut
        }
    }
}
