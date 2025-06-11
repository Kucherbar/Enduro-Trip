package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Direction;

import androidx.appcompat.app.AppCompatDelegate;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    public static int butChecker = 1;
    public static int onFirstLocation = 0;
    @SuppressLint("ResourceAsColor")
    public static int polylineColor = (R.color.green);
    DBTrip dbSQLite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey(getString(R.string.map_kit_key));
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        dbSQLite = new DBTrip(this);
        insertExampleTrip();

        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController((BottomNavigationView) findViewById(R.id.nav_view), navController);
    }

    void insertExampleTrip() {
        if (dbSQLite.selectAllTrips().size()==0) {
            long trip = dbSQLite.insertTrip("Демо поездка",1,5 ,4,7,"Wed Jun 11");
            dbSQLite.insertLocation(55.494956, 47.484163, 0,1, trip);
            dbSQLite.insertLocation(55.493707, 47.484290, 3, 60, trip);
            dbSQLite.insertLocation(55.493525, 47.479815, 5, 120, trip);
            dbSQLite.insertLocation(55.493832, 47.479299, 7, 180, trip);
            dbSQLite.insertLocation(55.493775, 47.477188, 1, 240, trip);
        }
    }

}