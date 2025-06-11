package com.example.myapplication.ui.speedometer;

import android.annotation.SuppressLint;
import android.content.Context;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


import com.example.myapplication.GPSHelpers;
import com.example.myapplication.DBTrip;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Trip;
import com.example.myapplication.databinding.FragmentSpeedometerBinding;

import java.util.Scanner;

public class SpeedometerFragment extends Fragment implements SensorEventListener {
    public static final double EARTH_RADIUS = 6371;
    public static double MinDistanceDif = 0.02;
    public static final double MINDISTANCEDIF_VALUE_LOW = 0.01;
    public static final double MINDISTANCEDIF_VALUE_HIGH = 0.2;
    TextView averageSpeedTV;
    TextView maxSpeedTV;
    TextView timeTV;
    TextView distanceTV;
    Button startTripBut;
    TextView speedTV;
    DBTrip dbSQLite;
    private @NonNull FragmentSpeedometerBinding binding;
    LocationManager locationManager;
    LocationListener locationListener;

    public final static int timeInterval = 1000;

    long idTrip;
    int tripTime = 0;
    double tripDistance = 0;
    int tripMaxSpeed = 0;
    int tripAverageSpeed = 0;
    String tripDate = "";
    static String tripName;
    double speed;


    double currentLatitude;
    double currentLongitude;
    double lastLatitude;
    double lastLongitude;
    double lastLt;
    double lastLng;
    long startTime;
    long startTimeAvSp;
    long endTime;
    long endTimeAvSp;
    long lastTimeStamp;
    long curTimeStamp;
    double distanceDif;
    CountDownTimer tripTimer;
    CountDownTimer calculateTimer;

    int i;
    int j;
    int badCoordSec;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSpeedometerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        speedTV = binding.speed;
        tripName = getString(R.string.trip);

        startTripBut = binding.startTrip;
        tripTimer = new Timer();
        i = 0;
        startTripBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.butChecker % 2 != 0) {                    // Начало поездки
                    MainActivity.butChecker++;
                    lastLatitude = currentLatitude;
                    lastLongitude = currentLongitude;
                    MyDialogFragment dialogFragment = new MyDialogFragment();
                    dialogFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.new_trip));
                    calculateTimer = new CountDownTimer(Integer.MAX_VALUE, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            i++;
                            //калибровка приходящих локаций
                            if (i == 30) SpeedometerFragment.MinDistanceDif = SpeedometerFragment.MINDISTANCEDIF_VALUE_LOW;
                            calculateTime();
                            if (badCoordSec > 0) {
                                badCoordSec--;
                                return;
                            }
                            calculateDistance();
                            calculateAverageSpeed();
                        }
                        @Override
                        public void onFinish() {}
                    }.start();
                    tripTimer.start();
                    startTimeAvSp = System.currentTimeMillis();
                    startTime = System.currentTimeMillis();
                    startTripBut.setText(R.string.stop_trip);
                    idTrip = dbSQLite.insertTrip(tripName, (int) (tripDistance), tripTime, tripAverageSpeed, tripMaxSpeed, tripDate);

                } else {                                       // Конец поездки
                    MainActivity.butChecker++;
                    tripTimer.cancel();
                    endTime = System.currentTimeMillis();
                    startTripBut.setText(getString(R.string.start_trip));
                    tripTimer.onFinish();
                    calculateTimer.cancel();
                }
            }
        });
        dbSQLite = new DBTrip(getContext());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
//                speed = location.getSpeed();
                MainActivity.onFirstLocation += 1;
                if (MainActivity.onFirstLocation == 1) {
                    SpeedometerFragment.MinDistanceDif = SpeedometerFragment.MINDISTANCEDIF_VALUE_HIGH;
                }
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                curTimeStamp = System.currentTimeMillis()/1000;
                long time = curTimeStamp - lastTimeStamp;
                if (time > 2500) {
                    badCoordSec = 4;
                }
//                double s = speed;
//                s = s - s % 1;
                double distance1 = calculateDistance(lastLt, lastLng, currentLatitude, currentLongitude) * 1000;
                Double j = distance1 / time;//м.с
                j = j * 3.6;
                if (time < 1) return;
                speed = ((int) (j * 10)) / 10.0;

                lastLt = currentLatitude;
                lastLng = currentLongitude;
                lastTimeStamp = curTimeStamp;//"latitude: " + currentLatitude + "\nlongitude: " + currentLongitude +
                if (badCoordSec > 0) return;

                if (i <= 15 && speed > 5) {
                    speedTV.setText(0 + "");
                    return;

                } else speedTV.setText((int) speed + "");
                calculateMaxSpeed();
            }
        };
        binding.secretBut.setOnClickListener(v -> {
            Toast.makeText(getContext(), R.string.secret_tap, Toast.LENGTH_SHORT).show();
            SecretDialogFragment dialogFragment = new SecretDialogFragment();
            dialogFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.secret));
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        FragmentActivity context = getActivity();
        GPSHelpers.chekGPS(context, locationManager, locationListener);
    }



    @Override
    public void onStart() {
        super.onStart();
        if (MainActivity.butChecker % 2 == 0) {
            binding.startTrip.setText(getString(R.string.stop_trip));
            tripTimer.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        tripTimer.cancel();
    }

    class Timer extends CountDownTimer {

        public Timer() {
            super(Integer.MAX_VALUE, timeInterval);
        }

        @Override
        @SuppressLint("MissingPermission")
        public void onTick(long millisUntilFinished) {
            if (lastLongitude == 0 && lastLatitude == 0) {
                if (!GPSHelpers.checkGPSPerms(getActivity())) return;
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                lastLatitude = currentLatitude = location.getLatitude();
                lastLongitude = currentLongitude = location.getLongitude();
            }
            updateTime();
            updateDistance();
            updateAverageSpeed();
            updateMaxSpeed();
            if (((lastLatitude != currentLatitude && lastLongitude != currentLongitude) && (distanceDif > SpeedometerFragment.MinDistanceDif))) {
                dbSQLite.insertLocation(currentLatitude, currentLongitude, (int) speed, tripTime / 1000, idTrip);
                j++;
            } else if (distanceDif != 0 && j == 0) {
                dbSQLite.insertLocation(currentLatitude, currentLongitude, (int) speed, tripTime / 1000, idTrip);
                j++;
            }
        }

        @Override
        public void onFinish() {
            tripTime = (int) (endTime - startTime) / 1000;
            tripDate = new java.util.Date().toString();
            Scanner sc = new Scanner(tripDate);
            tripDate = sc.next() + " " + sc.next() + " " + sc.next();
            Trip trip = new Trip(idTrip, tripName, (int) (tripDistance), tripTime, tripAverageSpeed, tripMaxSpeed, tripDate);
            dbSQLite.updateTrip(trip);
            nullifyAll();
        }
    }

    public void nullifyAll() {
        lastLatitude = currentLatitude = 0;
        lastLongitude = currentLongitude = 0;
        timeTV = binding.time;
        SpeedometerFragment.setTripName(tripName);
        tripDistance = 0.0;
        tripAverageSpeed = 0;
        tripMaxSpeed = 0;
        distanceTV = binding.distance;
        distanceTV.setText((tripDistance) + "");
        updateAverageSpeed();
        updateMaxSpeed();
    }

    public double calculateDistance() {


        double lat1Rad = Math.toRadians(lastLatitude);
        double lat2Rad = Math.toRadians(currentLatitude);
        double lon1Rad = Math.toRadians(lastLongitude);
        double lon2Rad = Math.toRadians(currentLongitude);

        double x = (lon2Rad - lon1Rad) * Math.cos((lat1Rad + lat2Rad) / 2);
        double y = (lat2Rad - lat1Rad);
        if (!(currentLatitude == 0 || currentLongitude == 0 || lastLongitude == 0 || lastLatitude == 0)) {
            distanceDif = Math.sqrt(x * x + y * y) * EARTH_RADIUS;
            if (distanceDif > SpeedometerFragment.MinDistanceDif) {
                tripDistance = tripDistance + distanceDif;
                lastLatitude = currentLatitude;
                lastLongitude = currentLongitude;
                endTimeAvSp = System.currentTimeMillis();
            }
        }
        return tripDistance;
    }

    public double calculateDistance(double lastLatitude, double lastLongitude, double currentLatitude, double currentLongitude) {
//        if (!(currentLatitude == 0 || currentLongitude == 0 || lastLongitude == 0 || lastLatitude == 0))
//
//            tripDistance = tripDistance + (111.2 * Math.acos(Math.sin(currentLatitude) * Math.sin(lastLatitude)
        double lat1Rad = Math.toRadians(lastLatitude);
        double lat2Rad = Math.toRadians(currentLatitude);
        double lon1Rad = Math.toRadians(lastLongitude);
        double lon2Rad = Math.toRadians(currentLongitude);
        double distance = 0;
        double x = (lon2Rad - lon1Rad) * Math.cos((lat1Rad + lat2Rad) / 2);
        double y = (lat2Rad - lat1Rad);
        if (!(currentLatitude == 0 || currentLongitude == 0 || lastLongitude == 0 || lastLatitude == 0)) {
            distance = Math.sqrt(x * x + y * y) * EARTH_RADIUS;
        }
        return distance;
    }

    public int calculateAverageSpeed() {
        return tripAverageSpeed = (int) ((tripDistance / (double) tripTime) * 3600000);
    }

    public void calculateMaxSpeed() {
        if (tripMaxSpeed < speed) tripMaxSpeed = (int) speed;
    }

    public int calculateTime() {
        return tripTime = (int) (System.currentTimeMillis() - startTime);
    }

    public void updateDistance() {
        if (distanceDif > SpeedometerFragment.MinDistanceDif) return;
        distanceTV = binding.distance;
        double distanceText = tripDistance;
        distanceText = ((distanceText * 1000 - distanceText * 1000 % 1) / 1000);
        distanceTV.setText(distanceText + "");
    }

    public void updateAverageSpeed() {
        averageSpeedTV = binding.averageSpeed;
        averageSpeedTV.setText(tripAverageSpeed + "");

    }

    public void updateMaxSpeed() {
        maxSpeedTV = binding.maxSpeed;
        maxSpeedTV.setText(tripMaxSpeed + "");
    }

    public void updateTime() {
        timeTV = binding.time;
        String hour, minute, sec;
        if (tripTime >= 3600000 && tripTime / 3600000 < 10) hour = (tripTime / 3600000 % 24) + "ч ";
        else if (tripTime >= 3600000 && tripTime / 3600000 >= 10)
            hour = (tripTime / 3600000 % 24) + "ч ";
        else hour = "";
        if (tripTime >= 60000 && tripTime / 60000 < 10) minute = (tripTime / 60000 % 60) + "м ";
        else if (tripTime >= 60000 && tripTime / 60000 >= 10)
            minute = (tripTime / 60000 % 60) + "м ";
        else minute = "0м ";
        if (tripTime >= 1000 && (tripTime / 1000 % 60) < 10) sec = (tripTime / 1000 % 60) + "с";
        else if (tripTime >= 1000 && tripTime / 1000 >= 10) sec = (tripTime / 1000 % 60) + "с";
        else sec = "0";
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
        SpeedometerFragment.tripName = tripname;
    }


}