package com.example.doctorside;

import android.net.Uri;

public class AppointmentDetail {
    private String patientName, doctorName, Date, Day, Time, appointmentId, Cid, Did, Status, patientUrl;
    private String proforma=null;

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getCid() {
        return Cid;
    }

    public void setCid(String cid) {
        Cid = cid;
    }

    public String getDid() {
        return Did;
    }

    public void setDid(String did) {
        Did = did;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPatientUrl() {
        return patientUrl;
    }

    public void setPatientUrl(String patientUrl) {
        this.patientUrl = patientUrl;
    }

    public String getProforma() {
        return proforma;
    }

    public void setProforma(String proforma) {
        this.proforma = proforma;
    }
}
