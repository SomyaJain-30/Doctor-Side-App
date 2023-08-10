package com.example.doctorside;

import java.util.List;

public class Slots {
    private String day;
    private List<String> times;


    public Slots(String day, List<String> times) {
        this.day = day;
        this.times = times;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public List<String> getTimes() {
        return times;
    }

    public void setTimes(List<String> times) {
        this.times = times;
    }
}
