package com.example.myapplication.ui.trip;

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
import com.example.myapplication.MainActivity;
import com.example.myapplication.MyLocation;
import com.example.myapplication.R;
import com.example.myapplication.databinding.MapRouteBinding;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.PolylineMapObject;
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
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = inflater.inflate(R.layout.map_route, null);
        builder.setView(v);
        binding = binding.inflate(inflater, null , false);
        mapView = v.findViewById(R.id.map_route_view);

        MapKitFactory.initialize(getContext());

        dbSQLite = new DBTrip(getContext());

        ArrayList<MyLocation> arr = dbSQLite.selectLocations(Long.valueOf(getTag()));

        MapObjectTapListener listener = new MapObjectTapListener() {
            @Override
            public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
                Toast.makeText(getContext(),getString(R.string.speed) + mapObject.getUserData().toString(),Toast.LENGTH_SHORT).show();
                return false;
            }
        };
        ImageProvider enduroImg = ImageProvider.fromResource(getContext(), R.drawable.ic_enduro);
        ImageProvider flagImg = ImageProvider.fromResource(getContext(), R.drawable.ic_flag_finish);
        ImageProvider pointImg = ImageProvider.fromResource(getContext(), R.drawable.ic_point_black);


        //Enduro mark
        PlacemarkMapObject placemarkStart = mapView.getMap().getMapObjects()
                .addPlacemark(new Point(arr.get(0).getLatidute(), arr.get(0).getLongitude()));
        placemarkStart.setIcon(enduroImg);

        placemarkStart.addTapListener(listener);
        placemarkStart.setUserData(new Integer(arr.get(0).getSpeed()));

        IconStyle iconStyleStart = new IconStyle().setTappableArea(new Rect(new PointF(50F,50f),new PointF(50F,50f)))
                        .setAnchor(new PointF(0.3f,0.7f));//.setScale(0.065f)
        placemarkStart.setIconStyle(iconStyleStart);
        //

        //Finish flag mark
        PlacemarkMapObject placemarkFinish = mapView.getMap().getMapObjects()
                .addPlacemark(new Point(arr.get(arr.size() - 1).getLatidute(), arr.get(arr.size() - 1).getLongitude()));

        placemarkFinish.addTapListener(listener);
        placemarkFinish.setUserData(new Integer(arr.get(arr.size() - 1).getSpeed()));

        IconStyle iconFlagStyle = new IconStyle()
                .setAnchor(new PointF(0.2f,0.9f));//.setScale(0.065f)
        IconStyle iconPointStyle = new IconStyle()
                .setAnchor(new PointF(0.5f,0.6f)).setScale(0.3f);

        placemarkFinish.useCompositeIcon().setIcon(getString(R.string.flag),flagImg, iconFlagStyle);
        placemarkFinish.useCompositeIcon().setIcon(getString(R.string.point), pointImg, iconPointStyle);
        //


        ArrayList<Point> pointArr = new ArrayList<>();

        for (int i = 0; i < arr.toArray().length; i++) {
            Point point = new Point((arr.get(i).getLatidute()), arr.get(i).getLongitude());
            pointArr.add(point);
//            placemark.setIconStyle(new IconStyle());
//            IconStyle iconStyleEnduro = new IconStyle();
//            iconStyleEnduro.setScale(0.9f).setAnchor(new PointF(0.5f, 1.0f)).setZIndex(1f);
//            placemark.useCompositeIcon().setIcon("enduro",ImageProvider.fromResource(getContext(),R.drawable.ic_enduro_black), iconStyleEnduro);
//            IconStyle iconStyleRoad = new IconStyle();
//            iconStyleRoad.setScale(0.5f).setAnchor(new PointF(0.9f, 1.0f)).setZIndex(2F);
//            placemark.useCompositeIcon().setIcon("road",ImageProvider.fromResource(getContext(),R.drawable.ic_road_circle_black), iconStyleRoad);
        }
        Polyline polyline = new Polyline(pointArr);
        PolylineMapObject polylineMapObject = mapView.getMap().getMapObjects().addPolyline(polyline);
        polylineMapObject.setStrokeColor(MainActivity.polylineColor);
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
