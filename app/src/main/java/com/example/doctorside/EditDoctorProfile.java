package com.example.doctorside;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class EditDoctorProfile extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_IMAGE = 100;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    EditText doctorName;
    EditText clinicAddress;
    EditText doctorEmail;
    EditText specialization;
    EditText education;
    EditText exprience;
    ImageView profile;
    CardView EditProfileImage;
    EditText gender;
    Button saveChanges;
    Uri imgUri;
    RequestOptions requestOptions;
    DocumentReference documentReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_doctor_profile);

        doctorName = (EditText) findViewById(R.id.doctor_editprofile_name);
        clinicAddress = (EditText) findViewById(R.id.doctor_editprofile_clinicaddress);
        doctorEmail = (EditText) findViewById(R.id.doctor_editprofile_email);
        specialization = (EditText) findViewById(R.id.doctor_editprofile_specialization);
        education = (EditText) findViewById(R.id.doctor_editprofile_education);
        exprience = (EditText) findViewById(R.id.doctor_editprofile_exprience);
        gender = (EditText) findViewById(R.id.doctor_editprofile_gender);
        saveChanges = (Button) findViewById(R.id.doctor_editprofile_savechanges);
        EditProfileImage = (CardView) findViewById(R.id.cardView8);
        profile = (ImageView) findViewById(R.id.profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CircleCrop());
        imgUri = null;
        String previousName = getIntent().getStringExtra("Name");
        String previousSpecialization = getIntent().getStringExtra("Specialization");
        String previousEmail = getIntent().getStringExtra("Email");
        String previousGender = getIntent().getStringExtra("Gender");
        String previousEducation = getIntent().getStringExtra("Education");
        String previousExprience = getIntent().getStringExtra("Exprience");
        String previousClinicAddress = getIntent().getStringExtra("Clinic Address");
        if(getIntent().getStringExtra("Profile Uri")!=null)
            imgUri = Uri.parse(getIntent().getStringExtra("Profile Uri"));

        doctorName.setText(previousName);
        specialization.setText(previousSpecialization);
        doctorEmail.setText(previousEmail);
        gender.setText(previousGender);
        education.setText(previousEducation);
        exprience.setText(previousExprience);
        clinicAddress.setText(previousClinicAddress);
        Glide.with(this).load(imgUri).apply(requestOptions).into(profile);
        EditProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!doctorName.getText().toString().isEmpty() && !specialization.getText().toString().isEmpty() && !doctorEmail.getText().toString().isEmpty() && !gender.getText().toString().isEmpty() && !education.getText().toString().isEmpty() && !exprience.getText().toString().isEmpty() && !clinicAddress.getText().toString().isEmpty()){
                    sendDataToFireStore();
                }else{
                    Toast.makeText(EditDoctorProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



    public void sendDataToFireStore(){
        dialogShow();
        documentReference = firebaseFirestore.collection("Doctors").document(firebaseAuth.getCurrentUser().getPhoneNumber());

        if(!doctorName.getText().toString().isEmpty()){
            documentReference.update("Name", doctorName.getText().toString());
        }
        if(!specialization.getText().toString().isEmpty()){
            documentReference.update("Specialization", specialization.getText().toString());
        }
        if(!doctorEmail.getText().toString().isEmpty()){
            documentReference.update("E-mail address", doctorEmail.getText().toString());
        }
        if(!education.getText().toString().isEmpty()){
            documentReference.update("Education" , education.getText().toString());
        }
        if(!exprience.getText().toString().isEmpty()){
            documentReference.update("Exprience" , exprience.getText().toString());
        }

        if(!gender.getText().toString().isEmpty()){
            documentReference.update("Gender" , gender.getText().toString());
        }
        if(!clinicAddress.getText().toString().isEmpty()){
            documentReference.update("Clinic Address", clinicAddress.getText().toString());
        }

        if(imageData !=null)
        {
            StorageReference sref = firebaseStorage.getReference().child("Doctors").child(firebaseAuth.getCurrentUser().getPhoneNumber())
                    .child("Profile");

            sref.putBytes(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    sref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imgUri = uri;
                            documentReference.update("Profile URL", uri);
                            sendBack(imgUri);
                        }
                    });
                }
            });
        }
        else
        {
            sendBack(imgUri);
        }
    }

    private void sendBack(Uri imgUri) {
        Intent i = new Intent();
        i.putExtra("Name", doctorName.getText().toString());
        i.putExtra("Specialization", specialization.getText().toString());
        i.putExtra("E-mail address", doctorEmail.getText().toString());
        i.putExtra("Education" , education.getText().toString());
        i.putExtra("Exprience" , exprience.getText().toString());
        i.putExtra("Gender" , gender.getText().toString());
        i.putExtra("Clinic Address", clinicAddress.getText().toString());
        i.putExtra("Profile URL", imgUri.toString());
        setResult(RESULT_OK, i);
        dismiss();
        finish();
        Toast.makeText(EditDoctorProfile.this, "Details Updated Successfully", Toast.LENGTH_SHORT).show();
    }

    byte[] imageData = null;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Glide.with(this).load(bitmap).apply(requestOptions).into(profile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageData = baos.toByteArray();
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