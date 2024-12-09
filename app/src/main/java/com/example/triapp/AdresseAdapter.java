package com.example.triapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.triapp.R;

import java.util.List;

public class AdresseAdapter extends RecyclerView.Adapter<AdresseAdapter.ViewHolder> {

    private final Context context;
    private final List<String> adresses;

    public AdresseAdapter(Context context, List<String> adresses) {
        this.context = context;
        this.adresses = adresses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_adresse, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.adresseText.setText(adresses.get(position));
    }

    @Override
    public int getItemCount() {
        return adresses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView adresseText;

        public ViewHolder(View itemView) {
            super(itemView);
            adresseText = itemView.findViewById(R.id.adresseText);
        }
    }
}
