package com.example.e_sbitar.commandes;

import com.example.e_sbitar.medicaments.Medicament;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class BDDCommandes {

    DatabaseReference databaseReference;

    public BDDCommandes(){

        FirebaseDatabase bdd = FirebaseDatabase.getInstance();
        databaseReference = bdd.getReference().child("Commandes");

    }

    public Task<Void> add (Commande commande){

        return databaseReference.push().setValue(commande);

    }

    public Query get (){

        return databaseReference.orderByValue();

    }

}