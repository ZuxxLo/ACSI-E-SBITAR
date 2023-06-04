package com.example.e_sbitar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_sbitar.accueil.AccueilPatient;
import com.example.e_sbitar.accueil.AccueilPrestataire;
import com.example.e_sbitar.inscription.InscriptionPatient;
import com.example.e_sbitar.inscription.InscriptionPrestataire;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Connexion extends AppCompatActivity {

    TextInputLayout email, mdp;
    String emailTexte, mdpTexte;
    TextView insciez_vous  ;
    Button connexionBtn;
    Dialog dialogue;

    FirebaseAuth fAuth;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);





        //liaison des variables déclarées avec leurs apparition dans l'interface
        email = findViewById(R.id.email);
        mdp = findViewById(R.id.mdp);
        insciez_vous = findViewById(R.id.inscription);
        connexionBtn = findViewById(R.id.connexion_btn);



        connexionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connexionUtilisateur();
            }
        });

        //dialogue pour demander à l'utilisateur s'il est un patient ou un prestataire de services
        dialogue = new Dialog(Connexion.this);
        dialogue.setContentView(R.layout.inscription_dialog);
        dialogue.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialogue.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corner_white));
        }
        dialogue.setCancelable(false);

        //boutons du dialogue d'inscription
        Button patientBtn = dialogue.findViewById(R.id.patient_btn);
        Button prestataireBtn = dialogue.findViewById(R.id.prestataire_btn);
        ImageView annuler = dialogue.findViewById(R.id.annuler_dialogue);

        insciez_vous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogue.show();
            }
        });

        patientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Connexion.this, InscriptionPatient.class);
                startActivity(intent);
            }
        });

        prestataireBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Connexion.this, InscriptionPrestataire.class);
                startActivity(intent);
            }
        });

        annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogue.dismiss();
            }
        });

    }

    private void connexionUtilisateur() {

        emailTexte = email.getEditText().getText().toString();
        mdpTexte = mdp.getEditText().getText().toString();

        //supprimer les erreurs précédentes pour effectuer un nouveau test
        email.setError(null);
        mdp.setError(null);

        if (emailTexte.isEmpty()) {
            email.setError("Champ obligatoire");
        }

        if (mdpTexte.isEmpty()) {
            mdp.setError("Champ obligatoire");
            return; //pour quitter la fonction onclick
        }

        fAuth = FirebaseAuth.getInstance();
        fAuth.signInWithEmailAndPassword(emailTexte, mdpTexte).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                //vérifier si c'est un client ou un livreur
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUid = user.getUid();
                DatabaseReference clientRef = FirebaseDatabase.getInstance().getReference().child("Utilisateurs").child("Patients");
                clientRef.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(currentUid)) {

                            // cet utilisateur est un patient
                            Intent intent = new Intent(Connexion.this, AccueilPatient.class);
                            startActivity(intent);
                            finish();

                        } else {

                            //cet utilisateur est un prestataire de services
                            Intent intent = new Intent(Connexion.this, AccueilPrestataire.class);
                            startActivity(intent);
                            finish();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }

                });

                Toast.makeText(Connexion.this, "Connexion réussie", Toast.LENGTH_SHORT).show();

            }

        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(Connexion.this, "Vérifiez vos coordonnées", Toast.LENGTH_SHORT).show();

            }

        });

    }

}