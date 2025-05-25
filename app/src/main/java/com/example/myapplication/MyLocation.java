package com.example.myapplication;

import java.io.Serializable;

public class MyLocation implements Serializable {
    private long id;
    private double latidute;
    private double longitude;
    private int speed;
    private int timeCode;
    private long idTrips;

    public MyLocation(long id, double latidute, double longitude,  int speed, int timeCode,long idTrips) {
        this.id = id;
        this.idTrips = idTrips;
        this.speed = speed;
        this.timeCode = timeCode;
        this.longitude = longitude;
        this.latidute = latidute;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setIdTrips(int idTrips) {
        this.idTrips = idTrips;
    }

    public int getTimeCode() {
        return timeCode;
    }

    public void setTimeCode(int timeCode) {
        this.timeCode = timeCode;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public double getLatidute() {
        return latidute;
    }

    public void setLatidute(double latidute) {
        this.latidute = latidute;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getIdTrips() {
        return idTrips;
    }

    public void setIdTrips(long idTrips) {
        this.idTrips = idTrips;
    }

}