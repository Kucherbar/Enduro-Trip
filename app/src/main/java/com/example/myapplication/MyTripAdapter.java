package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.ui.dashboard.DashboardFragment;

import java.util.ArrayList;

public class MyTripAdapter extends ArrayAdapter<Trip> {
    public MyTripAdapter(@NonNull Context context, ArrayList<Trip> resource) {
        super(context,R.layout.trip_item, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trip_item, null);
        }
        Trip trip = getItem(position);
        if (trip.getName() == "0") {
            return null;
        }
        TextView titleTV = convertView.findViewById(R.id.titleName);
        titleTV.setText(trip.getName());
        TextView distanceTV = convertView.findViewById(R.id.distance1);
        distanceTV.setText(trip.getDistance() + "");
        TextView timeTV = convertView.findViewById(R.id.time1);
        timeTV.setText(trip.getTime() / 60 + " минут");
        TextView averageSpeedTV = convertView.findViewById(R.id.averageSpeed1);
        averageSpeedTV.setText(trip.getAverageSpeed() + "");
        TextView maxSpeedTV = convertView.findViewById(R.id.maxSpeed1);
        maxSpeedTV.setText(trip.getMaxSpeed() + "");
        TextView dateTV= convertView.findViewById(R.id.date);
        dateTV.setText(trip.getDate());
        TextView tripID = convertView.findViewById(R.id.tripID);
        tripID.setText(trip.getId() + "");

        return convertView;
    }
}
