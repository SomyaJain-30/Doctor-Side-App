package com.example.doctorside;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class DoctorFormPage2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_form_page2);
        Intent i = getIntent();
        String name = i.getStringExtra("name");
        String address = i.getStringExtra("address");
        String email = i.getStringExtra("email");
        String gender = i.getStringExtra("gender");
    }
}