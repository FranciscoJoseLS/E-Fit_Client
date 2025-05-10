package com.e_fit.ui.routine;

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
import com.e_fit.util.MenuActivity;
import com.e_fit.util.SharedPrefs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoutineList extends MenuActivity {

    private Context context;
    private RoutineClient client;
    private ArrayList<Routine> routines;
    private TextView tvLoading;
    private ListView lvRoutines;
    private RoutineAdapter routineAdapter;
    private Button btnNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_routine_list);
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
        loadData();
    }

    private void loadData(){
        //PASAR USUARIO A LA FUNCION
        String userId=SharedPrefs.getString(this,"id","");
        client.getRoutines(userId, new RoutineClient.RoutineCallback() {
            @Override
            public void onRoutinesReceived(List<Routine> retrievedRoutines) {
                routines.clear();
                routines.addAll(retrievedRoutines);
                runOnUiThread(() -> {
                    routines.addAll(retrievedRoutines);
                    loadRoutines();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    // Muestra un mensaje de error al usuario
                    Toast.makeText(context, "Error al cargar las rutinas", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadRoutines() {
        // Filtrar rutinas activas
        ArrayList<Routine> filteredRoutines = routines.stream()
                .filter(Routine::getActive)
                .collect(Collectors.toCollection(ArrayList::new));

        if (filteredRoutines.isEmpty()) {
            tvLoading.setText(R.string.empty_routines);
        } else {
            tvLoading.setVisibility(View.GONE);
            // Pasar rutinas activas al adapter
            routineAdapter = new RoutineAdapter(this, filteredRoutines);
            lvRoutines.setAdapter(routineAdapter);
        }

        // Habilitar el botón
        btnNew.setOnClickListener(i -> {
            startActivity(new Intent(this, RoutineView.class));
        });
    }

}