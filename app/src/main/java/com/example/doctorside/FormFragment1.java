package com.example.doctorside;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class FormFragment1 extends Fragment {
    private static final int REQUEST_CODE = 456;
    private static final int PICK_IMAGE_REQUEST = 897;
    EditText doctorName;
    EditText doctorClinicAddress;
    EditText doctorEmail;
    ImageView doctorProfile;
    TextView addProfile;
    RadioGroup radioGroupDoctor;
    Uri selectedImageUri;
    String gender = "Male";

    public String[] permissions ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_form1, container, false);
        doctorClinicAddress = v.findViewById(R.id.doctor_clinic_address);
        doctorEmail = v.findViewById(R.id.doctor_email_address);
        doctorName = v.findViewById(R.id.doctor_name);
        addProfile = v.findViewById(R.id.add_profile_text);
        doctorProfile = v.findViewById(R.id.form_doctor_profile);
        radioGroupDoctor = v.findViewById(R.id.radiogroup_doctor);

//        if(doctorName.getText().toString().isEmpty() || doctorClinicAddress.getText().toString().isEmpty() || doctorEmail.getText().toString().isEmpty() || radioGroupDoctor.getCheckedRadioButtonId() == -1){
//            Toast.makeText(DoctorFormPage.this, "Enter all the fields", Toast.LENGTH_SHORT).show();
//        }else{
////                    senddatatofirestore();
//            Intent i = new Intent(DoctorFormPage.this,DoctorFormPage2.class);
//            i.putExtra("name"  , doctorName.getText().toString());
//            i.putExtra("address", doctorClinicAddress.getText().toString());
//            i.putExtra("email", doctorEmail.getText().toString());
//            i.putExtra("gender", ((RadioButton)findViewById(radioGroupDoctor.getCheckedRadioButtonId())).getText().toString());
//            startActivity(i);
//        }

        radioGroupDoctor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadioButton = v.findViewById(checkedId);
                if (selectedRadioButton != null) {
                    gender = selectedRadioButton.getText().toString();
                    // Use selectedText as the text of the selected radio button
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 (API level 33) and higher, require READ_MEDIA_IMAGES
            permissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            // Android versions lower than 13, require READ_EXTERNAL_STORAGE
            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        }

        addProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the permission is not granted
                //Toast.makeText(getContext(), "This is form", Toast.LENGTH_SHORT).show();
                if (checkPermissions()) {
                    // Permissions are already granted, you can proceed with your code.
                    pickImageFromGallery();
                } else {
                        // Request the necessary permissions.
                    requestPermissions();
                }

            }
        });

        return v;
    }



    private boolean checkPermissions() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), permissions, PICK_IMAGE_REQUEST);
    }


    public boolean checkValues() {
        if(doctorName.getText().toString().isEmpty() || doctorClinicAddress.getText().toString().isEmpty() || doctorEmail.getText().toString().isEmpty() || radioGroupDoctor.getCheckedRadioButtonId() == -1){
            Toast.makeText(getContext(), "Enter all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            boolean allPermissionsGranted = true;

            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                pickImageFromGallery();
            } else {
                // Handle the case where permissions were denied. You can show a message to the user or take appropriate action.
            }

        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            // Get the URI of the selected image
            selectedImageUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Glide.with(this).load(bitmap).apply((new RequestOptions()).transform(new CircleCrop())).into(doctorProfile);
            // You can use the selectedImageUri to display or process the chosen image
        }
    }
}