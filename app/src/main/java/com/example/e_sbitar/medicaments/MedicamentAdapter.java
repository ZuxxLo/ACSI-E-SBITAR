package com.example.e_sbitar.medicaments;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MedicamentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    TextView nomMédicament, quantité, code, prix, date_de_fabrication, date_dexpiration;
    TextInputLayout modifNomMédicament, modifQuantité, modifCode, modifPrix, modifDate_de_fabrication, modifDate_dexpiration;
    String nomMedTexte, prixTexte, codeTexte, dateFabTexte, dateExpTexte, quantitéTexte;

    Dialog dialogue;

    private Context context;

    ArrayList<Medicament> medsList = new ArrayList<>();

    public MedicamentAdapter(Context context) {
        this.context = context;
    }

    public void setItems(ArrayList<Medicament> Médicaments) {
        medsList.clear(); //pour que les items de la recyclerview ne se dupliquent pas
        medsList.addAll(Médicaments);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.activity_medicament_adapter, parent, false);

        MedicamentViewHolder medViewHolder = new MedicamentViewHolder(view);

        medViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //vérifier si c'est un patient ou un prestataire

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUid = user.getUid();
                DatabaseReference clientRef = FirebaseDatabase.getInstance().getReference().child("Utilisateurs").child("Patients");
                clientRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(currentUid)) {

                            // cet utilisateur est un patient

                            dialogue = new Dialog(context);
                            dialogue.setContentView(R.layout.commander_medicament_dialog);
                            dialogue.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                Drawable draw = context.getResources().getDrawable(R.drawable.rounded_corner_white);
                                dialogue.getWindow().setBackgroundDrawable(draw);
                            }
                            dialogue.setCancelable(false);

                            Button commanderBtn = dialogue.findViewById(R.id.commander_btn);
                            ImageView annuler = dialogue.findViewById(R.id.annuler_det_dialogue);
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

                            annuler.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogue.dismiss();
                                }
                            });

                            commanderBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    FirebaseDatabase bdd = FirebaseDatabase.getInstance();
                                    DatabaseReference medRef = bdd.getReference().child("Utilisateurs").child("Patients")
                                            .child(currentUid).child("Panier").child(current);

                                    HashMap<String, Object> medMap = new HashMap<>();
                                    medMap.put("nom", medsList.get(medViewHolder.getAdapterPosition()).getNom());
                                    medMap.put("code", medsList.get(medViewHolder.getAdapterPosition()).getCode());
                                    medMap.put("prix", medsList.get(medViewHolder.getAdapterPosition()).getPrix());
                                    medMap.put("quantité", medsList.get(medViewHolder.getAdapterPosition()).getQuantité());
                                    medMap.put("date_de_fabrication", medsList.get(medViewHolder.getAdapterPosition()).getDate_de_fabrication());
                                    medMap.put("date_dexpiration", medsList.get(medViewHolder.getAdapterPosition()).getDate_dexpiration());

                                    medRef.updateChildren(medMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context, current + " est ajouté à votre panier", Toast.LENGTH_LONG).show();
                                            dialogue.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Une erreur d'est produite", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            });

                        } else {

                            //prestataire

                            dialogue = new Dialog(context);
                            dialogue.setContentView(R.layout.details_de_medicament_dialog);
                            dialogue.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                Drawable draw = context.getResources().getDrawable(R.drawable.rounded_corner_white);
                                dialogue.getWindow().setBackgroundDrawable(draw);
                            }
                            dialogue.setCancelable(false);

                            Button modfierBtn = dialogue.findViewById(R.id.modifier_btn);
                            Button supprimerBtn = dialogue.findViewById(R.id.supprimer_btn);
                            ImageView annuler = dialogue.findViewById(R.id.annuler_det_dialogue);
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

                            annuler.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogue.dismiss();
                                }
                            });

                            modfierBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogue.setContentView(R.layout.modifier_medicament_dialog);
                                    dialogue.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        Drawable draw = context.getResources().getDrawable(R.drawable.rounded_corner_white);
                                        dialogue.getWindow().setBackgroundDrawable(draw);
                                    }

                                    dialogue.setCancelable(false);

                                    Button annulerBtn = dialogue.findViewById(R.id.modif_annuler_btn);
                                    Button confirmerBtn = dialogue.findViewById(R.id.modif_confirmer_btn);
                                    modifNomMédicament = dialogue.findViewById(R.id.modifNomMédicament);
                                    modifNomMédicament.getEditText().setText(nomMédicament.getText());
                                    modifCode = dialogue.findViewById(R.id.modifCode);
                                    modifCode.getEditText().setText(code.getText());
                                    modifPrix = dialogue.findViewById(R.id.modifPrix);
                                    modifPrix.getEditText().setText(prix.getText());
                                    modifQuantité = dialogue.findViewById(R.id.modifQuantité);
                                    modifQuantité.getEditText().setText(quantité.getText());
                                    modifDate_de_fabrication = dialogue.findViewById(R.id.modifDate_de_fabrication);
                                    modifDate_de_fabrication.getEditText().setText(date_de_fabrication.getText());
                                    modifDate_dexpiration = dialogue.findViewById(R.id.modifDate_dexpiration);
                                    modifDate_dexpiration.getEditText().setText(date_dexpiration.getText());

                                    dialogue.show();

                                    annulerBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialogue.dismiss();
                                        }
                                    });

                                    confirmerBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            modifierMédicament();
                                        }
                                    });

                                }
                            });

                            supprimerBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(medViewHolder.itemView.getContext());
                                    builder.setTitle("Voulez vous vraiment supprimer ce medicament?");
                                    builder.setMessage("Si vous cliquez sur Oui, le medicament ne sera plus disponible dans la liste");
                                    builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            supprimerMed();
                                            Toast.makeText(context, "Médicament supprimé", Toast.LENGTH_LONG).show();
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

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }

                });

            }

            private void supprimerMed() {

                int position = medViewHolder.getAdapterPosition();
                FirebaseDatabase.getInstance().getReference().child("Médicaments").child(medsList.get(position).getNom())
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    notifyDataSetChanged();
                                }
                            }
                        });

            }

            private void modifierMédicament() {
                nomMedTexte = modifNomMédicament.getEditText().getText().toString();
                prixTexte = modifPrix.getEditText().getText().toString();
                dateFabTexte = modifDate_de_fabrication.getEditText().getText().toString();
                dateExpTexte = modifDate_dexpiration.getEditText().getText().toString();
                quantitéTexte = modifQuantité.getEditText().getText().toString();
                codeTexte = modifCode.getEditText().getText().toString();

                if (nomMedTexte.isEmpty()) {
                    modifNomMédicament.setError("Champ obligatoire");
                    return;
                }


                if (codeTexte.isEmpty()) {
                    modifCode.setError("Champ obligatoire");
                    return;
                }

                if (codeTexte.length() < 8) {
                    modifCode.setError("Le code est trop court, il doit contenir plus de 8 chiffres");
                    return;
                }

                if (prixTexte.isEmpty()) {
                    modifPrix.setError("Champ obligatoire");
                    return;
                }

                if (quantitéTexte.isEmpty()) {
                    modifQuantité.setError("Champ obligatoire");
                    return;
                }

                int qtt = Integer.parseInt(quantitéTexte);

                if (qtt < 0) {
                    modifQuantité.setError("Vérifiez la quantité");
                    return;
                }

                if (dateFabTexte.isEmpty()) {
                    modifDate_de_fabrication.setError("Champ obligatoire");
                    return;
                }

                String[] dateFabTable = dateFabTexte.split("/");

                if (dateFabTable.length > 3 || dateFabTable.length < 3) {
                    modifDate_de_fabrication.setError("Format incorrect, vous devez entrer jj/mm/aaaa");
                    return;
                }

                int jourFab = Integer.parseInt(dateFabTable[0]);
                int moisFab = Integer.parseInt(dateFabTable[1]);
                int anneeFab = Integer.parseInt(dateFabTable[2]);

                if (jourFab < 1 || jourFab > 31) {
                    modifDate_de_fabrication.setError("Vérifiez le jour de fabrication");
                    return;
                }

                if (moisFab < 1 || moisFab > 12) {
                    modifDate_de_fabrication.setError("Vérifiez le mois de fabrication");
                    return;
                }

                if (anneeFab > 2023 || anneeFab < 2015) {
                    modifDate_de_fabrication.setError("Vérifiez l'année de fabrication");
                    return;
                }

                if (dateExpTexte.isEmpty()) {
                    modifDate_dexpiration.setError("Champ obligatoire");
                    return;
                }

                String[] dateExpTable = dateExpTexte.split("/");

                if (dateExpTable.length > 3 || dateExpTable.length < 3) {
                    modifDate_dexpiration.setError("Format incorrect, vous devez entrer jj/mm/aaaa");
                    return;
                }

                int jourExp = Integer.parseInt(dateExpTable[0]);
                int moisExp = Integer.parseInt(dateExpTable[1]);
                int anneeExp = Integer.parseInt(dateExpTable[2]);

                if (jourExp < 1 || jourExp > 31) {
                    modifDate_dexpiration.setError("Vérifiez le jour d'expiration");
                    return;
                }

                if (moisExp < 1 || moisExp > 12) {
                    modifDate_dexpiration.setError("Vérifiez le mois d'expiration");
                    return;
                }

                if (anneeExp > 2040 || anneeExp < 2023) {
                    modifDate_dexpiration.setError("Vérifiez l'année d'expiration");
                    return;
                }

                FirebaseDatabase bdd = FirebaseDatabase.getInstance();
                DatabaseReference medRef = bdd.getReference().child("Médicaments").child(nomMedTexte);

                HashMap<String, Object> medMap = new HashMap<>();
                medMap.put("nom", nomMedTexte);
                medMap.put("code", codeTexte);
                medMap.put("prix", prixTexte);
                medMap.put("quantité", quantitéTexte);
                medMap.put("date_de_fabrication", dateFabTexte);
                medMap.put("date_dexpiration", dateExpTexte);

                supprimerMed();

                medRef.updateChildren(medMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Informations modifiées", Toast.LENGTH_LONG).show();
                        dialogue.dismiss();
                    }
                });
            }

        });

        return medViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MedicamentViewHolder viewHolder = (MedicamentViewHolder) holder;

        Medicament med = medsList.get(position);
        viewHolder.nom.setText(med.getNom());
        viewHolder.prix.setText(med.getPrix());
        viewHolder.quantité.setText(med.getQuantité());

    }

    @Override
    public int getItemCount() {
        return medsList.size();
    }

    public class MedicamentViewHolder extends RecyclerView.ViewHolder {

        public TextView nom, quantité, prix;

        public MedicamentViewHolder(@NonNull View itemView) {

            super(itemView);
            nom = itemView.findViewById(R.id.nom_medd);
            prix = itemView.findViewById(R.id.prix_medd);
            quantité = itemView.findViewById(R.id.quantité_medd);

        }
    }

}

