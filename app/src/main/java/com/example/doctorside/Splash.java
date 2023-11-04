package com.example.doctorside;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Splash extends AppCompatActivity {

    private static int splash_timer=3000;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            checkIfDoctorAlreadyExist(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        }
        else
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(Splash.this, DoctorsLogin.class);
                    startActivity(i);
                    finish();
                }
            }, splash_timer);
        }

    }

    private void checkIfDoctorAlreadyExist(String s) {

        firebaseFirestore.collection("Doctors").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                boolean flg = true;
                for(DocumentSnapshot ds: queryDocumentSnapshots){
                    if(ds.getId().equals(s)){
                        flg = false;
                        Intent i = new Intent(Splash.this, DoctorHomePage.class);
                        startActivity(i);
                        break;
                    }
                }
                if(flg == true){
                    Intent i = new Intent(Splash.this, DoctorFormPage.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                }


            }
        });
    }
}