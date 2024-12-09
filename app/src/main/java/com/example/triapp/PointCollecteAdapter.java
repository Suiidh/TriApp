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

public class PointCollecteAdapter extends RecyclerView.Adapter<PointCollecteAdapter.ViewHolder> {

    private final Context context;
    private final Map<String, List<String>> pointsParCommune;

    public PointCollecteAdapter(Context context, Map<String, List<String>> pointsParCommune) {
        this.context = context;
        this.pointsParCommune = pointsParCommune;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_point_collecte, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String commune = (String) pointsParCommune.keySet().toArray()[position];
        List<String> adresses = pointsParCommune.get(commune);

        holder.communeTextView.setText(commune);
        holder.adressesTextView.setText(joinAdresses(adresses));
    }

    @Override
    public int getItemCount() {
        return pointsParCommune.size();
    }

    private String joinAdresses(List<String> adresses) {
        StringBuilder sb = new StringBuilder();
        for (String adresse : adresses) {
            sb.append("• ").append(adresse).append("\n");
        }
        return sb.toString().trim();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView communeTextView;
        TextView adressesTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            communeTextView = itemView.findViewById(R.id.communeTextView);
            adressesTextView = itemView.findViewById(R.id.adressesTextView);
        }
    }
}
