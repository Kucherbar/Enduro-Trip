package com.example.myapplication.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.DBTrip;
import com.example.myapplication.MyTripAdapter;
import com.example.myapplication.Trip;
import com.example.myapplication.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    ListView lv;
    ArrayList<Trip> list = new ArrayList<>();
    DBTrip dbSQLite;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dbSQLite = new DBTrip(getContext());
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        lv = binding.lv;
        list = dbSQLite.selectAllTrips();
        ArrayAdapter<Trip> adapter = new MyTripAdapter(getActivity(), list);
        lv.setAdapter(adapter);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}