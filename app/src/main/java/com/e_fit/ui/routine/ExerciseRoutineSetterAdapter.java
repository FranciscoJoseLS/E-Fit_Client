package com.e_fit.ui.routine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e_fit.R;
import com.e_fit.enities.Exercise;
import com.e_fit.enities.ExerciseRoutine;
import com.e_fit.enities.ExerciseType;
import com.e_fit.enities.SetType;

import java.util.ArrayList;
import java.util.List;

public class ExerciseRoutineSetterAdapter extends RecyclerView.Adapter<ExerciseRoutineSetterAdapter.ViewHolder> {

    private List<Exercise> exercises;

    public ExerciseRoutineSetterAdapter(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_routine_setter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        ExerciseRoutine routine = mapExerciseToExerciseRoutine(exercise);

        // Datos principales del ejercicio
        holder.tvName.setText(routine.getExercise().getName());

        // Spinner para el descanso
        ArrayList<String> listaDescansos = new ArrayList<>();
        listaDescansos.add("Seleccione el descanso");

        for (int sec = 5; sec <= 120; sec += 5) {
            int mins = sec / 60;
            int rest = sec % 60;

            String textoTiempo;

            if (mins > 0 && rest > 0) textoTiempo = mins + " min " + rest + " seg";
            else if (mins > 0) textoTiempo = mins + " min";
            else textoTiempo = rest + " seg";

            listaDescansos.add(textoTiempo);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(holder.itemView.getContext(), android.R.layout.simple_spinner_item, listaDescansos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spRest.setAdapter(adapter);

        //Tipo de ejercicio
        ArrayAdapter<ExerciseType> adapterType = new ArrayAdapter<>(holder.itemView.getContext(), android.R.layout.simple_spinner_item, ExerciseType.values());
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spType.setAdapter(adapterType);

        // Número de series
        holder.npSets.setMinValue(1);
        holder.npSets.setMaxValue(10);
        holder.npSets.setValue(5);

        // Agregar dinámicamente Spinners por el número de series
        holder.npSets.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateSpinners(holder, newVal);
            }
        });

        // Inicializar los Spinners con el valor inicial del NumberPicker
        updateSpinners(holder, holder.npSets.getValue());
    }

    private void updateSpinners(ViewHolder holder, int count) {
        // Limpiar el contenedor de Spinners
        holder.spinnerContainer.removeAllViews();

        // Agregar nuevos Spinners según el valor del NumberPicker
        for (int i = 0; i < count; i++) {
            Spinner spinner = new Spinner(holder.itemView.getContext());
            List<SetType> spinnerItems = List.of(SetType.values());
            ArrayAdapter<SetType> adapter = new ArrayAdapter<>(
                    holder.itemView.getContext(),
                    android.R.layout.simple_spinner_item,
                    spinnerItems
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            holder.spinnerContainer.addView(spinner);
        }
    }

    //Mapeo el Ejercicio -> EjercicioRutina
    private ExerciseRoutine mapExerciseToExerciseRoutine(Exercise exercise) {
        return new ExerciseRoutine(exercise, null, 0, 0L, 0,null, 1, null);
    }

    @Override
    public int getItemCount() {
        return exercises != null ? exercises.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        NumberPicker npSets;
        CheckBox cbSuper;
        Spinner spRest, spType;
        LinearLayout spinnerContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvExercise);
            spRest = itemView.findViewById(R.id.spRest);
            npSets = itemView.findViewById(R.id.npSets);
            cbSuper = itemView.findViewById(R.id.cbSuper);
            spinnerContainer = itemView.findViewById(R.id.spinnerContainer);
            spType = itemView.findViewById(R.id.spType);
        }
    }
}