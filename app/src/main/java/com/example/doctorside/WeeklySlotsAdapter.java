package com.example.doctorside;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class WeeklySlotsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<Slots> slots;
    boolean locked;
    public WeeklySlotsAdapter(Context context, ArrayList<Slots> slots, boolean locked)
    {
        this.context = context;
        this.slots = slots;
        this.locked = locked;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weekly_slot_chooser, parent, false);
        return new WeeklySlotsAdapter.WeekSlotCardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Slots slot = slots.get(position);
        WeekSlotCardHolder h = (WeekSlotCardHolder)holder;
        h.bind(slot);
        h.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(h);
            }
        });
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }

    private AlertDialog timePickerDialog;
    private void showTimePickerDialog(WeekSlotCardHolder h) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View dialogView = inflater.inflate(R.layout.dialog_time_picker, null);

        final TimePicker dialogTimePicker = dialogView.findViewById(R.id.dialogTimePicker);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int hour = dialogTimePicker.getHour();
                        int minute = dialogTimePicker.getMinute();
                        String timeSlot = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                        h.adp.addTime(timeSlot,h.day.getText().toString());
                        slots.set(h.getAdapterPosition(),new Slots(h.day.getText().toString(), h.adp.times));
                        timePickerDialog.dismiss();
                    }
                });

        timePickerDialog = dialogBuilder.create();
        timePickerDialog.show();
    }

    private class WeekSlotCardHolder extends RecyclerView.ViewHolder {
        TextView day;
        ImageView add;
        RecyclerView time_slots;
        TimeSlotsAdapter adp;
        public WeekSlotCardHolder(View v) {
            super(v);
            day = v.findViewById(R.id.week_day);
            add = v.findViewById(R.id.addSlot);
            if(locked)
                add.setVisibility(View.INVISIBLE);
            time_slots = v.findViewById(R.id.rv_slots);
            time_slots.setLayoutManager(new GridLayoutManager(context, 3));
            adp = new TimeSlotsAdapter(context,locked);
            time_slots.setAdapter(adp);
        }

        public void bind(Slots slot)
        {
            day.setText(slot.getDay());
            adp.setTimes(slot.getTimes());
        }
    }
}
