package com.example.doctorside;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doctorside.AppointmentDetail;
import com.example.doctorside.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RequestedApplicationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int IT_IS_DATE = 0;
    private static final int IT_IS_APPOINTMENT = 1;
    ArrayList<AppointmentDetail>  appointmentDetails ;
    ArrayList<Object> date_app;
    Activity activity;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    public RequestedApplicationAdapter(Activity activity, ArrayList<AppointmentDetail> appointmentDetails){
        this.appointmentDetails = appointmentDetails;
        this.activity = activity;
        date_app = makeArrayList(appointmentDetails);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private ArrayList<Object> makeArrayList(ArrayList<AppointmentDetail> appointmentDetails) {
        ArrayList<Object> date_app = new ArrayList<>();

        Collections.sort(appointmentDetails, new Comparator<AppointmentDetail>() {
            @Override
            public int compare(AppointmentDetail o1, AppointmentDetail o2) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.US);
                try {
                    Date dateTime1 = sdf.parse(o1.getDate() + " " + o1.getTime());
                    Date dateTime2 = sdf.parse(o2.getDate() + " " + o2.getTime());
                    int dateComparison = dateTime1.compareTo(dateTime2);

                    if (dateComparison == 0) {
                        // Dates are the same, compare based on time
                        return o1.getTime().compareTo(o2.getTime());
                    } else {
                        return dateComparison;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });


        String currentDate = null;
        for(AppointmentDetail ad: appointmentDetails)
        {
            if(!ad.getDate().equals(currentDate))
            {
                date_app.add(ad.getDate() + ", " + ad.getDay());
                currentDate = ad.getDate();
            }
            date_app.add(ad);
        }
        System.out.println(date_app.size());
        return date_app;
    }

    @Override
    public int getItemViewType(int position) {
        if(date_app.get(position) instanceof String)
            return IT_IS_DATE;
        else
            return IT_IS_APPOINTMENT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==IT_IS_APPOINTMENT)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.requested_application_card, parent, false);
            return new RequestedApplicationAdapter.viewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.requested_date_day, parent, false);
            return new RequestedApplicationAdapter.viewHolder2(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof viewHolder)
        {
            Glide.with(activity).load(((AppointmentDetail)date_app.get(position)).getPatientUrl()).into(((viewHolder) holder).imageView);
            ((viewHolder) holder).patientName.setText(((AppointmentDetail)date_app.get(position)).getPatientName());
            ((viewHolder) holder).timeSlot.setText(convertTo12HourFormat(String.valueOf(((AppointmentDetail)date_app.get(position)).getTime())));
            viewHolder h = (viewHolder) holder;
            ((viewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((AppointmentDetail)date_app.get(holder.getAdapterPosition())).getStatus().equals("Requested"))
                        showAcceptRejectDialog(h);
                    else if(((AppointmentDetail)date_app.get(holder.getAdapterPosition())).getStatus().equals("Confirmed"))
                        makeVideoCallDialog(h);

                }
            });
        }
        else
        {
            ((viewHolder2) holder).tv.setText((String)date_app.get(position));
        }
    }

    public void makeVideoCallDialog(viewHolder holder)
    {
        LayoutInflater inflater = LayoutInflater.from(activity.getApplicationContext());
        View dialogView = inflater.inflate(R.layout.call_dialog, null);

        TextView name, dateDayTime;
        name = dialogView.findViewById(R.id.accept_reject_patientname);
        dateDayTime = dialogView.findViewById(R.id.accept_reject_date);
        Button call;
        call = dialogView.findViewById(R.id.join_call);

        name.setText(((AppointmentDetail)date_app.get(holder.getAdapterPosition())).getPatientName());
        String str =  ((AppointmentDetail)date_app.get(holder.getAdapterPosition())).getDate() + ", " +
                ((AppointmentDetail)date_app.get(holder.getAdapterPosition())).getDay() + ", " +
                convertTo12HourFormat(String.valueOf(((AppointmentDetail)date_app.get(holder.getAdapterPosition())).getTime()));

        dateDayTime.setText(str);


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity, R.style.RoundedCornerDialog);
        dialogBuilder.setView(dialogView);
        AlertDialog acceptRejectDialog = dialogBuilder.create();
        acceptRejectDialog.show();

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(activity, CallActivity.class);
                in.putExtra("AId", ((AppointmentDetail)date_app.get(holder.getAdapterPosition())).getAppointmentId());
                in.putExtra("DName", ((AppointmentDetail)date_app.get(holder.getAdapterPosition())).getDoctorName());
                activity.startActivity(in);
                acceptRejectDialog.dismiss();
            }
        });
    }
    private void showAcceptRejectDialog(viewHolder holder) {
        LayoutInflater inflater = LayoutInflater.from(activity.getApplicationContext());
        View dialogView = inflater.inflate(R.layout.activity_accept_reject, null);


        TextView name, dateDayTime;
        name = dialogView.findViewById(R.id.accept_reject_patientname);
        dateDayTime = dialogView.findViewById(R.id.accept_reject_date);
        ImageView iv = dialogView.findViewById(R.id.download);
        Button accept, reject;
        accept = dialogView.findViewById(R.id.accept_button);
        reject = dialogView.findViewById(R.id.reject_button);

        name.setText(((AppointmentDetail)date_app.get(holder.getAdapterPosition())).getPatientName());
        String str =  ((AppointmentDetail)date_app.get(holder.getAdapterPosition())).getDate() + ", " +
                ((AppointmentDetail)date_app.get(holder.getAdapterPosition())).getDay() + ", " +
                convertTo12HourFormat(String.valueOf(((AppointmentDetail)date_app.get(holder.getAdapterPosition())).getTime()));

        dateDayTime.setText(str);


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity, R.style.RoundedCornerDialog);
        dialogBuilder.setView(dialogView);
        AlertDialog acceptRejectDialog = dialogBuilder.create();
        acceptRejectDialog.show();

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptRejectDialog.dismiss();
                Map<String,Object> toBeupdated = new HashMap<>();
                toBeupdated.put("Status", "Confirmed");
                firebaseFirestore.collection("Appointments").document(((AppointmentDetail)date_app.get(holder.getAdapterPosition())).getAppointmentId())
                        .update(toBeupdated);
                date_app.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptRejectDialog.dismiss();
                Map<String,Object> toBeupdated = new HashMap<>();
                toBeupdated.put("Status", "Rejected");
                firebaseFirestore.collection("Appointments").document(((AppointmentDetail)date_app.get(holder.getAdapterPosition())).getAppointmentId())
                        .update(toBeupdated);
                date_app.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private static String convertTo12HourFormat(String time) {
        String convertedTime = "";

        DateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        DateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        try {
            Date date = inputFormat.parse(time);
            convertedTime = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertedTime;
    }

    @Override
    public int getItemCount() {
        return date_app.size();
    }


    private class viewHolder extends RecyclerView.ViewHolder{
        TextView patientName;
        TextView timeSlot;
        ImageView imageView;
        public viewHolder(View itemView){
            super(itemView);
            patientName = itemView.findViewById(R.id.patient_name);
            timeSlot = itemView.findViewById(R.id.date_text);
            imageView = itemView.findViewById(R.id.patient_img);
        }
    }

    private class viewHolder2 extends RecyclerView.ViewHolder {
        TextView tv;
        public viewHolder2(View view) {
            super(view);
            tv = view.findViewById(R.id.tv);
        }
    }
}
