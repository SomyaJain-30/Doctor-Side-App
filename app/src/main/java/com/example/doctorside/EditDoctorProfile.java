package com.example.doctorside;

import static java.security.AccessController.getContext;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditDoctorProfile extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_IMAGE = 100;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    EditText doctorName;
    EditText clinicAddress;
    EditText doctorEmail;
    EditText education;
    EditText exprience;
    CheckBox checkBox1;
    CheckBox checkBox2;
    CheckBox checkBox3;
    CheckBox checkBox4;
    CheckBox checkBox5;
    ArrayList<String> areaOfSpecialization;
    GridLayout gridLayout;
    AutoCompleteTextView role;
    AutoCompleteTextView language;
    RecyclerView langRv;
    LanguageAdapter languageAdapter;
    List<String> langs;
    EditText about;
    ImageView profile;
    CardView EditProfileImage;
    RadioGroup radioGroupGender;
    Button saveChanges;
    Uri imgUri;
    RequestOptions requestOptions;
    DocumentReference documentReference;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_doctor_profile);

        doctorName = (EditText) findViewById(R.id.doctor_editprofile_name);
        clinicAddress = (EditText) findViewById(R.id.doctor_editprofile_clinicaddress);
        doctorEmail = (EditText) findViewById(R.id.doctor_editprofile_email);
        education = (EditText) findViewById(R.id.doctor_editprofile_education);
        exprience = (EditText) findViewById(R.id.doctor_editprofile_exprience);
        radioGroupGender = (RadioGroup) findViewById(R.id.radiogroup_editprofile_doctor);
        saveChanges = (Button) findViewById(R.id.doctor_editprofile_savechanges);
        EditProfileImage = (CardView) findViewById(R.id.cardView8);
        profile = (ImageView) findViewById(R.id.profile);
        role = findViewById(R.id.doctor_editprofile_role);
        language = findViewById(R.id.doctor_editprofile_language);
        about = findViewById(R.id.doctor_editprofile_about);
        checkBox1 = findViewById(R.id.checkbox1);
        checkBox2 = findViewById(R.id.checkbox2);
        checkBox3 = findViewById(R.id.checkbox3);
        checkBox4 = findViewById(R.id.checkbox4);
        checkBox5 = findViewById(R.id.checkbox5);
        gridLayout = findViewById(R.id.grid_layout);
//        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                RadioButton selectedRadioButton = findViewById(checkedId);
//                if (selectedRadioButton != null) {
//                    gender = selectedRadioButton.getText().toString();
//                    // Use selectedText as the text of the selected radio button
//                }
//            }
//        });

        langRv = findViewById(R.id.language_rec);

        language.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Handle the "OK" button click here
                    // For example, dismiss the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(language.getWindowToken(), 0);
                    String l = language.getText().toString();
                    List<String> arr = Arrays.asList(getResources().getStringArray(R.array.Languages));
                    if(arr.contains(l))
                    {
                        langs.add(l);
                        languageAdapter.notifyDataSetChanged();
                    }
                    language.setText("");
                    return true;
                }
                return false;
            }
        });

        ArrayAdapter<String> adapter_lang = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.Languages));
        language.setAdapter(adapter_lang);

        ArrayAdapter<String> adapter_role = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.Role));
        role.setDropDownHorizontalOffset(-role.getWidth());
        role.setDropDownWidth(500);
        role.setDropDownAnchor(R.id.doctor_editprofile_role);
        role.setAdapter(adapter_role);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CircleCrop());
        imgUri = null;
        String previousName = getIntent().getStringExtra("Name");
        List<String> previousSpecialization = getIntent().getStringArrayListExtra("Specialization");
        String previousEmail = getIntent().getStringExtra("Email");
        String previousGender = getIntent().getStringExtra("Gender");
        String previousEducation = getIntent().getStringExtra("Education");
        String previousExprience = getIntent().getStringExtra("Experience");
        String previousClinicAddress = getIntent().getStringExtra("Clinic Address");
        String previousRole = getIntent().getStringExtra("Role");
        List<String> previousLanguage = getIntent().getStringArrayListExtra("Language");
        String previousAbout = getIntent().getStringExtra("About");
        if(getIntent().getStringExtra("Profile Uri")!=null)
            imgUri = Uri.parse(getIntent().getStringExtra("Profile Uri"));

        doctorName.setText(previousName);
        doctorEmail.setText(previousEmail);
        education.setText(previousEducation);
        exprience.setText(previousExprience);
        clinicAddress.setText(previousClinicAddress);
        role.setText(previousRole);
        about.setText(previousAbout);
        Glide.with(this).load(imgUri).apply(requestOptions).into(profile);

        langs = new ArrayList<>(previousLanguage);
        languageAdapter = new LanguageAdapter(this, langs);
        langRv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        langRv.setAdapter(languageAdapter);

        if (previousSpecialization != null) {
            for (int i = 0; i < gridLayout.getChildCount(); i++) {
                View view = gridLayout.getChildAt(i);
                if (view instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) view;
                    String checkBoxText = checkBox.getText().toString();

                    // Check if the CheckBox text is in the selectedCheckBoxText list
                    if (previousSpecialization.contains(checkBoxText)) {
                        checkBox.setChecked(true);
                    }
                }
            }
        }

        for (int i = 0; i < radioGroupGender.getChildCount(); i++) {
            View view = radioGroupGender.getChildAt(i);
            if (view instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) view;
                if (radioButton.getText().toString().equals(previousGender)) {
                    // Select the matching RadioButton
                    radioButton.setChecked(true);
                    break; // Exit the loop once found
                }
            }
        }

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
                boolean flg = checkBox1.isChecked() || checkBox2.isChecked() || checkBox3.isChecked() || checkBox4.isChecked() || checkBox5.isChecked();
                if(!langs.isEmpty() && flg && !doctorName.getText().toString().isEmpty() && !doctorEmail.getText().toString().isEmpty() && radioGroupGender.getCheckedRadioButtonId() != -1 && !education.getText().toString().isEmpty() && !exprience.getText().toString().isEmpty() && !clinicAddress.getText().toString().isEmpty() && !role.getText().toString().isEmpty() && !about.getText().toString().isEmpty()){
                    sendDataToFireStore();
                }else{
                    Toast.makeText(EditDoctorProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



    public void sendDataToFireStore(){

        areaOfSpecialization= new ArrayList<>();
        if(checkBox1.isChecked()){
            areaOfSpecialization.add(checkBox1.getText().toString());
        }
        if(checkBox2.isChecked()){
            areaOfSpecialization.add(checkBox2.getText().toString());
        }
        if(checkBox3.isChecked()){
            areaOfSpecialization.add(checkBox3.getText().toString());
        }
        if(checkBox4.isChecked()){
            areaOfSpecialization.add(checkBox4.getText().toString());
        }
        if(checkBox5.isChecked()){
            areaOfSpecialization.add(checkBox5.getText().toString());
        }
        dialogShow();
        documentReference = firebaseFirestore.collection("Doctors").document(firebaseAuth.getCurrentUser().getPhoneNumber());

        if(!doctorName.getText().toString().isEmpty()){
            documentReference.update("Name", doctorName.getText().toString());
        }
        if(!areaOfSpecialization.isEmpty()){
            documentReference.update("Specialization", areaOfSpecialization);
        }
        if(!doctorEmail.getText().toString().isEmpty()){
            documentReference.update("E-mail address", doctorEmail.getText().toString());
        }
        if(!education.getText().toString().isEmpty()){
            documentReference.update("Education" , education.getText().toString());
        }
        if(!exprience.getText().toString().isEmpty()){
            documentReference.update("Experience" , exprience.getText().toString());
        }

        if(radioGroupGender.getCheckedRadioButtonId() != -1){
            RadioButton selectedRadioButton = findViewById(radioGroupGender.getCheckedRadioButtonId());
            String text = selectedRadioButton.getText().toString();
            documentReference.update("Gender" , text);
        }
        if(!clinicAddress.getText().toString().isEmpty()){
            documentReference.update("Clinic Address", clinicAddress.getText().toString());
        }
        if(!role.getText().toString().isEmpty()){
            documentReference.update("Role", role.getText().toString());
        }

        documentReference.update("Language", langs);

        if(!about.getText().toString().isEmpty()){
            documentReference.update("About", about.getText().toString());
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
        i.putStringArrayListExtra("Specialization", areaOfSpecialization);
        i.putExtra("E-mail address", doctorEmail.getText().toString());
        i.putExtra("Education" , education.getText().toString());
        i.putExtra("Experience" , exprience.getText().toString());
        RadioButton selectedRadioButton = findViewById(radioGroupGender.getCheckedRadioButtonId());
        String gender = selectedRadioButton.getText().toString();
        i.putExtra("Gender" , gender);
        i.putExtra("Clinic Address", clinicAddress.getText().toString());
        i.putExtra("Profile URL", imgUri.toString());
        i.putExtra("Role", role.getText().toString());
        i.putStringArrayListExtra("Language", (ArrayList<String>) langs);
        i.putExtra("About", about.getText().toString());
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