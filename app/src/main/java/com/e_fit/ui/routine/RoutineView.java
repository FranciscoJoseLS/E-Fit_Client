package com.e_fit.ui.routine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.e_fit.R;
import com.e_fit.api.RoutineClient;
import com.e_fit.api.UserClient;
import com.e_fit.enities.Routine;
import com.e_fit.enities.User;
import com.e_fit.util.MenuActivity;
import com.e_fit.util.SharedPrefs;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.regex.Pattern;

public class RoutineView extends MenuActivity {

    private EditText etName, etEstimatedDuration, etDefaultDays, etDescription;
    private TextView tvTitle;
    private Button btnCreate, btnReturn, btnAddExercises, btnRemove;
    private Routine routine;
    private RoutineClient client;
    private boolean isEditMode;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_routine_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadElements();
        loadRoutineData();
    }

    private void loadElements() {
        etName = findViewById(R.id.etName);
        etEstimatedDuration = findViewById(R.id.etEstimatedDuration);
        etDefaultDays = findViewById(R.id.etDefaultDays);
        etDescription = findViewById(R.id.etDescription);
        btnCreate = findViewById(R.id.btnCreate);
        btnReturn = findViewById(R.id.btnReturn);
        btnAddExercises = findViewById(R.id.btnAddExercises);
        btnRemove = findViewById(R.id.btnRemove);
        client = new RoutineClient();
        context = this;

        btnReturn.setOnClickListener(i->{
            super.onBackPressed();
        });

        btnCreate.setOnClickListener(v -> {
            if (isEditMode) updateRoutine(); else createRoutine();
        });

        btnRemove.setOnClickListener(v -> {
            deleteRoutine();
        });
    }

    private void loadRoutineData() {
        routine = getIntent().getParcelableExtra("routine");
        isEditMode = routine != null;

        if (isEditMode) {
            tvTitle = findViewById(R.id.tvTitle);
            tvTitle.setText(routine.getName());
            etName.setVisibility(View.GONE);
            etEstimatedDuration.setText(routine.getEstimatedDuration());
            etDefaultDays.setText(String.valueOf(routine.getDefaultDays()));
            etDescription.setText(routine.getDescription());
            btnCreate.setText(R.string._update);
            //Comprobar los ejercicios de la rutina
            if(routine.getExerciseRoutines().isEmpty()) {
                //LISTAR EJERCICIOS RUTINAS

            }else{
                btnAddExercises.setVisibility(View.GONE);
            }
        } else {
            btnRemove.setVisibility(View.GONE);
            btnCreate.setText(R.string._new);
            btnAddExercises.setVisibility(View.GONE);


            etName.setText("Name Test");
            etEstimatedDuration.setText("1:23");
            etDefaultDays.setText("Lunes");
            etDescription.setText("DESCRIPTION TEST");
        }
    }

    private void createRoutine() {
        String name = etName.getText().toString().trim();
        String estimatedDuration = etEstimatedDuration.getText().toString().trim();
        String defaultDaysStr = etDefaultDays.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (name.isEmpty() || estimatedDuration.isEmpty() || defaultDaysStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Debe rellenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isValidTimeFormat(estimatedDuration)){
            Toast.makeText(this, "La duración estimada debe de ser 'h:mm'", Toast.LENGTH_SHORT).show();
            return;
        }

        int defaultDays;
        switch (normalizeString(defaultDaysStr)) {
            case "lunes":defaultDays = 1;break;
            case "martes":defaultDays = 2;break;
            case "miercoles":defaultDays = 3;break;
            case "jueves":defaultDays = 4;break;
            case "viernes":defaultDays = 5;break;
            case "sabado":defaultDays = 6;break;
            case "domingo":defaultDays = 7;break;
            default:defaultDays = 0;break;
        }

        // Crear una nueva rutina con los datos ingresados
        Routine newRoutine = new Routine(name, estimatedDuration, defaultDays, description, true);
        client.createRoutine(newRoutine,SharedPrefs.getString(this, "id", ""), new RoutineClient.RoutineOperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "La rutina ha sido creada", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(context, RoutineList.class));
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Error al crear la rutina", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRoutine() {
        String estimatedDuration = etEstimatedDuration.getText().toString().trim();
        String defaultDaysStr = etDefaultDays.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (estimatedDuration.isEmpty() || defaultDaysStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Debe rellenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int defaultDays;
        switch (normalizeString(defaultDaysStr)) {
            case "lunes":defaultDays = 1;break;
            case "martes":defaultDays = 2;break;
            case "miercoles":defaultDays = 3;break;
            case "jueves":defaultDays = 4;break;
            case "viernes":defaultDays = 5;break;
            case "sabado":defaultDays = 6;break;
            case "domingo":defaultDays = 7;break;
            default:defaultDays = 0;break;
        }

        // Actualizar la rutina existente con los nuevos datos ingresados
        routine.setEstimatedDuration(estimatedDuration);
        routine.setDefaultDays(defaultDays);
        routine.setDescription(description);
        Context context = this;

        client.updateRoutine(routine,SharedPrefs.getString(this, "id", ""),  new RoutineClient.RoutineOperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "La rutina ha sido actualizada", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(context,RoutineList.class));
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Error actualizar la rutina", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteRoutine() {
        routine.setActive(false);
        client.updateRoutine(routine, SharedPrefs.getString(this, "id", ""), new RoutineClient.RoutineOperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(context, "La rutina ha sido eliminada", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(context,RoutineList.class));
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Error al eliminar la rutina", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String normalizeString(String input) {
        // Eliminar acentos y coincidencia de mayusculas
        String lowerCase = input.toLowerCase();
        String normalized = Normalizer.normalize(lowerCase, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized;
    }
    public static boolean isValidTimeFormat(String input) {
        String regex = "^\\d+:\\d{2}$";
        return Pattern.matches(regex, input);
    }

}