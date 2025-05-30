package com.example.myapplication.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.work.Data;

import com.example.myapplication.DBTrip;
import com.example.myapplication.MainActivity;
import com.example.myapplication.MyLocation;
import com.example.myapplication.MyTripAdapter;
import com.example.myapplication.R;
import com.example.myapplication.Trip;
import com.example.myapplication.databinding.FragmentNotificationsBinding;
import com.example.myapplication.databinding.TripItemBinding;
import com.example.myapplication.ui.dashboard.DashboardFragment;

import java.util.ArrayList;
import java.util.Map;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    ListView lv;
    ArrayList<Trip> list = new ArrayList<>();
    DBTrip dbSQLite;
    static boolean check;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        check = true;
        dbSQLite = new DBTrip(getContext());
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        lv = binding.lv;
        list = dbSQLite.selectAllTrips();
        ArrayAdapter<Trip> adapter = new MyTripAdapter(getActivity(), list);
        lv.setAdapter(adapter);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TripItemBinding tripItemBinding = TripItemBinding.bind(view);
                long tripID = Integer.valueOf(tripItemBinding.tripID.getText() + "") ;
                MapRouteDialogFragment map = new MapRouteDialogFragment();
                map.show(getActivity().getSupportFragmentManager(), tripID + "");
//                Bundle bundle = new Bundle();
//                bundle.putLong("tripId",tripID);
//                Navigation.findNavController(view).navigate(R.id.navigation_dashboard,bundle);
                return true;
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