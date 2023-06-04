package com.example.e_sbitar.bottom_navigation_patient;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.e_sbitar.Connexion;
import com.example.e_sbitar.R;
import com.example.e_sbitar.medicaments.Medicament;
import com.example.e_sbitar.panier.PanierAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class PatientPanierFragment extends Fragment {

    RecyclerView medsRecycler;
    LinearLayoutManager manager;

    PanierAdapter medAdapter;

    Button lancerCommandeBtn;
    ImageView retourBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_fragment_panier, container, false);

        Button deconnexion = view.findViewById(R.id.dec14);

        deconnexion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Context context = getContext();
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(context, "Déconnexion", Toast.LENGTH_LONG).show();

                // Finish the current activity to go back to the previous fragment or activity
                getActivity().finish();

                // Start a new activity to go to the "connexion" page
                Intent intent = new Intent(context, Connexion.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the activity stack
                startActivity(intent);

            }

        });

        lancerCommandeBtn = view.findViewById(R.id.lancer_commande_btn);
        retourBtn = view.findViewById(R.id.retour_btn);

        medsRecycler = view.findViewById(R.id.meds_recycler);
        medsRecycler.setHasFixedSize(true);

        Context ctx = getContext();

        manager = new LinearLayoutManager(ctx);
        medsRecycler.setLayoutManager(manager);

        medAdapter = new PanierAdapter(ctx);
        medsRecycler.setAdapter(medAdapter);

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


        lancerCommandeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("lancer la commande ?");
                builder.setMessage("");
                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        lancerCommande();
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

        retourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new PatientMedicamentsFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.med_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;

    }

    private void lancerCommande() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = user.getUid();

        // Récupérer la liste des médicaments dans le panier
        ArrayList<Medicament> medsList = medAdapter.getItems();

        // Créer une hashmap pour stocker les informations de chaque médicament
        HashMap<String, Object> medsMap = new HashMap<>();

        for (int i = 0; i < medsList.size(); i++) {
            Medicament med = medsList.get(i);

            HashMap<String, Object> medInfo = new HashMap<>();
            medInfo.put("nom", med.getNom());
            medInfo.put("prix", med.getPrix());
            medInfo.put("quantité", med.getQuantité());

            medsMap.put("med" + (i+1), medInfo);
        }

        // Enregistrer la commande dans la base de données
        DatabaseReference commandesRef = FirebaseDatabase.getInstance().getReference().child("Commandes");
        String commandeId = commandesRef.push().getKey();

        // Get a reference to the "nom_prénom" value in the Firebase Realtime Database
        DatabaseReference patientRef = FirebaseDatabase.getInstance().getReference()
                .child("Utilisateurs")
                .child("Patients")
                .child(currentUid)
                .child("nom_prénom");

        // Add a ValueEventListener to retrieve the value
        patientRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String nomPrenom = dataSnapshot.getValue(String.class);

                    HashMap<String, Object> commandeInfo = new HashMap<>();
                    commandeInfo.put("medicaments", medsMap);
                    commandeInfo.put("patient", nomPrenom);
                    commandeInfo.put("etat", "En attente");
                    commandeInfo.put("code", commandeId);

                    commandesRef.child(commandeId).setValue(commandeInfo)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Supprimer les médicaments du panier
                                    DatabaseReference panierRef = FirebaseDatabase.getInstance().getReference().child("Utilisateurs").child("Patients").child(currentUid).child("Panier");
                                    panierRef.removeValue();

                                    // Afficher un message de confirmation
                                    Toast.makeText(getContext(), "La commande a été lancée avec succès", Toast.LENGTH_SHORT).show();


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Afficher un message d'erreur
                                    Toast.makeText(getContext(), "Une erreur s'est produite lors de la création de la commande", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that may occur
            }
        });

    }


    private void loadMedsData() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = user.getUid();
        DatabaseReference clientRef = FirebaseDatabase.getInstance().getReference().child("Utilisateurs").child("Patients");

        clientRef.child(currentUid).child("Panier").orderByKey().addValueEventListener(new ValueEventListener() {

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



}