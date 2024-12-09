package com.example.triapp;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triapp.R;

import java.util.List;
import java.util.Map;

public class CommuneAdapter extends RecyclerView.Adapter<CommuneAdapter.ViewHolder> {

    private final Context context;
    private final List<String> communes;
    private final Map<String, List<String>> collecteParCommune;

    public CommuneAdapter(Context context, List<String> communes, Map<String, List<String>> collecteParCommune) {
        this.context = context;
        this.communes = communes;
        this.collecteParCommune = collecteParCommune;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_commune, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String commune = communes.get(position);
        holder.communeName.setText(commune);

        holder.itemView.setOnClickListener(v -> {
            List<String> adresses = collecteParCommune.get(commune);

            if (adresses != null && !adresses.isEmpty()) {
                // Afficher la liste des adresses
                View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_adresses, null);

                RecyclerView recyclerViewAdresses = dialogView.findViewById(R.id.recyclerViewAdresses);
                recyclerViewAdresses.setLayoutManager(new LinearLayoutManager(context));
                recyclerViewAdresses.setAdapter(new AdresseAdapter(context, adresses));

                new AlertDialog.Builder(context)
                        .setTitle("Adresses dans " + commune)
                        .setView(dialogView)
                        .setPositiveButton("Fermer", null)
                        .show();
            } else {
                Toast.makeText(context, "Aucune adresse trouvée pour cette commune.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return communes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView communeName;

        public ViewHolder(View itemView) {
            super(itemView);
            communeName = itemView.findViewById(R.id.communeName);
        }
    }
}

