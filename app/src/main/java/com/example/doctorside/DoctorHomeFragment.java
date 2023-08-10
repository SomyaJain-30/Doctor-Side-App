package com.example.doctorside;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.TtsSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;



public class DoctorHomeFragment extends Fragment {
    Toolbar toolbar;
    ImageView menu;
    CardView requestedApplication;
    CardView upcomingApplication;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_doctor_home, container, false);
        toolbar = v.findViewById(R.id.doctor_home_toolbar);
        menu = v.findViewById(R.id.menu);
        setHasOptionsMenu(true);
        requestedApplication = v.findViewById(R.id.requested_application_cardview);
        upcomingApplication = v.findViewById(R.id.upcoming_applications_cardview);

        requestedApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(getContext(), DoctorRequestedApplication.class);
                startActivity(i);
            }
        });
        upcomingApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), DoctorUpcomingApplication.class);
                startActivity(i);
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
        return v;
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.inflate(R.menu.doctor_menu_items);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.doctor_profile){
                    Intent i = new Intent(getContext(), DoctorProfile.class);
                    startActivity(i);
                    return true;
                }else if(id == R.id.doctor_logout){
                    Toast.makeText(getContext(), "This is logout button", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else
                    return false;
            }
        });
        popupMenu.show();
    }
//
//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate( R.menu.doctor_menu_items, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if(id == R.id.doctor_profile){
//            Intent i = new Intent(getContext(), DoctorProfile.class);
//            startActivity(i);
//            return true;
//        }else if(id == R.id.doctor_logout){
//            Toast.makeText(getContext(), "This is logout button", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

}