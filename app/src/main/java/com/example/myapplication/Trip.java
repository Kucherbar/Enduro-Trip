package com.example.myapplication;

import java.io.Serializable;

public class Trip implements Serializable {
    private long id;
    private String name;
    private int distance;
    private int time;
    private int maxSpeed;
    private int averageSpeed;
    private String date;

    public Trip(long id, String name, int distance, int time, int averageSpeed, int maxSpeed, String date) {
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.time = time;
        this.maxSpeed = maxSpeed;
        this.averageSpeed = averageSpeed;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(int averageSpeed) {
        this.averageSpeed = averageSpeed;
    }
}
