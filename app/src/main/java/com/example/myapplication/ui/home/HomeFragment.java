package com.example.myapplication.ui.home;
import android.Manifest;
import android.content.Context;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Criteria;
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
import com.example.myapplication.MainActivity;
import com.example.myapplication.Trip;
import com.example.myapplication.databinding.FragmentHomeBinding;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.CacheRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class HomeFragment extends Fragment implements SensorEventListener {
    public static final double EARTH_RADIUS =  6371;
    public static double MinDistanceDif = 0.02;
    public static final double MINDISTANCEDIF_VALUE_LOW = 0.01;
    public static final double MINDISTANCEDIF_VALUE_HIGH = 0.2;
    TextView averageSpeedTV;
    TextView maxSpeedTV;
    TextView timeTV;
    TextView distanceTV;
    Button startTripBut;
    DBTrip dbSQLite;
    private FragmentHomeBinding binding;
    LocationManager locationManager;
    LocationListener locationListener;

    public final static int timeInterval = 1000;

    long idTrip;
    int tripTime;
    double tripDistance;
    int tripMaxSpeed = 0;
    int tripAverageSpeed;
    String tripDate;
    static String tripName = "Нет названия";
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
    Timer timer;
    CountDownTimer calculateTimer;

    int i;
    int j;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        TextView textView = binding.textHome;
        startTripBut = binding.startTrip;
        timer = new Timer();
        i = 0;
        startTripBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.butChecker % 2 != 0) {                    // Начало поездки
                    MainActivity.butChecker++;
                    lastLatitude = currentLatitude;
                    lastLongitude = currentLongitude;
                    MyDialogFragment dialogFragment = new MyDialogFragment();
                    dialogFragment.show(getActivity().getSupportFragmentManager(), "Trip name dialog");
                    calculateTimer = new CountDownTimer(Integer.MAX_VALUE,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            i++;
                            if (i == 30) HomeFragment.MinDistanceDif = HomeFragment.MINDISTANCEDIF_VALUE_LOW;

                            calculateTime();
                            calculateDistance();
                            calculateAverageSpeed();
                        }

                        @Override
                        public void onFinish() {

                        }
                    }.start();
                    timer.start();
                    startTime = System.currentTimeMillis();
                    startTripBut.setText("Завершить поездку");
                } else {                                       // Конец поездки
                    MainActivity.butChecker++;
                    timer.cancel();
                    endTime = System.currentTimeMillis();
                    startTripBut.setText("Начать поездку");
                    timer.onFinish();
                    calculateTimer.cancel();
                }
            }
        });
        dbSQLite = new DBTrip(getContext());
//        dbSQLite.deleteTableLocation();
//        dbSQLite.deleteTableTrips();
        ArrayList<Trip> arr = new ArrayList<>(dbSQLite.selectAllTrips());
        if (!arr.isEmpty()) {
            idTrip = arr.get(arr.size() - 1).getId() + 1;
        }
        else{
            idTrip = 1;
        }


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
//                speed = location.getSpeed();
                MainActivity.onFirstLocation += 1;
                if (MainActivity.onFirstLocation == 1) {
                    HomeFragment.MinDistanceDif = HomeFragment.MINDISTANCEDIF_VALUE_HIGH;
                }
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                curTimeStamp = System.currentTimeMillis()/1000;
//                double s = speed;
//                s = s - s % 1;
                double distance1 = calculateDistance(lastLt, lastLng,currentLatitude,currentLongitude) * 1000;
                long time = curTimeStamp - lastTimeStamp;
                Double j  = distance1/ time ;//м.с
                j = j * 3.6;
                if (time < 1) return;
                speed =  ((int) (j*10))/10.0;

                lastLt = currentLatitude;
                lastLng = currentLongitude;
                lastTimeStamp = curTimeStamp;//"latitude: " + currentLatitude + "\nlongitude: " + currentLongitude +
                if (i <= 15 && speed > 5) {
                    textView.setText("\nspeed: " + 0.0);
                    return;

                }
                textView.setText("\nspeed: " + speed);
                calculateMaxSpeed();
            }
        };
        return root;
    }

    @Override
    public void onResume() {

        super.onResume();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(),"GPS isn't working", Toast.LENGTH_LONG).show();
            return;

        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (!locationManager.isLocationEnabled()) {
                    Toast.makeText(getContext(),"location isn't turned up", Toast.LENGTH_SHORT).show();
                }
                else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (MainActivity.butChecker % 2 == 0) {
            binding.startTrip.setText("Завершить поездку");
            timer.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    class Timer extends CountDownTimer {

        public Timer() {
            super(Integer.MAX_VALUE, timeInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            if (lastLongitude == 0 && lastLatitude == 0) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                lastLatitude = currentLatitude = location.getLatitude();
                lastLongitude = currentLongitude = location.getLongitude();
                return;
            }
            updateTime();
            updateDistance();
            updateAverageSpeed();
            updateMaxSpeed();
            if( ((lastLatitude != currentLatitude && lastLongitude != currentLongitude) && (distanceDif > HomeFragment.MinDistanceDif)) ) {
            dbSQLite.insert(currentLatitude,currentLongitude,(int)speed,tripTime / 1000,idTrip);
            startTimeAvSp = System.currentTimeMillis();
            j++;
            } else if(distanceDif != 0 && j == 0) {
                dbSQLite.insert(currentLatitude,currentLongitude,(int)speed,tripTime / 1000,idTrip);
                j++;
            }
        }

        @Override
        public void onFinish() {
            tripTime = (int) (endTime - startTime) / 1000;
            tripDate = new java.util.Date().toString();
            Scanner sc = new Scanner(tripDate);
            tripDate = sc.next() + " " + sc.next() + " " + sc.next();
            dbSQLite.insert(tripName,(int) (tripDistance),tripTime,tripAverageSpeed,tripMaxSpeed,tripDate);
            idTrip = idTrip + 1;
            nullifyAll();
        }
    }

    public void nullifyAll() {
        lastLatitude = currentLatitude = 0;
        lastLongitude = currentLongitude = 0;
        timeTV = binding.time;
        timeTV.setText("00:00");
        HomeFragment.setTripName("Нет названия");
        tripDistance = 0;
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
            if (distanceDif > HomeFragment.MinDistanceDif) {
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
        return tripAverageSpeed = (int) ((tripDistance / (double) endTimeAvSp - startTimeAvSp ) * 3600000);
    }
    public void calculateMaxSpeed() {
        if ( tripMaxSpeed < speed) tripMaxSpeed = (int) speed;
    }
    public int calculateTime() {
        return tripTime = (int)(System.currentTimeMillis() - startTime);
    }
    public void updateDistance() {
        if (distanceDif > HomeFragment.MinDistanceDif) return;
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
        maxSpeedTV = binding.MaxSpeed;
        maxSpeedTV.setText(tripMaxSpeed + "");
    }
    public void updateTime() {
        timeTV = binding.time;
        String hour,minute,sec;
        if (tripTime >= 3600000 && tripTime / 3600000 < 10) hour = "0" + (tripTime / 3600000 % 24)+ ":";
        else if (tripTime >= 3600000 && tripTime / 3600000 >= 10) hour = (tripTime / 3600000 % 24) + ":";
        else hour = "";
        if (tripTime >= 60000 && tripTime / 60000 < 10) minute = "0" + (tripTime / 60000 % 60) + ":";
        else if (tripTime >= 60000 && tripTime / 60000 >= 10) minute = (tripTime / 60000 % 60) + ":";
        else minute = "00:";
        if (tripTime >= 1000 && (tripTime / 1000 % 60) < 10) sec = "0" + (tripTime / 1000 % 60);
        else if (tripTime >= 1000 && tripTime / 1000 >= 10) sec = (tripTime / 1000 % 60) + "";
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

    public static void main(String[] args) {
    }

}