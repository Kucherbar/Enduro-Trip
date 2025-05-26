package com.example.myapplication.ui.home;
import android.Manifest;
import android.content.Context;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;



import com.example.myapplication.DBTrip;
import com.example.myapplication.Trip;
import com.example.myapplication.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements SensorEventListener {
    TextView averageSpeedTV;
    TextView maxSpeedTV;
    TextView timeTV;
    TextView distanceTV;
    Button startTripBut;
    DBTrip dbSQLite;
    private FragmentHomeBinding binding;

    public final static int timeInterval = 1000;

    long idTrip;
    int tripTime;
    double tripDistance;
    int tripMaxSpeed;
    int tripAverageSpeed;
    static String tripName = "null";
    int speed;


    double currentLatitude;
    double currentLongitude;
    double lastLatitude;
    double lastLongitude;
    int timeCode;
    long startTime;
    long endTime;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        TextView textView = binding.textHome;
        startTripBut = binding.startTrip;

        Timer timer = new Timer();
        startTripBut.setOnClickListener(new View.OnClickListener() {

            int i = 1;
            @Override
            public void onClick(View v) {

                if (i % 2 != 0) {                    // Начало поездки
                    i++;
                    lastLatitude = currentLatitude;
                    lastLongitude = currentLongitude;
                    MyDialogFragment dialogFragment = new MyDialogFragment();
                    dialogFragment.show(getActivity().getSupportFragmentManager(), "Trip name dialog");// Название не передается
                    timer.start();
                    startTime = System.currentTimeMillis();
                    startTripBut.setText("Завершить поездку");
                } else {                                       // Конец поездки
                    i++;
                    timer.cancel();
                    endTime = System.currentTimeMillis();
                    startTripBut.setText("Начать поездку");
                    timer.onFinish();
                }
            }
        });
        dbSQLite = new DBTrip(getContext());
        ArrayList<Trip> arr = new ArrayList<>(dbSQLite.selectAllTrips());
        if (!arr.isEmpty()) {
            idTrip = arr.get(arr.size() - 1).getId() + 1;
        }
        else idTrip = 1;


        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
               speed = (int) location.getSpeed();
               currentLatitude = location.getLatitude();
               currentLongitude = location.getLongitude();

                textView.setText("latitude: " + currentLatitude + "\nlongitude: " + currentLongitude + "\nspeed: " + speed);
            }
        };
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(),"GPS isn't working", Toast.LENGTH_LONG).show();
            return root;

        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        Toast.makeText(getActivity(),"GPS is working", Toast.LENGTH_SHORT).show();
        return root;
    }



    class Timer extends CountDownTimer {

        public Timer() {
            super(Integer.MAX_VALUE, timeInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            if (lastLongitude == 0 && lastLatitude == 0) {
                lastLatitude = currentLatitude;
                lastLongitude = currentLongitude;
                return;
            }
            updateDistance();
            updateTime();
            updateAverageSpeed();
            updateMaxSpeed();
//            if( !((lastLatitude == currentLatitude && lastLongitude == currentLongitude) || (tripDistance / 1000 <= 1)) ) {
            dbSQLite.insert(currentLatitude,currentLongitude,speed,timeCode / 1000,idTrip);
//            }
        }

        @Override
        public void onFinish() {
            tripTime = (int) (endTime - startTime) / 1000;
            dbSQLite.insert(tripName,(int) (tripDistance),tripTime,tripAverageSpeed,tripMaxSpeed);
            idTrip = idTrip + 1;
            nullifyAll();
        }
    }

    public void nullifyAll() {
        lastLatitude = currentLatitude = 0;
        lastLongitude = currentLongitude = 0;
        tripMaxSpeed = 0;
        timeTV = binding.time;
        timeTV.setText("00:00");
        updateDistance();
        updateAverageSpeed();
        updateMaxSpeed();
    }
    public int calculateDistance() {
        return (int) (tripDistance = tripDistance + (111.2 * Math.acos(Math.sin(currentLatitude) * Math.sin(lastLatitude)
                        + Math.cos(currentLatitude) * Math.cos(lastLatitude) * Math.cos(lastLongitude - currentLongitude))));
    }
    public void updateDistance() {
        calculateDistance();
        lastLatitude = currentLatitude;
        lastLongitude = currentLongitude;
        distanceTV = binding.distance;
        distanceTV.setText((int) (tripDistance) + "");
    }
    public void updateAverageSpeed() {
        tripAverageSpeed = (int) (tripDistance / (tripTime / 1000));
        averageSpeedTV = binding.averageSpeed;
        averageSpeedTV.setText(tripAverageSpeed + "");

    }
    public void updateMaxSpeed() {
        if (tripMaxSpeed < speed) tripMaxSpeed = speed;
        maxSpeedTV = binding.MaxSpeed;
        maxSpeedTV.setText(tripMaxSpeed + "");
    }
    public void updateTime() {
        timeCode = (int)(System.currentTimeMillis() - startTime);
        timeTV = binding.time;
        String hour,minute,sec;
        if (timeCode >= 3600000 && timeCode / 3600000 < 10) hour = "0" + timeCode / 3600000 + ":";
        else if (timeCode >= 3600000 && timeCode / 3600000 >= 10) hour = timeCode / 3600000 + ":";
        else hour = "";
        if (timeCode >= 60000 && timeCode / 60000 < 10) minute = "0" + timeCode / 60000 + ":";
        else if (timeCode >= 60000 && timeCode / 60000 >= 10) minute = timeCode / 60000 + ":";
        else minute = "00:";
        if (timeCode >= 1000 && timeCode / 1000 < 10) sec = "0" + timeCode / 1000;
        else if (timeCode >= 1000 && timeCode / 1000 >= 10) sec = timeCode / 1000 + "";
        else sec = "00";
        timeTV.setText(hour + minute + sec);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public static void setTripName(String tripname) {
        HomeFragment.tripName = tripname;
    }
}