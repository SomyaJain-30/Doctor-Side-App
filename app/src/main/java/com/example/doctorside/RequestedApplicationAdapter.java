package com.example.doctorside;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doctorside.AppointmentDetail;
import com.example.doctorside.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class RequestedApplicationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int IT_IS_DATE = 0;
    private static final int IT_IS_APPOINTMENT = 1;
    Context context;
    ArrayList<AppointmentDetail>  appointmentDetails ;
    ArrayList<Object> date_app;
    public RequestedApplicationAdapter(Context context, ArrayList<AppointmentDetail> appointmentDetails){
        this.appointmentDetails = appointmentDetails;
        this.context = context;
        date_app = makeArrayList(appointmentDetails);

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
            ((viewHolder) holder).patientName.setText(((AppointmentDetail)date_app.get(position)).getPatientName());
            ((viewHolder) holder).timeSlot.setText(convertTo12HourFormat(String.valueOf(((AppointmentDetail)date_app.get(position)).getTime())));
            ((viewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        else
        {
            ((viewHolder2) holder).tv.setText((String)date_app.get(position));
        }
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
        public viewHolder(View itemView){
            super(itemView);
            patientName = itemView.findViewById(R.id.patient_name);
            timeSlot = itemView.findViewById(R.id.date_text);
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
