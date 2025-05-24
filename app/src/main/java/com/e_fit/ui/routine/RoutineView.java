package com.e_fit.ui.routine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e_fit.R;
import com.e_fit.api.RoutineClient; // Importa la clase RoutineClient
import com.e_fit.enities.ExerciseRoutine;
import com.e_fit.enities.Routine;
import com.e_fit.util.MenuActivity;
import com.e_fit.util.SharedPrefs;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RoutineView extends MenuActivity {

    private EditText etName, etEstimatedDuration, etDescription;
    private TextView tvTitle;
    private Button btnCreate, btnReturn, btnAddExercises, btnRemove;
    private Routine routine;
    private RoutineClient client;
    private boolean isEditMode;
    private Spinner spDay;
    private RecyclerView rvExercises;
    private ExerciseRoutineAdapter exerciseAdapter;
    private Context context;
    private String routineId;
    String[] daysOfWeek = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

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
        spDay = findViewById(R.id.spDay);
        rvExercises = findViewById(R.id.rvExercises);
        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        etDescription = findViewById(R.id.etDescription);
        btnCreate = findViewById(R.id.btnCreate);
        btnReturn = findViewById(R.id.btnReturn);
        btnAddExercises = findViewById(R.id.btnAddExercises);
        btnRemove = findViewById(R.id.btnRemove);
        client = new RoutineClient();
        context = this;
        btnAddExercises.setVisibility(View.GONE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, daysOfWeek) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(ContextCompat.getColor(RoutineView.this, R.color.black_3));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTextColor(ContextCompat.getColor(RoutineView.this, R.color.white));
                return view;
            }
        };
        spDay.setAdapter(adapter);

        btnReturn.setOnClickListener(i -> super.onBackPressed());

        btnCreate.setOnClickListener(v -> {
            if (isEditMode) updateRoutine();
            else createRoutine();
        });

        btnAddExercises.setOnClickListener(v -> {
            Intent i = new Intent(this, ExerciseSetterList.class);
            i.putExtra("routine", routine);
            startActivity(i);
        });
        btnRemove.setOnClickListener(v -> deleteRoutine());
    }

    private void loadRoutineData() {
        routine = getIntent().getParcelableExtra("routine");
        isEditMode = routine != null;

        if (isEditMode) {
            tvTitle = findViewById(R.id.tvTitle);
            tvTitle.setText(routine.getName());
            etName.setVisibility(View.GONE);
            etEstimatedDuration.setText(routine.getEstimatedDuration());
            etDescription.setText(routine.getDescription());
            btnCreate.setText(R.string._update);
            routineId = routine.getRoutineId().toString();
            int defaultDay = routine.getDefaultDays();
            spDay.setSelection(defaultDay - 1);

            // Inicializa el adapter *aquí*, pero con una lista vacía.
            //Los datos llegarán asíncronamente.
            exerciseAdapter = new ExerciseRoutineAdapter(this, new ArrayList<>());
            rvExercises.setAdapter(exerciseAdapter);
            // Lógica para mostrar/ocultar el botón basada en si hay ejercicios
            fetchExercisesForRoutine(routineId);

        } else {
            btnRemove.setVisibility(View.GONE);
            btnCreate.setText(R.string._new);
        }
    }

    private void createRoutine() {
        String name = etName.getText().toString().trim();
        String estimatedDuration = etEstimatedDuration.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(estimatedDuration) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Debe rellenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidTimeFormat(estimatedDuration)) {
            Toast.makeText(this, "La duración estimada debe de ser 'h:mm'", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedDay = spDay.getSelectedItemPosition() + 1;

        Routine newRoutine = new Routine(name, estimatedDuration, selectedDay, description, true);
        client.createRoutine(newRoutine, SharedPrefs.getString(this, "id", ""), new RoutineClient.RoutineOperationCallback() { // Usa RoutineClient
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
        String description = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(estimatedDuration) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Debe rellenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedDay = spDay.getSelectedItemPosition() + 1;

        routine.setEstimatedDuration(estimatedDuration);
        routine.setDefaultDays(selectedDay);
        routine.setDescription(description);

        client.updateRoutine(routine, SharedPrefs.getString(this, "id", ""), new RoutineClient.RoutineOperationCallback() {  // Usa RoutineClient
            @Override
            public void onSuccess() {
                Toast.makeText(context, "La rutina ha sido actualizada", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(context, RoutineList.class));
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Error al actualizar la rutina", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteRoutine() {
        routine.setActive(false);
        client.updateRoutine(routine, SharedPrefs.getString(this, "id", ""), new RoutineClient.RoutineOperationCallback() { // Usa RoutineClient
            @Override
            public void onSuccess() {
                Toast.makeText(context, "La rutina ha sido eliminada", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(context, RoutineList.class));
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Error al eliminar la rutina", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static boolean isValidTimeFormat(String input) {
        String regex = "^\\d+:\\d{2}$";
        return Pattern.matches(regex, input);
    }

    private void fetchExercisesForRoutine(String routineId) {
        client.getExercisesForRoutine(routineId, new RoutineClient.ExerciseRoutinesCallback() { // Usa RoutineClient
            @Override
            public void onExerciseRoutinesReceived(List<ExerciseRoutine> exerciseRoutinesList) {
                // Actualizar la lista de ejercicios y notificar al adaptador
                if (exerciseAdapter != null) {
                    exerciseAdapter.setExerciseRoutines(exerciseRoutinesList);
                    exerciseAdapter.notifyDataSetChanged();
                } else {
                    Log.e("RoutineView", "exerciseAdapter is null in fetchExercisesForRoutine");
                }
                // Mostrar el botón si la lista está vacía, ocultar si no.
                btnAddExercises.setVisibility(exerciseRoutinesList.isEmpty() ? View.VISIBLE : View.GONE);

            }

            @Override
            public void onError(Exception e) {
                // Manejar el error al obtener los ejercicios
                Toast.makeText(RoutineView.this, "Error al obtener ejercicios", Toast.LENGTH_SHORT).show();
                // Considerar mostrar el botón de añadir ejercicios aquí también, dependiendo de la lógica de la app.
                btnAddExercises.setVisibility(View.VISIBLE);
            }
        });
    }
}