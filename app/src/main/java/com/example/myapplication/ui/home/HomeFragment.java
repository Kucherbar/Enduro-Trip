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

import java.net.CacheRequest;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements SensorEventListener {
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
    int tripMaxSpeed;
    int tripAverageSpeed;
    String tripDate;
    static String tripName = "null";
    double speed;


    double currentLatitude;
    double currentLongitude;
    double lastLatitude;
    double lastLongitude;
    long startTime;
    long endTime;
    double lastLt;
    double lastLng;
    long lastTimeStamp;
    long curTimeStamp;
    Timer timer;
    CountDownTimer calculateTimer;
    


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        TextView textView = binding.textHome;
        startTripBut = binding.startTrip;
        timer = new Timer();
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
                            calculateTime();
                            calculateDistance();
                            calculateAverageSpeed();
                            calculateMaxSpeed();
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
        ArrayList<Trip> arr = new ArrayList<>(dbSQLite.selectAllTrips());
        if (!arr.isEmpty()) {
            idTrip = arr.get(arr.size() - 1).getId() + 1;
        }
        else idTrip = 1;


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        criteria.setSpeedAccuracy(Criteria.ACCURACY_LOW);
//        criteria.setPowerRequirement(Criteria.POWER_HIGH);
//        criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
//        locationManager.getBestProvider(criteria,true);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                speed = location.getSpeed();
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                curTimeStamp = System.currentTimeMillis();
//                double i  =  ((111.2 * Math.acos(Math.sin(currentLatitude) * Math.sin(lastLt)
//                        + Math.cos(currentLatitude) * Math.cos(lastLt) * Math.cos(lastLng - currentLongitude))) / ((curTimeStamp - lastTimeStamp) / 60000));
//                speed = (i * 10 - i * 10 % 1) / 10;

                lastLt = currentLatitude;
                lastLng = currentLongitude;
                lastTimeStamp = curTimeStamp;
                textView.setText("latitude: " + currentLatitude + "\nlongitude: " + currentLongitude + "\nspeed: " + speed);
            }
        };
        return root;
    }

    @Override
    public void onResume() {

        super.onResume();
        timer.start();

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
                    Toast.makeText(getContext(),"GPS is working", Toast.LENGTH_SHORT).show();
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
                lastLatitude = currentLatitude;
                lastLongitude = currentLongitude;
                return;
            }
            updateTime();
            updateDistance();
            updateAverageSpeed();
            updateMaxSpeed();
            if( !((lastLatitude == currentLatitude && lastLongitude == currentLongitude) || (tripDistance / 1000 <= 1)) ) {
            dbSQLite.insert(currentLatitude,currentLongitude,(int)speed,tripTime / 1000,idTrip);
            }
        }

        @Override
        public void onFinish() {
            tripTime = (int) (endTime - startTime) / 1000;
            tripDate = new java.util.Date().toString();
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
        distanceTV = binding.distance;
        distanceTV.setText((tripDistance) + "");
        calculateAverageSpeed();
        updateAverageSpeed();
        calculateMaxSpeed();
        updateMaxSpeed();
    }
    public int calculateDistance() {
        return (int) (tripDistance = tripDistance + (111.2 * Math.acos(Math.sin(currentLatitude) * Math.sin(lastLatitude)
                        + Math.cos(currentLatitude) * Math.cos(lastLatitude) * Math.cos(lastLongitude - currentLongitude))));
    }
    public int calculateAverageSpeed() {
        return tripAverageSpeed = (int) (tripDistance / ((double) tripTime / 3600000));
    }
    public void calculateMaxSpeed() {
        if (tripMaxSpeed < speed) tripMaxSpeed = (int)speed;
    }
    public int calculateTime() {
        return tripTime = (int)(System.currentTimeMillis() - startTime);
    }
    public void updateDistance() {
        lastLatitude = currentLatitude;
        lastLongitude = currentLongitude;
        distanceTV = binding.distance;
        distanceTV.setText((tripDistance = (tripDistance * 1000 - tripDistance * 1000 % 1) / 1000) + "");
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