package com.example.e_sbitar.commandes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_sbitar.R;
import com.example.e_sbitar.medicaments.BDDMedicaments;
import com.example.e_sbitar.medicaments.MedicamentAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CommandeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    Dialog dialogue;

    RecyclerView commandsRecycler;
    LinearLayoutManager manager;

    DetailsCommandeAdapter detailsCommandeAdapter;
    BDDMedsCommande bddMedsCommande;

    ArrayList<Commande> commandsList = new ArrayList<>();

    public CommandeAdapter(Context context) {
        this.context = context;
    }

    public void setItems(ArrayList<Commande> Commandes) {
        commandsList.clear(); //pour que les items de la recyclerview ne se dupliquent pas
        commandsList.addAll(Commandes);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.activity_commande_adapter, parent, false);

        CommandeViewHolder commandeViewHolder = new CommandeViewHolder(view);

        commandeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                int position = commandeViewHolder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Commande commande = commandsList.get(position);
                    String itemId = commande.getCode();

                    dialogue = new Dialog(context);
                    dialogue.setContentView(R.layout.details_de_commande_dialog);
                    dialogue.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Drawable draw = context.getResources().getDrawable(R.drawable.rounded_corner_white);
                        dialogue.getWindow().setBackgroundDrawable(draw);
                    }
                    dialogue.setCancelable(false);

                    Button approuverBtn = dialogue.findViewById(R.id.approuver_btn);
                    approuverBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            DatabaseReference commandeRef = FirebaseDatabase.getInstance().getReference()
                                    .child("Commandes").child(itemId).child("etat");
                            commandeRef.setValue("Approuvée");

                            dialogue.dismiss();

                        }
                    });

                    Button refuserBtn = dialogue.findViewById(R.id.refuser_btn);
                    refuserBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            DatabaseReference commandeRef = FirebaseDatabase.getInstance().getReference()
                                    .child("Commandes").child(itemId).child("etat");
                            commandeRef.setValue("Refusée");
                            dialogue.dismiss();

                        }
                    });
                    ImageView annuler = dialogue.findViewById(R.id.annuler_det_dialogue);

                    dialogue.show();

                    annuler.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogue.dismiss();
                        }
                    });

                    commandsRecycler = dialogue.findViewById(R.id.commande_recycler);
                    commandsRecycler.setHasFixedSize(true);

                    Context ctx = dialogue.getContext();

                    manager = new LinearLayoutManager(ctx);
                    commandsRecycler.setLayoutManager(manager);

                    detailsCommandeAdapter = new DetailsCommandeAdapter(ctx);
                    commandsRecycler.setAdapter(detailsCommandeAdapter);
                    bddMedsCommande = new BDDMedsCommande(itemId);

                    SwipeRefreshLayout swipeRefreshLayout = dialogue.findViewById(R.id.commande_swiper);
                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {

                            loadCommandMedsData(itemId);
                            detailsCommandeAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);

                        }
                    });

                    loadCommandMedsData(itemId);

                }

            }

            private void loadCommandMedsData(String itemId) {

                bddMedsCommande.get().addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ArrayList<MedCommande> othCommands = new ArrayList<>();

                        for (DataSnapshot data : snapshot.getChildren()){
                            MedCommande comm;
                            comm = data.getValue(MedCommande.class);
                            othCommands.add(comm);
                        }

                        detailsCommandeAdapter.setItems(othCommands);
                        detailsCommandeAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });

            }

        });

        return commandeViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        CommandeViewHolder viewHolder = (CommandeViewHolder) holder;

        Commande commande = commandsList.get(position);
        viewHolder.etat.setText(commande.getEtat());
        viewHolder.code.setText(commande.getCode());

    }

    @Override
    public int getItemCount() {

        return commandsList.size();

    }

    public class CommandeViewHolder extends RecyclerView.ViewHolder {

        public TextView etat, code, patient;

        public CommandeViewHolder(@NonNull View itemView) {

            super(itemView);
            etat = itemView.findViewById(R.id.etat_cmdd);
            code = itemView.findViewById(R.id.code_cmdd);
            patient = itemView.findViewById(R.id.patient_cmdd);

        }
    }

}