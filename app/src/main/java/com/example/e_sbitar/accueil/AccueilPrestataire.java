package com.example.e_sbitar.accueil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.e_sbitar.R;
import com.example.e_sbitar.bottom_navigation_prestataire.PrestataireMedicamentsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccueilPrestataire extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil_prestataire);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.medicaments);

    }

    PrestataireMedicamentsFragment prestataireMedicamentsFragment = new PrestataireMedicamentsFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {


            case R.id.medicaments:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, prestataireMedicamentsFragment).commit();
                return true;


        }

        return false;

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

}
