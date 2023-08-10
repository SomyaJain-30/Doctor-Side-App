package com.example.doctorside;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TimeSlotsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<String> times;
    List<String> convertedTimes;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    public TimeSlotsAdapter(Context context)
    {
        this.context = context;
        times = new ArrayList<>();
        convertedTimes = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_slot_card, parent, false);
        return new TimeSlotsAdapter.TimeSlotCardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String time = convertedTimes.get(holder.getAdapterPosition());
        ((TimeSlotCardHolder) holder).tv.setText(time);
        ((TimeSlotCardHolder) holder).close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                times.remove(holder.getAdapterPosition());
                convertedTimes.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return convertedTimes.size();
    }

    public void addTime(String timeSlot, String day) {
        times.add(timeSlot);
        notifyDataSetChanged();

        Collections.sort(times);

        // Step 2: Make times unique
        Set<String> uniqueTimesSet = new LinkedHashSet<>(times);
        times = new ArrayList<>(uniqueTimesSet);

        // Step 3: Convert to 12-hour format with AM/PM
        convertedTimes = convertTo12HourFormat(times);
    }

    public List<String> getTimes()
    {
        return times;
    }

    private static List<String> convertTo12HourFormat(List<String> times) {
        List<String> convertedTimes = new ArrayList<>();

        DateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        DateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        for (String time : times) {
            try {
                Date date = inputFormat.parse(time);
                String convertedTime = outputFormat.format(date);
                convertedTimes.add(convertedTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return convertedTimes;
    }

    public void setTimes(List<String> times) {
        this.times = times;
        Collections.sort(times);
        Set<String> uniqueTimesSet = new LinkedHashSet<>(times);
        times = new ArrayList<>(uniqueTimesSet);
        convertedTimes = convertTo12HourFormat(times);
        notifyDataSetChanged();
    }

    private class TimeSlotCardHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView close;
        public TimeSlotCardHolder(View view) {
            super(view);
            tv = view.findViewById(R.id.time);
            close = view.findViewById(R.id.close);
        }
    }
}
