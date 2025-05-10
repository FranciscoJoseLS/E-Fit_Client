package com.e_fit.ui.routine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.e_fit.R;
import com.e_fit.enities.Routine;

import java.util.ArrayList;

public class RoutineAdapter extends ArrayAdapter<Routine> {
    private ArrayList<Routine> routines;

    public RoutineAdapter(Context c, ArrayList<Routine> routines) {
        super(c, R.layout.routine_item_view, routines);
        this.routines = routines;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int posicion, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater mostrado = LayoutInflater.from(getContext());
        @SuppressLint("ViewHolder")
        View view = mostrado.inflate(R.layout.routine_item_view, parent, false);

        // Muestro los datos
        TextView tvName = view.findViewById(R.id.tvName);
        tvName.setText(routines.get(posicion).getName());

        TextView tvDay = view.findViewById(R.id.tvDay);
        tvDay.setText(String.valueOf(routines.get(posicion).getDefaultDays())); // Convertir entero a cadena

        TextView tvDescription = view.findViewById(R.id.tvDescription);
        tvDescription.setText(routines.get(posicion).getDescription());

        TextView tvDuration = view.findViewById(R.id.tvDuration);
        tvDuration.setText(routines.get(posicion).getEstimatedDuration());

        // Incorporo click en la vista
        Button btn = view.findViewById(R.id.btnVer);
        btn.setOnClickListener(it -> {
            Intent i = new Intent(getContext(), RoutineView.class);
            i.putExtra("routine", routines.get(posicion));
            getContext().startActivity(i);
        });

        return view;
    }

    public void updateData(ArrayList<Routine> routines) {
        this.routines = routines;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return routines.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
