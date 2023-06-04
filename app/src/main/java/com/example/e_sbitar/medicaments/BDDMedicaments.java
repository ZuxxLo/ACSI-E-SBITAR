package com.example.e_sbitar.medicaments;

import com.example.e_sbitar.medicaments.Medicament;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class BDDMedicaments {

    DatabaseReference databaseReference;

    public BDDMedicaments(){

        FirebaseDatabase bdd = FirebaseDatabase.getInstance();
        databaseReference = bdd.getReference().child("Médicaments");

    }

    public Task<Void> add (Medicament médicament){

        return databaseReference.push().setValue(médicament);

    }

    public Query get (){

        return databaseReference.orderByValue();

    }

}