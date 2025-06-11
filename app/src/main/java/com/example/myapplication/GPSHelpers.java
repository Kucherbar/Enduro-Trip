package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

public class GPSHelpers {

    public static boolean checkGPSPerms(FragmentActivity activity) {
        String afl = Manifest.permission.ACCESS_FINE_LOCATION;
        String acl = Manifest.permission.ACCESS_COARSE_LOCATION;
        int grantOk = PackageManager.PERMISSION_GRANTED;
        if (ActivityCompat.checkSelfPermission(activity, afl) != grantOk
                && ActivityCompat.checkSelfPermission(activity, acl) != grantOk) {
            ActivityCompat.requestPermissions(activity, new String[]{afl, acl}, 1);
            return false;
        }
        return true;
    }
    @SuppressLint("MissingPermission")
    public static void chekGPS(FragmentActivity context, LocationManager locationManager, LocationListener locationListener) {
        if (!checkGPSPerms(context)) {
            Toast.makeText(context, "Необходимо предоставить доступ к GPS", Toast.LENGTH_LONG).show();
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (!locationManager.isLocationEnabled()) {
                    Toast.makeText(context, "Необходимо включить GPS", Toast.LENGTH_SHORT).show();
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
