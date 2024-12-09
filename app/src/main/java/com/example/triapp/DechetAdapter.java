package com.example.triapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

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
            // Récupérer les points de collecte
            List<String> pointsCollecte = dechetsMap.get(typeDechet);

            // Récupérer l'image associée à ce type de déchet
            int imageResId = getPoubelleImageForDechet(typeDechet);

            // Construire le message des points de collecte
            StringBuilder message = new StringBuilder();
            if (pointsCollecte != null && !pointsCollecte.isEmpty()) {
                message.append("Points de collecte pour ").append(typeDechet).append(":\n\n");
                for (String point : pointsCollecte) {
                    message.append("- ").append(point).append("\n");
                }
            } else {
                message.append("Aucun point de collecte trouvé pour ce type de déchet.");
            }

            // Afficher une boîte de dialogue avec l'image et les points de collecte
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_poubelle_info, null);
            TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
            ImageView dialogImage = dialogView.findViewById(R.id.dialogImage);

            dialogMessage.setText(message.toString());
            dialogImage.setImageResource(imageResId);

            new AlertDialog.Builder(context)
                    .setTitle(typeDechet)
                    .setView(dialogView)
                    .setPositiveButton("OK", null)
                    .show();
        });

        return convertView;
    }

    // Méthode pour récupérer l'image de la poubelle en fonction du type de déchet
    private int getPoubelleImageForDechet(String typeDechet) {
        switch (typeDechet.toLowerCase()) {
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
