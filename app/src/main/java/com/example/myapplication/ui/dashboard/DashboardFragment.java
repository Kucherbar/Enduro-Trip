package com.example.myapplication.ui.dashboard;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.se.omapi.Session;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.myapplication.DBTrip;
import com.example.myapplication.GPSHelpers;
import com.example.myapplication.MainActivity;
import com.example.myapplication.MyLocation;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentDashboardBinding;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingRouterType;
import com.yandex.mapkit.directions.driving.DrivingSection;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.directions.driving.VehicleType;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.location.SubscriptionSettings;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.places.panorama.IconImageFactory;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DashboardFragment extends Fragment implements DrivingSession.DrivingRouteListener, SensorEventListener {
    private MapView mapView;
    LocationManager locationManager;
    LocationListener locationListener;
    DrivingRouter drivingRouter;
    DrivingOptions drivingOptions;
    VehicleOptions vehicleOptions;
    InputListener inputListener = null;
    DrivingSession drivingSession;
    DrivingSession.DrivingRouteListener drivingRouteListener;
    PlacemarkMapObject currentPlacemark;
    MapObjectCollection mapObjects = null;
    ArrayList<RequestPoint> buildPoints;
    PolylineMapObject polylineRoute;
    DBTrip dbSQLite;
    static CameraPosition cameraOnStart;
    static Polyline polyline;
    static int tapChecker;
    int i;

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        MapKitFactory.initialize(getContext());
        mapView = binding.mapView;
        dbSQLite = new DBTrip(getContext());
        if (cameraOnStart != null) mapView.getMap().move(cameraOnStart);
        else {

            MyLocation location = dbSQLite.selectLastLocation();
            if (!(location == null)) {
                Point point = new Point(location.getLatidute(), location.getLongitude());
                mapView.getMap().move(new CameraPosition(point, 8.0f, 150.0f, 30.0f));
            }
        }
        if (polyline != null && tapChecker % 2 != 0) {
            polylineRoute = mapView.getMap().getMapObjects().addPolyline(polyline);
            polylineRoute.setStrokeColor(MainActivity.polylineColor);
        }

        mapObjects = mapView.getMap().getMapObjects();
        ImageProvider enduroImg = ImageProvider.fromResource(getContext(), R.drawable.ic_enduro_on_two_wheels);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        IconStyle iconStyle = new IconStyle().setScale(0.08f);
        i = 0;
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (i != 0) {
                    mapObjects.remove(currentPlacemark);
                }
                currentPlacemark = mapView.getMap().getMapObjects().addPlacemark(new Point(location.getLatitude(), location.getLongitude()));
                currentPlacemark.setIcon(enduroImg);
                currentPlacemark.setIconStyle(iconStyle);
                if (i == 0 && cameraOnStart == null) {
                    mapView.getMap().move(new CameraPosition(
                        new Point(location.getLatitude(), location.getLongitude()), 14f, 150f, 30f
                    ));
                }
                i++;
            }
        };

        inputListener = new InputListener() {
            @Override
            public void onMapTap(@NonNull Map map, @NonNull Point point) {
                if (currentPlacemark != null && tapChecker % 2 == 0) {
                    buildPoints = new ArrayList<>();
                    Point geometry = currentPlacemark.getGeometry();
                    geometry = new Point(geometry.getLatitude(), geometry.getLongitude());
                    buildPoints.add(new RequestPoint(
                            geometry,
                            RequestPointType.WAYPOINT,
                            null,
                            null,
                            null));
                    buildPoints.add(new RequestPoint(point,
                            RequestPointType.WAYPOINT,
                            null,
                            null,
                            null));
                    showRoute();
                    tapChecker++;
                } else if (tapChecker % 2 != 0) {
                    mapObjects.remove(polylineRoute);
                    tapChecker++;
                }
                ;
            }

            @Override
            public void onMapLongTap(@NonNull Map map, @NonNull Point point) {

            }
        };
        mapView.getMap().addInputListener(inputListener);
        Toast.makeText(getContext(), "Постройте маршрут тапом", Toast.LENGTH_SHORT).show();

        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.ONLINE);
        drivingOptions = new DrivingOptions().setRoutesCount(1);
        vehicleOptions = new VehicleOptions().setVehicleType(VehicleType.MOTO);

        GPSHelpers.chekGPS(getActivity(), locationManager, locationListener);
        View root = binding.getRoot();
        return root;
    }

    public void showRoute() {

        drivingSession = drivingRouter.requestRoutes(buildPoints, drivingOptions, vehicleOptions, drivingRouteListener = new DrivingSession.DrivingRouteListener() {
            @Override
            public void onDrivingRoutes(@NonNull List<DrivingRoute> list) {
                polyline = new Polyline((list.get(0).getGeometry().getPoints()));
                polylineRoute = mapView.getMap().getMapObjects().addPolyline(polyline);
                polylineRoute.setStrokeColor(MainActivity.polylineColor);
            }

            @Override
            public void onDrivingRoutesError(@NonNull Error error) {
                Toast.makeText(getContext(), "ошибка", Toast.LENGTH_SHORT).show();
            }
        });
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
        cameraOnStart = mapView.getMap().getCameraPosition();
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

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}