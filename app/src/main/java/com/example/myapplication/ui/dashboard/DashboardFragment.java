package com.example.myapplication.ui.dashboard;

import android.os.Bundle;
import android.os.Parcelable;
import android.se.omapi.Session;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentDashboardBinding;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingSection;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;

import java.util.List;

public class  DashboardFragment extends Fragment implements DrivingSession.DrivingRouteListener {
    private MapView mapView;
    Session session;
    Point ROUTE_START_LOCATION = new Point(47.229410,39.718281);
    Point ROUTE_END_LOCATION = new Point(47.214004,39.794605);
    Point SCREEN_CENTER = new Point(
            (ROUTE_START_LOCATION.getLatitude()
                    + ROUTE_END_LOCATION.getLatitude())/2,
            (ROUTE_START_LOCATION.getLongitude()
            + ROUTE_END_LOCATION.getLongitude())/2);
    MapObjectCollection mapObjects = null;
    DrivingRoute drivingRoute = null;
    DrivingSection drivingSection = null;

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
            binding = FragmentDashboardBinding.inflate(inflater, container, false);
            View root = binding.getRoot();
            MapKitFactory.initialize(getContext());
            mapView = binding.mapView;

            return root;
        }


    @Override
    public void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        MapKitFactory.getInstance().onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDrivingRoutes(@NonNull List<DrivingRoute> list) {

    }

    @Override
    public void onDrivingRoutesError(@NonNull Error error) {

    }
}