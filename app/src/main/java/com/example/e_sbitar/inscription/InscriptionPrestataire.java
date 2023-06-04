package com.example.e_sbitar.inscription;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.e_sbitar.accueil.AccueilPrestataire;
import com.example.e_sbitar.Connexion;
import com.example.e_sbitar.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class InscriptionPrestataire extends AppCompatActivity {
    TextInputLayout nom_prenom, email, mdp, confirmer_mdp, tel, clé;
    String nom_prenomTexte, emailTexte, mdpTexte, confirmer_mdpTexte, telTexte, cléTexte;
    ImageView retourBtn;
    Button inscriptionBtn;
    String cléPrestataireVérif = "#prestataire%acsi";

    FirebaseAuth fAuth;
    FirebaseUser prestataire;
    String uidPrestataire;
    FirebaseDatabase bdd;
    DatabaseReference prestataireRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription_prestataire);

        nom_prenom = findViewById(R.id.nom_prénom);
        email = findViewById(R.id.email);
        mdp = findViewById(R.id.mdp);
        confirmer_mdp = findViewById(R.id.confirmer_mdp);
        tel = findViewById(R.id.telephone);
        clé = findViewById(R.id.clé_prestataire);

        retourBtn = findViewById(R.id.retour_btn);
        inscriptionBtn = findViewById(R.id.inscription_btn);

        retourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InscriptionPrestataire.this, Connexion.class);
                startActivity(intent);
            }
        });

        inscriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inscriptionUtilisateurPrestataire();
            }
        });

    }

    public void inscriptionUtilisateurPrestataire() {

        nom_prenomTexte = nom_prenom.getEditText().getText().toString();
        emailTexte = email.getEditText().getText().toString();
        mdpTexte = mdp.getEditText().getText().toString();
        confirmer_mdpTexte = confirmer_mdp.getEditText().getText().toString();
        telTexte = tel.getEditText().getText().toString();
        cléTexte = clé.getEditText().getText().toString();

        nom_prenom.setError(null);
        email.setError(null);
        mdp.setError(null);
        confirmer_mdp.setError(null);
        tel.setError(null);
        clé.setError(null);

        if (nom_prenomTexte.isEmpty()) {
            nom_prenom.setError("Champ obligatoire");
            return;
        }

        if (emailTexte.isEmpty()) {
            email.setError("Champ obligatoire");
            return;
        }

        if (mdpTexte.isEmpty()) {
            mdp.setError("Champ obligatoire");
            return;
        }

        if (mdpTexte.length()<8) {
            mdp.setError("Le mot de passe doit contenir plus de 7 caractères");
            return;
        }

        if (confirmer_mdpTexte.isEmpty()) {
            confirmer_mdp.setError("Champ obligatoire");
            return;
        }

        if (confirmer_mdpTexte.length()<8){
            confirmer_mdp.setError("Le mot de passe doit contenir plus de 7 caractères");
            return;
        }

        if (!mdpTexte.equals(confirmer_mdpTexte)) {
            mdp.setError("Le mot de passe et la confirmation doivent être identiques");
            confirmer_mdp.setError("Le mot de passe et la confirmation doivent être identiques");
            return;
        }

        if (telTexte.isEmpty()) {
            tel.setError("Champ obligatoire");
            return;
        }

        if (telTexte.length()<10){
            tel.setError("Numéro de téléphone trop court");
        }

        if (cléTexte.isEmpty()){
            clé.setError("Champ obligatoire");
            return;
        }

        if (!cléTexte.equals(cléPrestataireVérif)){
            clé.setError("Clé erronée");
            return;
        }


        fAuth = FirebaseAuth.getInstance();
        fAuth.createUserWithEmailAndPassword(emailTexte, mdpTexte).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                prestataire = FirebaseAuth.getInstance().getCurrentUser();
                uidPrestataire = prestataire.getUid();

                bdd = FirebaseDatabase.getInstance();
                prestataireRef = bdd.getReference().child("Utilisateurs").child("Prestataires").child(uidPrestataire);

                HashMap<String, Object> inscriptionMap = new HashMap<>();
                inscriptionMap.put("Identifiant", uidPrestataire);
                inscriptionMap.put("Nom et prénom", nom_prenomTexte);
                inscriptionMap.put("Adresse email", emailTexte);
                inscriptionMap.put("Mot de passe", mdpTexte);
                inscriptionMap.put("Numéro de téléphone", telTexte);
                inscriptionMap.put("Clé d'administrateur", cléTexte);

                prestataireRef.updateChildren(inscriptionMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(InscriptionPrestataire.this, AccueilPrestataire.class);
                        startActivity(intent);
                        Toast.makeText(InscriptionPrestataire.this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InscriptionPrestataire.this, "Inscription échouée", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InscriptionPrestataire.this, "Un promblème s'est arrivé", Toast.LENGTH_SHORT).show();
            }
        });

    }
}