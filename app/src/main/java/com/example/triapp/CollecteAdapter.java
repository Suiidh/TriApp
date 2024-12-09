package com.example.triapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class CollecteAdapter extends RecyclerView.Adapter<CollecteAdapter.CollecteViewHolder> {

    private final Context context;
    private final Map<String, List<String>> collecteData; // Map de Commune -> Liste d'adresses
    private final List<String> communes; // Liste ordonnée des noms de communes

    public CollecteAdapter(Context context, Map<String, List<String>> collecteData, List<String> communes) {
        this.context = context;
        this.collecteData = collecteData;
        this.communes = communes;
    }

    @NonNull
    @Override
    public CollecteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_point_collecte, parent, false);
        return new CollecteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollecteViewHolder holder, int position) {
        String commune = communes.get(position);
        List<String> adresses = collecteData.get(commune);

        // Associer les données à la vue
        holder.communeTextView.setText(commune);
        if (adresses != null) {
            StringBuilder adresseList = new StringBuilder();
            for (String adresse : adresses) {
                adresseList.append("- ").append(adresse).append("\n");
            }
            holder.adressesTextView.setText(adresseList.toString());
        } else {
            holder.adressesTextView.setText("Aucune adresse disponible.");
        }
    }

    @Override
    public int getItemCount() {
        return communes.size();
    }

    public static class CollecteViewHolder extends RecyclerView.ViewHolder {
        TextView communeTextView;
        TextView adressesTextView;

        public CollecteViewHolder(@NonNull View itemView) {
            super(itemView);
            communeTextView = itemView.findViewById(R.id.communeTextView);
            adressesTextView = itemView.findViewById(R.id.adressesTextView);
        }
    }
}
