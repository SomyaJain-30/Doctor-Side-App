package com.example.doctorside;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorFormPage extends AppCompatActivity {

    Button doctorNext;
    Button doctorPrevious;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    DocumentReference documentReference;
    StorageReference storageReference;
    FormFragment1 obj1 = null;
    FormFragment2 obj2 = null;
    FormFragment3 obj3 = null;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_form_page);
        doctorNext = (Button) findViewById(R.id.doctor_form_button_next);
        doctorPrevious = findViewById(R.id.doctor_form_button_previous);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        replaceFragment(new FormFragment1());



        doctorNext.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void onClick(View v) {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.form_frame_layout);
                    if(fragment instanceof FormFragment1) {
                        if (((FormFragment1) fragment).checkValues()) {
                            obj1 = (FormFragment1) fragment;
                            if(obj2 != null){
                                replaceFragment(obj2);
                            }else{
                                replaceFragment(new FormFragment2());
                            }
                            doctorPrevious.setVisibility(View.VISIBLE);
                        }
                    }else if(fragment instanceof FormFragment2){
                        if (((FormFragment2) fragment).checkValues()) {
                            obj2 = (FormFragment2) fragment;
                            if(obj3 != null){
                                replaceFragment(obj3);
                            }else{
                                replaceFragment(new FormFragment3());
                            }
                            doctorNext.setText("Save");
                            doctorPrevious.setVisibility(View.VISIBLE);
                        }
                    }else if(fragment instanceof FormFragment3){
                        obj3 = (FormFragment3) fragment;
                        if(((FormFragment3) fragment).checkValues()){
                            sendDatatoFireStore();
                            Intent i = new Intent(DoctorFormPage.this, DoctorHomePage.class);
                            startActivity(i);
                            finish();
                        }

                    }
                }
        });
        doctorPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.form_frame_layout);
                if(fragment instanceof FormFragment2){
                    replaceFragment(obj1);
                    doctorPrevious.setVisibility(View.INVISIBLE);
                }else if(fragment instanceof FormFragment3){
                    replaceFragment(obj2);
                    doctorNext.setText("Next");
                }
            }
        });
    }

//    public void senddatatofirestore(){
//        documentReference = firebaseFirestore.collection("Doctors").document(firebaseAuth.getCurrentUser().getPhoneNumber());
//        if(!obj1.doctorName.getText().toString().isEmpty()){
//            documentReference.update("Name", obj1.doctorName.getText().toString());
//        }
//        if(!obj1.doctorClinicAddress.getText().toString().isEmpty()){
//            documentReference.update("Clinic Address", obj1.doctorClinicAddress.getText().toString());
//        }
//        if(!obj1.doctorEmail.getText().toString().isEmpty()){
//            documentReference.update("E-mail address", obj1.doctorEmail.getText().toString());
//        }
//        if(!obj2doctorEducation.getText().toString().isEmpty()){
//            documentReference.update("Education" , doctorEducation.getText().toString());
//        }
//        if(!doctorExprience.getText().toString().isEmpty()){
//            documentReference.update("Exprience" , doctorExprience.getText().toString());
//        }
//        documentReference.update("Gender" , ((RadioButton)findViewById(obj1.radioGroupDoctor.getCheckedRadioButtonId())).getText().toString());
//        documentReference.update("Profile URL" , "");
//    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void sendDatatoFireStore(){
        DocumentReference documentReference = firebaseFirestore.collection("Doctors").document(firebaseAuth.getCurrentUser().getPhoneNumber());
        Map<String, Object> doctordata = new HashMap<>();

        doctordata.put("Name", obj1.doctorName.getText().toString());
        doctordata.put("Clinic Address",obj1.doctorClinicAddress.getText().toString());
        doctordata.put("E-mail address" , obj1.doctorEmail.getText().toString());
        doctordata.put("Gender" , obj1.gender);

        doctordata.put("Education" , obj2.doctorEducation.getText().toString());
        doctordata.put("Experience", obj2.doctorExperience.getText().toString());
        doctordata.put("Role", obj2.doctorRole.getText().toString());
        List<String> area= new ArrayList<>();
        if(obj2.checkBox1.isChecked()){
            area.add(obj2.checkBox1.getText().toString());
        }
        if(obj2.checkBox2.isChecked()){
            area.add(obj2.checkBox2.getText().toString());
        }
        if(obj2.checkBox3.isChecked()){
            area.add(obj2.checkBox3.getText().toString());
        }
        if(obj2.checkBox4.isChecked()){
            area.add(obj2.checkBox4.getText().toString());
        }
        if(obj2.checkBox5.isChecked()){
            area.add(obj2.checkBox5.getText().toString());
        }
        doctordata.put("Specialization", area);
        doctordata.put("Language", obj2.langs);
        doctordata.put("About", obj2.doctorAbout.getText().toString());
        doctordata.put("SurveyData", obj3.response);

        documentReference.set(doctordata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(DoctorFormPage.this, "Details Saved Successfully", Toast.LENGTH_SHORT).show();
            }
        });
        storageReference.child("Doctor").child(firebaseAuth.getCurrentUser().getPhoneNumber()).child("Profile").putFile(obj1.selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child("Doctor").child(firebaseAuth.getCurrentUser().getPhoneNumber()).child("Profile").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        documentReference.update("Profile URL", uri);
                    }
                });
            }
        });

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.form_frame_layout,fragment);
        fragmentTransaction.commit();
    }
}