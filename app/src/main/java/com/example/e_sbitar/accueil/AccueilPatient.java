package com.example.e_sbitar.accueil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.e_sbitar.R;
import com.example.e_sbitar.bottom_navigation_patient.PatientMedicamentsFragment;
import com.example.e_sbitar.bottom_navigation_patient.PatientPanierFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccueilPatient extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil_patient);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.medicaments);

    }

    PatientMedicamentsFragment patientMedicamentsFragment = new PatientMedicamentsFragment();
    PatientPanierFragment patientPanierFragment = new PatientPanierFragment();


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {


            case R.id.medicaments:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, patientMedicamentsFragment).commit();
                return true;

            case R.id.panier:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, patientPanierFragment).commit();
                return true;
        }

        return false;

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

}
