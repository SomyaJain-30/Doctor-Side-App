package com.example.doctorside;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DoctorProfile extends AppCompatActivity {
    TextView doctorName;
    TextView doctorContact;
    TextView clinicAddress;
    TextView doctorEmail;
    TextView specialization;
    TextView education;
    TextView exprience;
    TextView gender;
    TextView role;
    TextView language;
    TextView about;
    ImageView profile;
    Button doctorEditProfileButton;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    Uri imgUri;
    RequestOptions requestOptions;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        doctorName = findViewById(R.id.doctor_name);
        doctorContact = findViewById(R.id.contact_doctor_fragment_profile);
        clinicAddress = findViewById(R.id.address_doctor_fragment_profile);
        specialization = findViewById(R.id.specialization_doctor_fragment_profile);
        education = findViewById(R.id.education_doctor_profile_fragment);
        exprience = findViewById(R.id.exprience_doctor_fragment_profile);
        doctorEmail = findViewById(R.id.email_doctor_fragment_profile);
        gender = findViewById(R.id.gender_doctor_fragment_profile);
        role = findViewById(R.id.doctor_role);
        language = findViewById(R.id.language_doctor);
        about = findViewById(R.id.about_doctor_fragment_profile);
        doctorEditProfileButton = findViewById(R.id.edit_profile_fragment_profile);
        profile = findViewById(R.id.profile);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CircleCrop());
        imgUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.doctor_image);

        DocumentReference documentReference = firebaseFirestore.collection("Doctors").document(firebaseAuth.getCurrentUser().getPhoneNumber());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> data = documentSnapshot.getData();
                    gender.setText(data.get("Gender").toString());
                    List<String> specializationList = (ArrayList) data.get("Specialization");
                    specialization.setText(String.join(", ", specializationList));
                    doctorName.setText(data.get("Name").toString());
                    doctorEmail.setText(data.get("E-mail address").toString());
                    education.setText(data.get("Education").toString());
                    clinicAddress.setText(data.get("Clinic Address").toString());
                    exprience.setText(data.get("Experience").toString());
                    doctorContact.setText(firebaseAuth.getCurrentUser().getPhoneNumber().toString());
                    role.setText(data.get("Role").toString());
                    List<String> languageList = (ArrayList) data.get("Language");
                    language.setText(String.join(", ", languageList));
                    System.out.println(data.get("About").toString());
                    about.setText(data.get("About").toString());
                    if(data.containsKey("Profile URL"))
                    {
                        imgUri = Uri.parse(data.get("Profile URL").toString());
                        Glide.with(DoctorProfile.this).load(imgUri).apply(requestOptions).into(profile);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                gender.setText("NaN");
                specialization.setText("NaN");
                doctorName.setText("NaN");
                doctorEmail.setText("NaN");
                education.setText("NaN");
                clinicAddress.setText("NaN");
                exprience.setText("NaN");
                doctorContact.setText("NaN");
                role.setText("NaN");
                language.setText("NaN");
                about.setText("NaN");
                Toast.makeText(DoctorProfile.this, "Error fetching data, try again!", Toast.LENGTH_SHORT).show();
            }
        });

        doctorEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DoctorProfile.this, EditDoctorProfile.class);
                i.putExtra("Name" , doctorName.getText().toString());
                String[] specializationWords = specialization.getText().toString().split(", ");
                ArrayList<String> specializationList = new ArrayList<>(Arrays.asList(specializationWords));
                String[] langWords = language.getText().toString().split(", ");
                ArrayList<String> langList = new ArrayList<>(Arrays.asList(langWords));
                i.putStringArrayListExtra("Specialization" , specializationList);
                i.putExtra("Email", doctorEmail.getText().toString());
                i.putExtra("Gender" , gender.getText().toString());
                i.putExtra("Education" , education.getText().toString());
                i.putExtra("Experience" , exprience.getText().toString());
                i.putExtra("Clinic Address" , clinicAddress.getText().toString());
                i.putExtra("Profile Uri", imgUri.toString());
                i.putExtra("Role", role.getText().toString());
                i.putStringArrayListExtra("Language",langList);
                i.putExtra("About", about.getText().toString());
                startActivityForResult(i, 90);
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK && data!=null)
        {
            if(requestCode==90)
            {
                gender.setText(data.getStringExtra("Gender"));
                ArrayList<String> specializationList = data.getStringArrayListExtra("Specialization");
                specialization.setText(String.join(", ", specializationList));
                doctorName.setText(data.getStringExtra("Name"));
                doctorEmail.setText(data.getStringExtra("E-mail address"));
                education.setText(data.getStringExtra("Education"));
                clinicAddress.setText(data.getStringExtra("Clinic Address"));
                exprience.setText(data.getStringExtra("Experience"));
                doctorContact.setText(firebaseAuth.getCurrentUser().getPhoneNumber());
                role.setText(data.getStringExtra("Role"));
                language.setText(String.join(", ", data.getStringArrayListExtra("Language")));
                about.setText(data.getStringExtra("About"));
                imgUri = Uri.parse(data.getStringExtra("Profile URL"));
                Glide.with(this).load(imgUri).apply(requestOptions).into(profile);
            }
        }
    }

    ProgressDialog progressDialog;
    void dialogShow()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false); // Prevent user from dismissing it by clicking outside
        progressDialog.show();

    }

    void dismiss()
    {
        progressDialog.dismiss();
    }


}