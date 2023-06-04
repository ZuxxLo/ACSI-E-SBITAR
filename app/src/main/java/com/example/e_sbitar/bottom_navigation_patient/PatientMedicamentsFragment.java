package com.example.e_sbitar.bottom_navigation_patient;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.e_sbitar.R;
import com.example.e_sbitar.medicaments.BDDMedicaments;
import com.example.e_sbitar.medicaments.Medicament;
import com.example.e_sbitar.medicaments.MedicamentAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class PatientMedicamentsFragment extends Fragment {

    public PatientMedicamentsFragment(){
        // require a empty public constructor
    }

    RecyclerView medsRecycler;
    LinearLayoutManager manager;
    TextView panierTexte;

    MedicamentAdapter medAdapter;
    BDDMedicaments bddMédicament;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.patient_fragment_medicaments, container, false);

        medsRecycler = view.findViewById(R.id.meds_recycler);
        medsRecycler.setHasFixedSize(true);

        Context ctx = getContext();

        manager = new LinearLayoutManager(ctx);
        medsRecycler.setLayoutManager(manager);

        medAdapter = new MedicamentAdapter(ctx);
        medsRecycler.setAdapter(medAdapter);
        bddMédicament = new BDDMedicaments();



        loadMedsData();



        return view;

    }

    private void loadMedsData() {

        bddMédicament.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<Medicament> othMeds = new ArrayList<>();

                for (DataSnapshot data : snapshot.getChildren()){
                    Medicament meds;
                    meds = data.getValue(Medicament.class);
                    othMeds.add(meds);
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