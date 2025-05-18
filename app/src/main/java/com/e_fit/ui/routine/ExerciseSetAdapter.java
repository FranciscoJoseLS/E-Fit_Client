package com.e_fit.ui.routine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.e_fit.R;
import com.e_fit.enities.Exercise;
import com.e_fit.ui.exercise.ExerciseView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExerciseSetAdapter extends ArrayAdapter<Exercise> {
    private ArrayList<Exercise> exercises;
    private ArrayList<Exercise> selectedExercises;
    private Context context;

    public ExerciseSetAdapter(Context context, ArrayList<Exercise> exercises) {
        super(context, R.layout.exercise_item_view, exercises);
        this.exercises = exercises;
        this.context = context;
        this.selectedExercises = new ArrayList<>();
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.exercise_item_view, parent, false);
        }

        // Referencias a vistas
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvMuscularGroup = view.findViewById(R.id.tvMuscularGroup);
        ImageView imageView = view.findViewById(R.id.ivExercise);
        Button btnVer = view.findViewById(R.id.btnVer);
        LinearLayout container = view.findViewById(R.id.container);

        Exercise exercise = exercises.get(position);

        // Mostrar datos
        tvName.setText(exercise.getName());
        tvMuscularGroup.setText(exercise.getMuscularGroup().toString());

        // Cargar imagen
        String imageName = exercise.getName() + ".png";
        Drawable drawable = getDrawableFromAssets(view.getContext(), imageName);
        imageView.setImageDrawable(drawable);

        // Configurar selección visual
        if (selectedExercises.contains(exercise)) {
            container.setBackgroundColor(getContext().getResources().getColor(R.color.black_2));
        } else {
            container.setBackgroundColor(getContext().getResources().getColor(R.color.black_transparent));
        }

        // Acción al hacer clic en el botón "Ver"
        btnVer.setOnClickListener(it -> {
            Intent i = new Intent(getContext(), ExerciseView.class);
            i.putExtra("exercise", exercise);
            getContext().startActivity(i);
        });

        // Seleccionar/deseleccionar al hacer clic en el item completo
        container.setOnClickListener(v -> {
            toggleSelection(exercise);
            notifyDataSetChanged();
        });

        return view;
    }


    public void toggleSelection(Exercise exercise) {
        if (selectedExercises.contains(exercise)) {
            selectedExercises.remove(exercise);
        } else {
            selectedExercises.add(exercise);
        }
    }

    public List<Exercise> getSelectedExercises() {
        return new ArrayList<>(selectedExercises);
    }

    public void clearSelection() {
        selectedExercises.clear();
        notifyDataSetChanged();
    }

    public Drawable getDrawableFromAssets(Context context, String imageName) {
        try {
            InputStream ims = context.getAssets().open(imageName);
            return Drawable.createFromStream(ims, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void updateData(ArrayList<Exercise> newData) {
            this.exercises = newData;

            //Agrego los ejercicios seleccionados al final
            for(Exercise e : selectedExercises)
                if(!this.exercises.contains(e))
                    this.exercises.add(e);

            notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return exercises.size();
    }

    @Override
    public Exercise getItem(int position) {
        return exercises.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}