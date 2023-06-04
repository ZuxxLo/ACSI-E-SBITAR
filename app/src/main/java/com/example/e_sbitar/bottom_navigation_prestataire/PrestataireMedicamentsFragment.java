package com.example.e_sbitar.bottom_navigation_prestataire;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.e_sbitar.R;
import com.example.e_sbitar.medicaments.BDDMedicaments;
import com.example.e_sbitar.medicaments.Medicament;
import com.example.e_sbitar.medicaments.MedicamentAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class PrestataireMedicamentsFragment extends Fragment {

    public PrestataireMedicamentsFragment(){
        // require a empty public constructor
    }

    RecyclerView medsRecycler;

    BDDMedicaments bddMédicament;
    MedicamentAdapter medAdapter;

    TextInputLayout nomMédicament, quantité, code, prix, date_de_fabrication, date_dexpiration;

    String nomMedTexte, prixTexte, codeTexte, dateFabTexte, dateExpTexte, quantitéTexte;

    ImageButton ajouterMed;

    Dialog dialogue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.prestataire_fragment_medicaments, container, false);

        medsRecycler = view.findViewById(R.id.meds_recycler);
        medsRecycler.setHasFixedSize(true);

        Context ctx = getContext();

        LinearLayoutManager manager = new LinearLayoutManager(ctx);
        medsRecycler.setLayoutManager(manager);

        medAdapter = new MedicamentAdapter(ctx);
        medsRecycler.setAdapter(medAdapter);
        bddMédicament = new BDDMedicaments();

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.med_swiper);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadMedsData();
                medAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        loadMedsData();

        ajouterMed = view.findViewById(R.id.ajouter_med_btn);

        dialogue = new Dialog(ctx);
        dialogue.setContentView(R.layout.ajout_de_medicamnet_dialog);
        dialogue.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialogue.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.rounded_corner_white));
        }
        dialogue.setCancelable(false);

        Button annulerBtn = dialogue.findViewById(R.id.annuler_btn);
        Button confirmerBtn = dialogue.findViewById(R.id.confirmer_btn);
        nomMédicament = dialogue.findViewById(R.id.nom_med);
        code = dialogue.findViewById(R.id.code_med);
        prix = dialogue.findViewById(R.id.prix_med);
        quantité = dialogue.findViewById(R.id.quantité_med);
        date_de_fabrication = dialogue.findViewById(R.id.date_fab);
        date_dexpiration = dialogue.findViewById(R.id.date_exp);

        ajouterMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogue.show();
            }
        });

        annulerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogue.dismiss();
            }
        });

        confirmerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ajouterMédicament();
            }
        });

        return view;

    }

    public void loadMedsData() {

        bddMédicament.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<Medicament> othMeds = new ArrayList<>();

                for (DataSnapshot data : snapshot.getChildren()){
                    Medicament med;
                    med = data.getValue(Medicament.class);
                    othMeds.add(med);
                }

                medAdapter.setItems(othMeds);
                medAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void ajouterMédicament() {

        nomMedTexte = nomMédicament.getEditText().getText().toString();
        prixTexte = prix.getEditText().getText().toString();
        dateFabTexte = date_de_fabrication.getEditText().getText().toString();
        dateExpTexte = date_dexpiration.getEditText().getText().toString();
        quantitéTexte = quantité.getEditText().getText().toString();
        codeTexte = code.getEditText().getText().toString();

        nomMédicament.setError(null);
        code.setError(null);
        prix.setError(null);
        date_de_fabrication.setError(null);
        date_dexpiration.setError(null);
        quantité.setError(null);

        if (nomMedTexte.isEmpty()) {
            nomMédicament.setError("Champ obligatoire");
            return;
        }

        if (codeTexte.isEmpty()) {
            code.setError("Champ obligatoire");
            return;
        }

        if (codeTexte.length() < 8) {
            code.setError("Le code est trop court, il doit contenir plus de 8 chiffres");
            return;
        }

        if (prixTexte.isEmpty()) {
            prix.setError("Champ obligatoire");
            return;
        }

        if (quantitéTexte.isEmpty()) {
            quantité.setError("Champ obligatoire");
            return;
        }

        int qtt = Integer.parseInt(quantitéTexte);

        if (qtt < 0){
            quantité.setError("Vérifiez la quantité");
            return;
        }

        if (dateFabTexte.isEmpty()) {
            date_de_fabrication.setError("Champ obligatoire");
            return;
        }

        String[] dateFabTable = dateFabTexte.split("/");

        if (dateFabTable.length>3 || dateFabTable.length<3){
            date_de_fabrication.setError("Format incorrect, vous devez entrer jj/mm/aaaa");
            return;
        }

        int jourFab = Integer.parseInt(dateFabTable[0]);
        int moisFab = Integer.parseInt(dateFabTable[1]);
        int anneeFab = Integer.parseInt(dateFabTable[2]);

        if (jourFab < 1 || jourFab > 31) {
            date_de_fabrication.setError("Vérifiez le jour de fabrication");
            return;
        }

        if (moisFab < 1 || moisFab > 12) {
            date_de_fabrication.setError("Vérifiez le mois de fabrication");
            return;
        }

        if (anneeFab > 2023 || anneeFab < 2015) {
            date_de_fabrication.setError("Vérifiez l'année de fabrication");
            return;
        }

        if (dateExpTexte.isEmpty()) {
            date_dexpiration.setError("Champ obligatoire");
            return;
        }

        String[] dateExpTable = dateExpTexte.split("/");

        if (dateExpTable.length>3 || dateExpTable.length<3){
            date_de_fabrication.setError("Format incorrect, vous devez entrer jj/mm/aaaa");
            return;
        }

        int jourExp = Integer.parseInt(dateExpTable[0]);
        int moisExp = Integer.parseInt(dateExpTable[1]);
        int anneeExp = Integer.parseInt(dateExpTable[2]);

        if (jourExp < 1 || jourExp > 31) {
            date_dexpiration.setError("Vérifiez le jour d'expiration");
            return;
        }

        if (moisExp < 1 || moisExp > 12) {
            date_dexpiration.setError("Vérifiez le mois d'expiration");
            return;
        }

        if (anneeExp > 2040 || anneeExp < 2023) {
            date_dexpiration.setError("Vérifiez l'année d'expiration");
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

        Toast.makeText(getContext(), "Ajout en cours...", Toast.LENGTH_SHORT).show();

        medRef.updateChildren(medMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(), "Médicament ajouté", Toast.LENGTH_SHORT).show();
                dialogue.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Opération échouée", Toast.LENGTH_SHORT).show();
            }
        });


    }

}