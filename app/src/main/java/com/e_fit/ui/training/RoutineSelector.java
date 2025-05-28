package com.e_fit.ui.training;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.e_fit.R;
import com.e_fit.api.RoutineClient;
import com.e_fit.enities.Routine;
import com.e_fit.ui.routine.RoutineAdapter;
import com.e_fit.ui.routine.RoutineView;
import com.e_fit.util.MenuActivity;
import com.e_fit.util.SharedPrefs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RoutineSelector extends MenuActivity {

    private Context context;
    private RoutineClient client;
    private ArrayList<Routine> routines;
    private TextView tvLoading;
    private ListView lvRoutines;
    private Button btnNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_routine_selector);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadElements();
    }

    private void loadElements() {
        context = this;
        client = new RoutineClient();
        routines = new ArrayList<>();
        tvLoading = findViewById(R.id.tvLoading);
        lvRoutines = findViewById(R.id.lvRoutines);
        btnNew = findViewById(R.id.btnNew);
        btnNew.setVisibility(View.GONE);
        loadData();
    }

    private void loadData(){
        String userId=SharedPrefs.getString(this,"id","");
        client.getRoutines(userId, new RoutineClient.RoutineCallback() {
            @Override
            public void onRoutinesReceived(List<Routine> retrievedRoutines) {
                routines.clear();
                routines.addAll(retrievedRoutines);
                runOnUiThread(() -> {
                    loadRoutines();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    // Muestra un mensaje de error al usuario
                    Toast.makeText(context, "Error al cargar las rutinas", Toast.LENGTH_SHORT).show();
                    tvLoading.setVisibility(View.GONE);
                });
            }
        });
        // Habilitar el botón
        btnNew.setOnClickListener(i -> {
            startActivity(new Intent(this, RoutineView.class));
        });
    }

    private void loadRoutines() {
        // Filtrar rutinas activas
        ArrayList<Routine> filteredRoutines = routines.stream()
                .filter(Routine::getActive)
                .collect(Collectors.toCollection(ArrayList::new));

        if (filteredRoutines.isEmpty()) {
            tvLoading.setText(R.string.empty_routines);
            btnNew.setVisibility(View.VISIBLE);
        } else {
            // Si la rutina corresponde al dia actual la pongo al principo
            int currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;
            Collections.sort(filteredRoutines, (r1, r2) -> {
                Integer r1Day = r1.getDefaultDays();
                Integer r2Day = r2.getDefaultDays();

                if (r1Day == null && r2Day == null) return 0;
                if (r1Day == null) return 1;
                if (r2Day == null) return -1;

                if (r1Day.equals(currentDayOfWeek))return -1;
                if (r2Day.equals(currentDayOfWeek))return 1;

                return r1Day.compareTo(r2Day);
            });
            tvLoading.setVisibility(View.GONE);
            // Pasar rutinas activas al adapter
            lvRoutines.setAdapter(new TrainingRoutineAdapter(this, filteredRoutines));
        }
    }

}