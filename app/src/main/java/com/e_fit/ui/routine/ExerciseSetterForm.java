package com.e_fit.ui.routine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e_fit.R;
import com.e_fit.api.ExerciseRoutineClient;
import com.e_fit.enities.Exercise;
import com.e_fit.enities.ExerciseRoutine;
import com.e_fit.enities.ExerciseType;
import com.e_fit.enities.Routine;
import com.e_fit.enities.SetType;
import com.e_fit.enities.SetTypeParcelable;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class ExerciseSetterForm extends AppCompatActivity {

    private TextView tvName, tvEstimatedDuration, tvDefDay, tvDescription;
    private RecyclerView rvExercises;
    private Button btnReturn, btnCreate;
    private ArrayList<Exercise> selectedExercises;
    private Routine routine;
    private ExerciseRoutineClient client;
    private int uploadExercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercise_setter_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadElements();
    }

    private void loadElements() {
        tvName = findViewById(R.id.tvName);
        tvEstimatedDuration = findViewById(R.id.tvEstimatedDuration);
        tvDefDay = findViewById(R.id.tvDefDay);
        tvDescription = findViewById(R.id.tvDescription);
        rvExercises = findViewById(R.id.rvExercises);
        btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(i -> {
            addExercises();
        });
        btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(i -> {
            super.onBackPressed();
        });
        client = new ExerciseRoutineClient();
        uploadExercises = 0;
        loadData();
    }

    private void loadData() {
        routine = (Routine) getIntent().getParcelableExtra("routine");
        selectedExercises = (ArrayList<Exercise>) getIntent().getSerializableExtra("exercises");

        if (routine != null && selectedExercises != null) {
            tvName.setText(routine.getName());
            tvEstimatedDuration.setText(routine.getEstimatedDuration());
            tvDefDay.setText(routine.getDefaultDays().toString());
            tvDescription.setText(routine.getDescription());

            //Exercises Forms
            ExerciseRoutineSetterAdapter adapter = new ExerciseRoutineSetterAdapter(selectedExercises);
            rvExercises.setLayoutManager(new LinearLayoutManager(this));
            rvExercises.setAdapter(adapter);
        }
    }

    private void addExercises() {
        ExerciseRoutineSetterAdapter adapter = (ExerciseRoutineSetterAdapter) rvExercises.getAdapter();

        for (int i = 0; i < adapter.getItemCount(); i++) {
            RecyclerView.ViewHolder viewHolder = rvExercises.findViewHolderForAdapterPosition(i);
            if (viewHolder instanceof ExerciseRoutineSetterAdapter.ViewHolder) {
                ExerciseRoutineSetterAdapter.ViewHolder holder = (ExerciseRoutineSetterAdapter.ViewHolder) viewHolder;

                // Obtener los datos de cada vista
                String exerciseName = holder.tvName.getText().toString();
                Integer numberOfSets = holder.npSets.getValue();
                int isSuperSet = holder.cbSuper.isChecked()? 1 : 0;
                Long restTime = parseRestTimeToSeconds(holder.spRest.getSelectedItem().toString());
                ExerciseType exerciseType = (ExerciseType) holder.spType.getSelectedItem();

                // Crear una lista de tipos de series seleccionados en los Spinners
                List<SetTypeParcelable> setTypesParcelable = new ArrayList<>();
                List<String> setTypeNamesForApi = new ArrayList<>(); // This list will hold the string names

                for (int j = 0; j < holder.spinnerContainer.getChildCount(); j++) {
                    View view = holder.spinnerContainer.getChildAt(j);
                    if (view instanceof Spinner) {
                        Spinner spinner = (Spinner) view;
                        SetType setType = (SetType) spinner.getSelectedItem();

                        SetTypeParcelable setTypeParcelable = new SetTypeParcelable(setType);
                        setTypesParcelable.add(setTypeParcelable);

                        if (setType != null) setTypeNamesForApi.add(setType.name());
                    }
                }

                //Recupero el ejercicio
                Exercise exercise = selectedExercises.stream()
                        .filter(e->e.getName().equals(exerciseName))
                        .findFirst().orElse(null);

                btnCreate.setVisibility(View.GONE);
                client.postExerciseRoutine(
                        exercise.getExerciseId(),
                        routine.getRoutineId().toString(),
                        numberOfSets,
                        restTime,
                        isSuperSet,
                        exerciseType.toString().toUpperCase(),
                        i,
                        setTypeNamesForApi,
                        new ExerciseRoutineClient.PostExerciseRoutineCallback() {
                            @Override
                            public void onExerciseRoutinePosted(boolean success) {
                                if (success) {
                                    redirect(numberOfSets);
                                } else {
                                    Toast.makeText(ExerciseSetterForm.this, "No se pudo asociar el ejercicio a esta rutina", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(ExerciseSetterForm.this, "No se pudo asociar el ejercicio a esta rutina", Toast.LENGTH_SHORT).show();
                                btnCreate.setVisibility(View.VISIBLE);
                                Log.e("ExerciseSetterForm", "Error posting exercise routine: " + e.getMessage(), e);
                            }
                        }
                );
            }
        }
    }

    public Long parseRestTimeToSeconds(String restTime) {
        if (restTime.equals("Seleccione el descanso")) {
            return 0L;
        }

        String[] parts = restTime.split(" ");
        int minutes = 0;
        int seconds = 0;

        for (int i = 0; i < parts.length; i++)
            if (parts[i].matches("\\d+")) {
                int value = Integer.parseInt(parts[i]);
                if (i + 1 < parts.length) {
                    String unit = parts[i + 1];
                    if (unit.contains("min"))minutes += value;
                    else if (unit.contains("seg"))seconds += value;
                }
            }

        return Long.valueOf(minutes * 60 + seconds);
    }

    private void redirect(int nSets){
        if(nSets == ++uploadExercises) {
            Toast.makeText(ExerciseSetterForm.this, "El ejercicio se asoció correctamente a esta rutina", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ExerciseSetterForm.this, RoutineList.class));
            finish();
        }
    }
}
