package com.example.myapplication.ui.notifications;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.DBTrip;
import com.example.myapplication.MyLocation;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentDashboardBinding;
import com.example.myapplication.databinding.MapRouteBinding;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.Rect;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.List;

public class MapRouteDialogFragment extends DialogFragment implements DrivingSession.DrivingRouteListener {
    private MapView mapView;
    DBTrip dbSQLite;
    private MapRouteBinding binding;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MapKitFactory.initialize(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        binding = binding.inflate(inflater, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = inflater.inflate(R.layout.map_route, null);
        builder.setView(v);

        mapView = binding.mapRouteView;
        ArrayList<MyLocation> arr = dbSQLite.selectLocations(Long.valueOf(getTag()));

        MapObjectTapListener listener = new MapObjectTapListener() {
            @Override
            public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
                Toast.makeText(getContext(),"speed was " + mapObject.getUserData().toString(),Toast.LENGTH_SHORT).show();
                return false;
            }
        };
        for (int i = 0; i <= arr.toArray().length; i++) {
            ImageProvider imageProvider = ImageProvider.fromResource(getContext(), R.drawable.ic_mark_red);
            PlacemarkMapObject placemark = mapView.getMap().getMapObjects()
                    .addPlacemark(new Point(arr.get(i).getLatidute(), arr.get(i).getLongitude()), imageProvider);
            placemark.addTapListener(listener);
            placemark.setUserData(new Integer(arr.get(i).getSpeed()));
            placemark.setIconStyle(new IconStyle().setTappableArea(new Rect()));
            IconStyle iconStyleEnduro = new IconStyle();
            iconStyleEnduro.setAnchor(new PointF(0.5f, 1.0f)).setScale(0.9f);
            placemark.useCompositeIcon().setIcon("enduro",ImageProvider.fromResource(getContext(),R.drawable.ic_enduro_black), iconStyleEnduro);
            IconStyle iconStyleRoad = new IconStyle();
            iconStyleRoad.setAnchor(new PointF(0.5f, 0.5f)).setScale(0.5f);
            placemark.useCompositeIcon().setIcon("road",ImageProvider.fromResource(getContext(),R.drawable.ic_circle_road), iconStyleRoad);
        }
        mapView.getMap().move(new CameraPosition(new Point(arr.get(0).getLatidute(),arr.get(0).getLongitude())
                ,17.0f, 150.0f,30.0f));

        return builder.create();
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
