package com.example.doctorside;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FormFragment2 extends Fragment {
    AutoCompleteTextView doctorSpecialization;
    AutoCompleteTextView doctorRole;
    EditText doctorEducation;
    AutoCompleteTextView doctorLanguage;
    EditText doctorExperience;
    MultiAutoCompleteTextView doctorAbout;
    GridLayout gridLayout;
    CheckBox checkBox1;
    CheckBox checkBox2;
    CheckBox checkBox3;
    CheckBox checkBox4;
    CheckBox checkBox5;
    RecyclerView langRv;
    LanguageAdapter languageAdapter;
    List<String> langs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_form2, container, false);

        doctorSpecialization = v.findViewById(R.id.doctor_specialization);
        doctorEducation = v.findViewById(R.id.doctor_education);
        doctorExperience = v.findViewById(R.id.doctor_experience);
        doctorLanguage = v.findViewById(R.id.doctor_language);
        doctorRole = v.findViewById(R.id.doctor_role);
        doctorAbout = v.findViewById(R.id.doctor_about);
        gridLayout = v.findViewById(R.id.grid_layout);
        checkBox1 = v.findViewById(R.id.checkbox1);
        checkBox2 = v.findViewById(R.id.checkbox2);
        checkBox3 = v.findViewById(R.id.checkbox3);
        checkBox4 = v.findViewById(R.id.checkbox4);
        checkBox5 = v.findViewById(R.id.checkbox5);
        langRv = v.findViewById(R.id.language_rec);
        langs = new ArrayList<>();
        languageAdapter = new LanguageAdapter(getContext(), langs);
        langRv.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        langRv.setAdapter(languageAdapter);


        ArrayAdapter<String> adapter_role = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.Role));
        doctorRole.setAdapter(adapter_role);

        ArrayAdapter<String> adapter_lang = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.Languages));
        doctorLanguage.setAdapter(adapter_lang);

        doctorLanguage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Handle the "OK" button click here
                    // For example, dismiss the keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(doctorLanguage.getWindowToken(), 0);
                    String l = doctorLanguage.getText().toString();
                    List<String> arr = Arrays.asList(getResources().getStringArray(R.array.Languages));
                    if(arr.contains(l))
                    {
                        langs.add(l);
                        languageAdapter.notifyDataSetChanged();
                    }
                    doctorLanguage.setText("");
                    return true;
                }
                return false;
            }
        });

        return v;
    }

    public boolean checkValues() {
        if(doctorRole.getText().toString().isEmpty() || doctorSpecialization.getText().toString().isEmpty() || doctorExperience.getText().toString().isEmpty() || doctorEducation.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Enter all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}