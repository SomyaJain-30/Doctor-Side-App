package com.example.doctorside;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DoctorsLogin extends AppCompatActivity {
    EditText doctorphonenumber;
    EditText doctorotp;
    Button getotpdoctor;
    Button verifyotpdoctor;
    ProgressBar progressBar;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    LinearLayout dotslayout;
    TextView[] dots;
    ProgressBar progressBarverify;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    String backendOtp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_login);
        doctorphonenumber = (EditText) findViewById(R.id.doctor_phone_number);
        doctorotp = (EditText) findViewById(R.id.otp_doctor);
        getotpdoctor = (Button) findViewById(R.id.getotp_button_doctor);
        verifyotpdoctor = (Button) findViewById(R.id.verify_otp_doctor);
        progressBar = (ProgressBar) findViewById(R.id.sending_doctor_otp);
        progressBarverify = (ProgressBar) findViewById(R.id.verify_progressbar);
        viewPager = findViewById(R.id.viewpager);
        dotslayout = findViewById(R.id.dots);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);
        //call adapter
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);


        getotpdoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!doctorphonenumber.getText().toString().trim().isEmpty()) {
                    if ((doctorphonenumber.getText().toString().trim()).length() == 10) {
                        progressBar.setVisibility(View.VISIBLE);
                        getotpdoctor.setVisibility(View.INVISIBLE);
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "+91" + doctorphonenumber.getText().toString(),
                                60, TimeUnit.SECONDS,
                                DoctorsLogin.this,
                                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                        progressBar.setVisibility(View.GONE);
                                        getotpdoctor.setVisibility(View.VISIBLE);
                                        getotpdoctor.setText("Resend Otp");
                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                        progressBar.setVisibility(View.GONE);
                                        getotpdoctor.setVisibility(View.VISIBLE);
                                        getotpdoctor.setText("Resend Otp");
                                        Toast.makeText(DoctorsLogin.this, "Error! please check internet connection ", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String backendotp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        checkIfItIsPatient("+91"+ doctorphonenumber.getText().toString(), backendotp);
                                        getotpdoctor.setText("Resend Otp");

                                    }
                                });
                    }
                    else{
                        Toast.makeText(DoctorsLogin.this, "Enter number correctly", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(DoctorsLogin.this, "Enter all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });


        verifyotpdoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!doctorotp.getText().toString().isEmpty()){
                    String doctor_otp  = doctorotp.getText().toString();
                    if(backendOtp!=null){
                        progressBarverify.setVisibility(View.VISIBLE);
                        verifyotpdoctor.setVisibility(View.INVISIBLE);
                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(backendOtp, doctor_otp);
                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            checkIfDoctorAlreadyExist("+91" + doctorphonenumber.getText().toString());

                                        }else{
                                            progressBarverify.setVisibility(View.INVISIBLE);
                                            verifyotpdoctor.setVisibility(View.VISIBLE);
                                            Toast.makeText(DoctorsLogin.this, "Enter Correct Otp", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else{
                        Toast.makeText(DoctorsLogin.this, "Please Check internet connection", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(DoctorsLogin.this, "Enter Otp", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void addDots(int position) {
        dots = new TextView[3];
        dotslayout.removeAllViews();
        for(int i = 0; i< dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(25);
            dots[i].setTextColor(getResources().getColor(R.color.lightblue));
            dotslayout.addView(dots[i]);
        }

        if(dots.length>0){
            dots[position].setTextColor(getResources().getColor(R.color.white));
            dots[position].setTextSize(30);
        }

    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void checkIfDoctorAlreadyExist(String s) {

        firebaseFirestore.collection("Doctors").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                boolean flg = true;
                for(DocumentSnapshot ds: queryDocumentSnapshots){
                    if(ds.getId().equals(s)){
                        flg = false;
                        Intent i = new Intent(DoctorsLogin.this, DoctorHomePage.class);
                        startActivity(i);
                        break;
                    }
                }
                if(flg == true){
                    progressBarverify.setVisibility(View.INVISIBLE);
                    verifyotpdoctor.setVisibility(View.VISIBLE);
                    Intent i = new Intent(DoctorsLogin.this, DoctorFormPage.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                }


            }
        });
    }

    private void checkIfItIsPatient(String s,String backendotp) {
        firebaseFirestore.collection("Patients").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(DocumentSnapshot ds: queryDocumentSnapshots){
                    if(ds.getId().equals(s)){

                        Toast.makeText(DoctorsLogin.this, "This number is already registered as patient", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        getotpdoctor.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                progressBar.setVisibility(View.GONE);
                getotpdoctor.setVisibility(View.VISIBLE);
                backendOtp = backendotp;





            }
        });
    }

//    public void sendDatatoFireStore(){
//        DocumentReference documentReference = firebaseFirestore.collection("Doctors").document(firebaseAuth.getCurrentUser().getPhoneNumber());
//        Map<String, Object> doctordata = new HashMap<>();
//
//        doctordata.put("Name", "dr name");
//        doctordata.put("Specialization", "specialization");
//        doctordata.put("Clinic Address", "clinic address");
//        doctordata.put("E-mail address" , "email");
//        doctordata.put("Education" , "Education");
//        doctordata.put("Exprience", "Exprience");
//        doctordata.put("Gender" , "gender");
//        documentReference.set(doctordata).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                Toast.makeText(DoctorsLogin.this, "Login Successfull!", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
//        {
//            checkIfDoctorAlreadyExist(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
//        }
//
//    }


}