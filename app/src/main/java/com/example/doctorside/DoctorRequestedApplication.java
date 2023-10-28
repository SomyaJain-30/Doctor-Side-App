package com.example.doctorside;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class DoctorRequestedApplication extends AppCompatActivity {
    RecyclerView rv;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    List<String> ids;
    ArrayList<AppointmentDetail> appointmentDetailArrayList = new ArrayList<>();
    RequestedApplicationAdapter requestedApplicationAdapter;
    int cnt=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_requested_application);
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        appointmentDetailArrayList = new ArrayList<>();

        firebaseFirestore.collection("Appointments").whereEqualTo("Did", firebaseAuth.getCurrentUser().getPhoneNumber()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot d : queryDocumentSnapshots) {
                    if (d.get("Status").toString().equals("Requested")) {
                        cnt++;
                        AppointmentDetail appointmentDetail = new AppointmentDetail();
                        appointmentDetail.setAppointmentId(d.getId());
                        appointmentDetail.setDate(d.get("Date").toString());
                        appointmentDetail.setDay(d.get("Day").toString());
                        appointmentDetail.setTime(d.get("TimeSlot").toString());
                        appointmentDetail.setCid(d.get("Cid").toString());
                        appointmentDetail.setDid(d.get("Did").toString());
                        appointmentDetail.setStatus(d.get("Status").toString());
                        firebaseFirestore.collection("Patients").document(appointmentDetail.getCid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                appointmentDetail.setPatientName(documentSnapshot.get("Name").toString());
                                appointmentDetail.setPatientUrl(documentSnapshot.get("Profile URL").toString());
                                appointmentDetailArrayList.add(appointmentDetail);
                                if (cnt == appointmentDetailArrayList.size()) {
                                    requestedApplicationAdapter = new RequestedApplicationAdapter(DoctorRequestedApplication.this, appointmentDetailArrayList);
                                    rv.setAdapter(requestedApplicationAdapter);
                                }
                            }
                        });
                    }
                }
            }

        });
    }
}