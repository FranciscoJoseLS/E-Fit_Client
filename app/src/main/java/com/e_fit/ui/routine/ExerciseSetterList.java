package com.e_fit.ui.routine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.e_fit.R;
import com.e_fit.api.ExerciseClient;
import com.e_fit.enities.Exercise;
import com.e_fit.enities.MuscularGroup;
import com.e_fit.enities.Routine;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExerciseSetterList extends AppCompatActivity {

    private Context context;
    private ExerciseClient client;
    private ArrayList<Exercise> exercises, filteredExercises;
    private EditText etName;
    private TextView tvLoading, tvlabel;
    private Spinner spMuscularGroup;
    private ListView lvExercises;
    private Routine routine;
    private ExerciseSetAdapter exerciseAdapter;
    private Button btnAddExercises, btnReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercise_setter_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadData();
    }

    private void loadData(){
        context = this;
        client = new ExerciseClient();
        exercises = new ArrayList<>();
        filteredExercises = new ArrayList<>();
        //Recupero la rutina
        routine = getIntent().getParcelableExtra("routine");
        tvlabel = findViewById(R.id.tvLabel);
        tvlabel.setText(routine.getName());
        loadExercises();
    }

    private void loadExercises(){
        client.getExercises(new ExerciseClient.ExerciseCallback() {
            @Override
            public void onExercisesReceived(List<Exercise> retrievedExercises) {
                exercises.clear();
                exercises.addAll(retrievedExercises);
                runOnUiThread(() -> {
                    loadElements();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    // Muestra un mensaje de error al usuario
                    Toast.makeText(context, "Error al cargar los ejercicios", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadElements() {
        tvLoading = findViewById(R.id.tvLoading);
        tvLoading.setVisibility(View.GONE);
        etName = findViewById(R.id.etName);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Spinner
        spMuscularGroup = findViewById(R.id.spMuscularGroup);
        ArrayAdapter<MuscularGroup> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, MuscularGroup.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMuscularGroup.setAdapter(adapter);
        spMuscularGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                search();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        lvExercises = findViewById(R.id.lvExercises);
        exerciseAdapter = new ExerciseSetAdapter(this, exercises);
        lvExercises.setAdapter(exerciseAdapter);
        btnAddExercises = findViewById(R.id.btnAddExercises);
        btnAddExercises.setOnClickListener(v -> {
            addExercises();
        });
        btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(i->{
            super.onBackPressed();
        });
    }

    public void search(){
        try {
            //Filtro los ejercicios por nombre y grupo muscular
            String nameQuery = etName.getText().toString().toLowerCase(Locale.getDefault());
            MuscularGroup selectedGroup = (MuscularGroup) spMuscularGroup.getSelectedItem();

            filteredExercises.clear();
            for (Exercise exercise : exercises) {
                boolean nameMatches = exercise.getName().toLowerCase(Locale.getDefault()).contains(nameQuery);
                boolean groupMatches = selectedGroup == MuscularGroup.TODOS || exercise.getMuscularGroup() == selectedGroup;

                if (nameMatches && groupMatches)
                    filteredExercises.add(exercise);
            }

            if (filteredExercises.isEmpty())
                exerciseAdapter.updateData(exercises);
            else
                exerciseAdapter.updateData(filteredExercises);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addExercises(){
        List<Exercise> selected = exerciseAdapter.getSelectedExercises();

        if (selected.size()<3) {
            Toast.makeText(this, "Selecciona al menos 3 ejercicios", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent i = new Intent(this, ExerciseSetterForm.class);
        i.putExtra("routine", routine);
        i.putExtra("exercises", new ArrayList<>(selected));
        startActivity(i);
    }
}
