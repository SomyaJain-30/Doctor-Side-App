package com.example.doctorside;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ManageSlotFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ImageView lock;
    boolean locked;
    WeeklySlotsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RecyclerView rv;
        View v =  inflater.inflate(R.layout.fragment_manage_slot, container, false);
        rv = (RecyclerView) v.findViewById(R.id.rv);
        lock = v.findViewById(R.id.lock);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        locked = true;
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        ArrayList<Slots> sortedSlotsList = new ArrayList<>();
        firebaseFirestore.collection("Doctors").document(firebaseAuth.getCurrentUser().getPhoneNumber())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                        Map<String, List<String>> slotsMap = (Map<String, List<String>>) documentSnapshot.get("Slots");
                        if(slotsMap==null)
                        {
                            slotsMap = new HashMap<>();
                            slotsMap.put("Monday", new ArrayList<>());
                            slotsMap.put("Tuesday", new ArrayList<>());
                            slotsMap.put("Wednesday", new ArrayList<>());
                            slotsMap.put("Thursday", new ArrayList<>());
                            slotsMap.put("Friday", new ArrayList<>());
                            slotsMap.put("Saturday", new ArrayList<>());
                            slotsMap.put("Sunday", new ArrayList<>());
                        }


                        for (String day : daysOfWeek) {
                            if (slotsMap.containsKey(day)) {
                                sortedSlotsList.add(new Slots(day, slotsMap.get(day)));
                            }
                        }

                        adapter = new WeeklySlotsAdapter(getContext(), sortedSlotsList, locked);
                        rv.setAdapter(adapter);
                    }
                });

        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locked = !locked;
                if(locked)
                    lock.setImageResource(R.drawable.baseline_lock_open_24);
                else
                    lock.setImageResource(R.drawable.baseline_lock_24);
                adapter = new WeeklySlotsAdapter(getContext(), sortedSlotsList, locked);
                rv.setAdapter(adapter);
            }
        });

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(adapter!=null)
        {
            ArrayList<Slots> slots = adapter.slots;
            Map<String, List<String>> slotsMap = new HashMap<>();
            for(Slots s: slots)
            {
                slotsMap.put(s.getDay(), s.getTimes());
            }
            firebaseFirestore.collection("Doctors").document(firebaseAuth.getCurrentUser().getPhoneNumber())
                    .update("Slots", slotsMap);
        }
    }
}