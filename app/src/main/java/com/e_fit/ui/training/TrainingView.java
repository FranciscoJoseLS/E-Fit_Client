package com.e_fit.ui.training;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.e_fit.api.RoutineClient;
import com.e_fit.api.ScoreClient;
import com.e_fit.enities.Exercise;
import com.e_fit.enities.ExerciseRoutine;
import com.e_fit.enities.Routine;
import com.e_fit.util.SharedPrefs;

import java.util.ArrayList;
import java.util.List;

public class TrainingView extends AppCompatActivity implements OnSetCheckedListener {
    private Context context;
    private TextView tvTitle, tvDuration, tvDescription, tvDay, tvNsets;
    private Button btnReturn, btnSave;
    private Routine routine;
    private RoutineClient client;
    private RecyclerView rvExercises;
    private TrainingAdapter trainingAdapter;
    private String routineId;
    private ProgressBar pbNsets;
    String[] daysOfWeek = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
    int totalSets;
    int doneSets;
    int uploads;
    boolean validated;
    LinearLayout llSave;
    private ScoreClient clientScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_training_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadElements();
        loadRoutineData();
    }

    private void loadElements() {
        tvTitle = findViewById(R.id.tvTitle);
        tvDuration = findViewById(R.id.tvDuration);
        tvDay = findViewById(R.id.tvDay);
        rvExercises = findViewById(R.id.rvExercises);
        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        tvDescription = findViewById(R.id.tvDescription);
        btnReturn = findViewById(R.id.btnReturn);
        btnSave = findViewById(R.id.btnSave);
        pbNsets = findViewById(R.id.pbNsets);
        tvNsets = findViewById(R.id.tvNsets);
        context = this;
        client = new RoutineClient();
        btnReturn.setOnClickListener(i -> super.onBackPressed());
        doneSets = 0;
        uploads = 0;
        validated = false;
        llSave = findViewById(R.id.llSave);
        llSave.setVisibility(View.GONE);
        btnSave.setOnClickListener(v -> {
            createSore();
        });
        clientScore = new ScoreClient();
    }

    private void loadRoutineData() {
        routine = getIntent().getParcelableExtra("routine");

        if (routine != null) {
            tvTitle.setText(routine.getName());
            tvDuration.setText(routine.getEstimatedDuration());
            tvDescription.setText(routine.getDescription());
            routineId = routine.getRoutineId().toString();
            int defaultDay = routine.getDefaultDays();
            if (defaultDay >= 1 && defaultDay <= daysOfWeek.length)
                tvDay.setText(daysOfWeek[defaultDay - 1]);

            trainingAdapter = new TrainingAdapter(this, new ArrayList<>(), this);
            rvExercises.setAdapter(trainingAdapter);
            // Lógica para mostrar/ocultar el botón basada en si hay ejercicios
            fetchExercisesForRoutine(routineId);
        }
    }


    private void fetchExercisesForRoutine(String routineId) {
        client.getExercisesForRoutine(routineId, new RoutineClient.ExerciseRoutinesCallback() {
            @Override
            public void onExerciseRoutinesReceived(List<ExerciseRoutine> exerciseRoutinesList) {
                if (exerciseRoutinesList == null | exerciseRoutinesList.isEmpty()) {
                    Toast.makeText(com.e_fit.ui.training.TrainingView.this, "La rutina no tiene ejericicios", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                // Actualizar la lista de ejercicios y notificar al adaptador
                if (trainingAdapter != null) {
                    trainingAdapter.setExerciseRoutines(exerciseRoutinesList);
                    trainingAdapter.notifyDataSetChanged();
                }
                //establezo los sets totales
                for (ExerciseRoutine er : exerciseRoutinesList)
                    totalSets += er.getnSets();
                tvNsets.setText(doneSets + "/" + totalSets);
                pbNsets.setMax(totalSets);
                //DESCOMENTAR SOLO PARA PRUEBAS
                //doneSets = totalSets;
            }

            @Override
            public void onError(Exception e) {
                // Manejar el error al obtener los ejercicios
                Toast.makeText(com.e_fit.ui.training.TrainingView.this, "Error al obtener ejercicios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSetChecked(int exercisePosition, int setPosition, boolean isChecked) {
        //Ajusto el numero de ejercicios realizados
        if (isChecked) doneSets++;
        else doneSets--;
        tvNsets.setText(doneSets + "/" + totalSets);
        //Actualizo el ProggressBar
        pbNsets.setProgress(doneSets);
        //Compruebo el boton y el estado de la validación
        if (doneSets == totalSets && totalSets > 0)
            llSave.setVisibility(View.VISIBLE);
        else llSave.setVisibility(View.GONE);
    }

    private void createSore() {
        //Itero los ejercicios creando una cadena [50*10/50*10/50*10]
        for (int i = 0; i < trainingAdapter.getItemCount(); i++) {
            TrainingAdapter.ViewHolder exerciseHolder = (TrainingAdapter.ViewHolder) rvExercises.findViewHolderForAdapterPosition(i);
            Exercise exercise = trainingAdapter.getExerciseList().get(i).getExercise();
            if (exerciseHolder != null) {
                StringBuilder exerciseSetsData = new StringBuilder("[");
                RecyclerView lvSets = exerciseHolder.lvSets;
                SetAdapter setAdapter = (SetAdapter) lvSets.getAdapter();
                for (int j = 0; j < setAdapter.getItemCount(); j++) {
                    SetAdapter.ViewHolder setHolder = (SetAdapter.ViewHolder) lvSets.findViewHolderForAdapterPosition(j);
                    if (setHolder != null) {
                        String kgValue = setHolder.tvKg.getText().toString();
                        String repValue = setHolder.tvRep.getText().toString();
                        //Añado los datos del String
                        if (!TextUtils.isEmpty(kgValue) && !TextUtils.isEmpty(repValue)) {
                            if (j > 0) exerciseSetsData.append("/");
                            exerciseSetsData.append(kgValue).append("*").append(repValue);
                        }
                    }
                }
                exerciseSetsData.append("]");
                saveScore(exercise.getExerciseId(), routine.getRoutineId().toString(), exerciseHolder.tvComments.getText().toString(), exerciseSetsData.toString());
            }
        }
    }

    private void saveScore(Long exercise,String routine, String comment,String load){
        String userId= SharedPrefs.getString(this,"id","");
        clientScore.postScore(userId, exercise, routine, comment, load, new ScoreClient.PostScoreCallback() {
            @Override
            public void onScorePosted(boolean success) {
                Toast.makeText(context, "Subiendo ...", Toast.LENGTH_SHORT).show();
                if(++uploads==trainingAdapter.getExerciseList().size()){
                    Toast.makeText(context, "Se ha registrado el correctamente", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(com.e_fit.ui.training.TrainingView.this, RoutineSelector.class));
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Error: no se pudo registrar el entreno", Toast.LENGTH_SHORT).show();
            }
        });
    }
}