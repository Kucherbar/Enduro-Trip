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
import android.os.Build;
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
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentHomeBinding;

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
    int tripName;
    int speed;


    double currentLatitude;
    double currentLongitude;
    double lastLatitude;
    double lastLongitude;
    public View onCreateView(@NonNull LayoutInflater inflater,
                               ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        TextView textView = binding.textHome;
        startTripBut = binding.startTrip;
        averageSpeedTV = binding.averageSpeed;
        maxSpeedTV = binding.MaxSpeed;
        timeTV = binding.time;
        distanceTV = binding.distance;
        tripTime = 1000;

        Timer timer = new Timer();
        startTripBut.setOnClickListener(new View.OnClickListener() {

            int i = 1;
            @Override
            public void onClick(View v) {

                if (i % 2 != 0) {
                    i++;
                    startTripBut.setText("Завершить поездку");
                    timer.start();
                } else {
                    i++;
                    startTripBut.setText("Начать поездку");
                    timer.cancel();
                    timer.onFinish();
                }
            }
        });
        dbSQLite = new DBTrip(getContext());
        if (!dbSQLite.selectAllLocations().isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                idTrip = dbSQLite.selectAllLocations().getLast().getIdTrips() + 1;
            }
        }
        else idTrip = 1;


        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
               speed = (int) location.getSpeed();
               currentLatitude = location.getLatitude();//1 градус = 88км при (37 градусами)
               currentLongitude = location.getLongitude();//1градус = 111.3км

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
            dbSQLite.insert(currentLatitude,currentLongitude,speed,tripTime,idTrip);
            updateDistance();
            updateAverageSpeed();
            updateMaxSpeed();
        }

        @Override
        public void onFinish() {
            dbSQLite.insert("Trip" + idTrip,(int) (tripDistance),1,tripAverageSpeed,tripMaxSpeed);
            idTrip++;
        }
    }


    public void updateDistance() {
        tripDistance = tripDistance + (111.2 * Math.acos(Math.sin(currentLatitude) * Math.sin(lastLatitude)
                + Math.cos(currentLatitude) * Math.cos(lastLatitude) * Math.cos(lastLongitude - currentLongitude)));
        lastLatitude = currentLatitude;
        lastLongitude = currentLatitude;
        distanceTV = binding.distance;
        distanceTV.setText(tripDistance + "");

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
}