package com.example.myapplication.ui.speedometer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;
import com.example.myapplication.databinding.DialogNameBinding;

public class MyDialogFragment extends DialogFragment {
    private DialogNameBinding binding;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        binding = DialogNameBinding.inflate(inflater, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = inflater.inflate(R.layout.dialog_name, null);


        builder.setView(v)
                // Add action buttons
                .setPositiveButton(R.string.setName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText ed = v.findViewById(R.id.tripname);
                        SpeedometerFragment.setTripName(ed.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cansel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SpeedometerFragment.setTripName("Нет названия");
                    }
                });

        return builder.create();
    }
}
