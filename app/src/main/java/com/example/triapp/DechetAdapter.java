package com.example.triapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

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

            // Afficher une boîte de dialogue avec les points de collecte
            if (pointsCollecte != null && !pointsCollecte.isEmpty()) {
                StringBuilder message = new StringBuilder("Points de collecte pour ").append(typeDechet).append(":\n\n");
                for (String point : pointsCollecte) {
                    message.append("- ").append(point).append("\n");
                }
                new androidx.appcompat.app.AlertDialog.Builder(context)
                        .setTitle("Points de collecte")
                        .setMessage(message.toString())
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                new androidx.appcompat.app.AlertDialog.Builder(context)
                        .setTitle("Points de collecte")
                        .setMessage("Aucun point de collecte trouvé pour ce type de déchet.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });

        return convertView;
    }
}
