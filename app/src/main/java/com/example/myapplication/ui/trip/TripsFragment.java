package com.example.myapplication.ui.trip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.DBTrip;
import com.example.myapplication.MyTripAdapter;
import com.example.myapplication.R;
import com.example.myapplication.Trip;
import com.example.myapplication.databinding.FragmentTripsBinding;
import com.example.myapplication.databinding.TripItemBinding;

import java.util.ArrayList;

public class TripsFragment extends Fragment {

    private FragmentTripsBinding binding;
    ListView lv;
    ArrayList<Trip> list = new ArrayList<>();
    DBTrip dbSQLite;
    static boolean check;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Toast.makeText(getContext(), R.string.show_trip_route, Toast.LENGTH_SHORT).show();
        dbSQLite = new DBTrip(getContext());
        binding = FragmentTripsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        lv = binding.lv;
        list = dbSQLite.selectAllTrips();
        ArrayAdapter<Trip> adapter = new MyTripAdapter(getActivity(), list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TripItemBinding tripItemBinding = TripItemBinding.bind(view);
                long tripID = Integer.valueOf(tripItemBinding.tripID.getText() + "");
                if (dbSQLite.selectLocations(tripID).size() == 0) {
                    Toast.makeText(getContext(), R.string.no_trip_points, Toast.LENGTH_SHORT).show();
                    return;
                }

                MapRouteDialogFragment map = new MapRouteDialogFragment();
                map.show(getActivity().getSupportFragmentManager(), tripID + "");
//                Bundle bundle = new Bundle();
//                bundle.putLong("tripId",tripID);
//                Navigation.findNavController(view).navigate(R.id.navigation_dashboard,bundle);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}