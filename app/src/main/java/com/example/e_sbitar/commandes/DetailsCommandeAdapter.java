package com.example.e_sbitar.commandes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.e_sbitar.R;

import java.util.ArrayList;

public class DetailsCommandeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    ArrayList<MedCommande> commandeMedsList = new ArrayList<>();

    public DetailsCommandeAdapter(Context context) {
        this.context = context;
    }

    public void setItems(ArrayList<MedCommande> Commandes) {
        commandeMedsList.clear(); //pour que les items de la recyclerview ne se dupliquent pas
        commandeMedsList.addAll(Commandes);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.activity_details_commande_adapter, parent, false);

        CommandeMedViewHolder commandeMedViewHolder = new CommandeMedViewHolder(view);

        return commandeMedViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        CommandeMedViewHolder viewHolder = (CommandeMedViewHolder) holder;

        MedCommande medCommande = commandeMedsList.get(position);

        viewHolder.nom.setText(medCommande.getNom());
        viewHolder.prix.setText(medCommande.getPrix());
        viewHolder.quantité.setText(medCommande.getQuantité());

    }

    @Override
    public int getItemCount() {

        return commandeMedsList.size();

    }

    public class CommandeMedViewHolder extends RecyclerView.ViewHolder {

        public TextView nom, prix, quantité;

        public CommandeMedViewHolder(@NonNull View itemView) {

            super(itemView);
            nom = itemView.findViewById(R.id.nom_medd);
            prix = itemView.findViewById(R.id.prix_medd);
            quantité = itemView.findViewById(R.id.quantité_medd);

        }
    }

}