package com.example.e_sbitar.panier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_sbitar.R;
import com.example.e_sbitar.medicaments.Medicament;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PanierAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    TextView nomMédicament, quantité, code, prix, date_de_fabrication, date_dexpiration;

    Dialog dialogue;

    private Context context;

    ArrayList<Medicament> medsList = new ArrayList<>();

    public PanierAdapter(Context context) {
        this.context = context;
    }

    public void setItems (ArrayList<Medicament> Médicaments){
        medsList.clear(); //pour que les items de la recyclerview ne se dupliquent pas
        medsList.addAll(Médicaments);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.activity_panier_adapter, parent, false);

        PanierViewHolder medViewHolder = new PanierViewHolder(view);

        medViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogue = new Dialog(context);
                dialogue.setContentView(R.layout.details_de_medicament_dans_le_panier);
                dialogue.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Drawable draw = context.getResources().getDrawable(R.drawable.rounded_corner_white);
                    dialogue.getWindow().setBackgroundDrawable(draw);
                }
                dialogue.setCancelable(false);

                Button retirerBtn = dialogue.findViewById(R.id.retirer_btn);
                ImageView annulerBtn = dialogue.findViewById(R.id.annuler_det_dialogue);
                nomMédicament = dialogue.findViewById(R.id.det_nom_val);
                code = dialogue.findViewById(R.id.det_code_val);
                prix = dialogue.findViewById(R.id.det_prix_val);
                quantité = dialogue.findViewById(R.id.det_quant_val);
                date_de_fabrication = dialogue.findViewById(R.id.det_date_fab_val);
                date_dexpiration = dialogue.findViewById(R.id.det_date_exp_val);

                String current = medsList.get(medViewHolder.getAdapterPosition()).getNom();
                nomMédicament.setText(current);
                code.setText(medsList.get(medViewHolder.getAdapterPosition()).getCode());
                prix.setText(medsList.get(medViewHolder.getAdapterPosition()).getPrix());
                quantité.setText(medsList.get(medViewHolder.getAdapterPosition()).getQuantité());
                date_de_fabrication.setText(medsList.get(medViewHolder.getAdapterPosition()).getDate_de_fabrication());
                date_dexpiration.setText(medsList.get(medViewHolder.getAdapterPosition()).getDate_dexpiration());

                dialogue.show();

                annulerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogue.dismiss();
                    }
                });

                retirerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = medViewHolder.getAdapterPosition();

                        AlertDialog.Builder builder = new AlertDialog.Builder(medViewHolder.itemView.getContext());
                        builder.setTitle("Voulez vous vraiment retirer ce medicament?");
                        builder.setMessage("Si vous cliquez sur Oui, le medicament ne sera plus disponible dans votre panier");
                        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                retirerDuPanier(position);
                                dialogue.dismiss();
                            }
                        });

                        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });

                        builder.show();

                    }
                });

            }

        });

        return medViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        PanierViewHolder viewHolder = (PanierViewHolder) holder;

        Medicament med = medsList.get(position);
        viewHolder.nom.setText(med.getNom());
        viewHolder.prix.setText(med.getPrix());
        viewHolder.quantité.setText(med.getQuantité());
        viewHolder.retirer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();

                AlertDialog.Builder builder = new AlertDialog.Builder(viewHolder.itemView.getContext());
                builder.setTitle("Voulez vous vraiment retirer ce medicament?");
                builder.setMessage("Si vous cliquez sur Oui, le medicament ne sera plus disponible dans votre panier");
                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        retirerDuPanier(position);
                    }
                });

                builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                builder.show();

            }
        });

    }

    private void retirerDuPanier(int position) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = user.getUid();
        DatabaseReference clientRef = FirebaseDatabase.getInstance().getReference().child("Utilisateurs").child("Patients");
        String nomCourant = medsList.get(position).getNom();
        clientRef.child(currentUid).child("Panier").child(nomCourant).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    notifyDataSetChanged();
                    Toast.makeText(context, nomCourant + " est retiré de votre panier", Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return medsList.size();
    }

    public ArrayList<Medicament> getItems() {
        return medsList;
    }

    public class PanierViewHolder extends RecyclerView.ViewHolder {

        TextView nom, quantité, prix;
        ImageView retirer;

        public PanierViewHolder(@NonNull View itemView) {
            super(itemView);
            nom = itemView.findViewById(R.id.nom_medd);
            prix = itemView.findViewById(R.id.prix_medd);
            quantité = itemView.findViewById(R.id.quantité_medd);
            retirer = itemView.findViewById(R.id.retirer_signe);
        }
    }

}