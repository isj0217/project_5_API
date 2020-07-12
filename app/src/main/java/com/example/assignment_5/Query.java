package com.example.assignment_5;

import android.widget.Toast;

import java.io.Serializable;

public class Query implements Serializable {

    private String location;

    private String date;
    private String time;
    private int nx;
    private int ny;

    public String getLocation() {return location; }

    public void setLocation(String location) {this.location = location; };

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNx() {
        return Integer.toString(nx);
    }

    public void setNx(int nx) {
        this.nx = nx;
    }

    public String getNy() {
        return Integer.toString(ny);
    }

    public void setNy(int ny) {
        this.ny = ny;
    }
}
