package com.e_fit.ui.routine;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e_fit.R;
import com.e_fit.enities.ExerciseRoutine;
import com.e_fit.ui.exercise.ExerciseView;

import java.util.List;

public class ExerciseRoutineAdapter extends RecyclerView.Adapter<ExerciseRoutineAdapter.ViewHolder> {

    private final Context context;
    private List<ExerciseRoutine> exerciseList;

    public ExerciseRoutineAdapter(Context context, List<ExerciseRoutine> exerciseList) {
        this.context = context;
        this.exerciseList = exerciseList;
    }

    // New method to update the exercise list
    public void setExerciseRoutines(List<ExerciseRoutine> exerciseRoutines) {
        this.exerciseList = exerciseRoutines;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvType, tvSerie, tvRest;
        Button btnView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvType = itemView.findViewById(R.id.tvType);
            tvSerie = itemView.findViewById(R.id.tvSerie);
            tvRest = itemView.findViewById(R.id.tvRest);
            btnView = itemView.findViewById(R.id.btnViewExercise);
        }
    }

    @NonNull
    @Override
    public ExerciseRoutineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.exercise_routine_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseRoutineAdapter.ViewHolder holder, int position) {
        ExerciseRoutine exercise = exerciseList.get(position);
        holder.tvName.setText(exercise.getExercise().getName());
        holder.tvType.setText(exercise.getExerciseType().toString());
        holder.tvSerie.setText("Nº sets: " + exercise.getnSets());
        holder.tvRest.setText("Descanso: " + exercise.getParsedRest());

        holder.btnView.setOnClickListener(it -> {
            Intent i = new Intent(context, ExerciseView.class);
            i.putExtra("exercise", exercise.getExercise());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }
}
