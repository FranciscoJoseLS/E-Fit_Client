package com.e_fit.ui.training;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e_fit.R;
import com.e_fit.api.ScoreClient;
import com.e_fit.enities.ExerciseRoutine;
import com.e_fit.enities.ExerciseType;
import com.e_fit.enities.Score;
import com.e_fit.util.SharedPrefs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.ViewHolder> implements OnSetCheckedListener {

    private final Context context;
    private List<ExerciseRoutine> exerciseList;
    private OnSetCheckedListener setCheckedListener;

    private final Map<Integer, CountDownTimer> activeTimers = new HashMap<>();
    private final Map<Integer, Long> currentRemainingMillisMap = new HashMap<>();
    private RecyclerView recyclerView;

    public TrainingAdapter(Context context, List<ExerciseRoutine> exerciseList, OnSetCheckedListener listener) {
        this.context = context;
        this.exerciseList = exerciseList;
        this.setCheckedListener = listener;
    }

    public void setExerciseRoutines(List<ExerciseRoutine> exerciseRoutines) {
        this.exerciseList = exerciseRoutines;
        clearAllTimers();
        notifyDataSetChanged();
    }

    private void clearAllTimers() {
        for (CountDownTimer timer : activeTimers.values())
            if (timer != null)timer.cancel();
        activeTimers.clear();
        currentRemainingMillisMap.clear();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        clearAllTimers();
        this.recyclerView = null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        EditText tvComments;
        TextView tvCronometer, tvScore;
        RecyclerView lvSets;
        ProgressBar pbCronometer;
        LinearLayout llCronometer;
        CardView card;

        @SuppressLint("ResourceAsColor")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvComments = itemView.findViewById(R.id.tvComments);
            lvSets = itemView.findViewById(R.id.lvSets);
            pbCronometer = itemView.findViewById(R.id.pbCronometer);
            llCronometer = itemView.findViewById(R.id.llCronometer);
            tvCronometer = itemView.findViewById(R.id.tvCronometer);
            tvScore = itemView.findViewById(R.id.tvScores);
            card = itemView.findViewById(R.id.card);
        }
    }

    @NonNull
    @Override
    public TrainingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.training_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ExerciseRoutine exercise = exerciseList.get(position);
        if (exercise.getExerciseType() != ExerciseType.LIBRE)
            holder.tvName.setText(String.format("%s en %s", exercise.getExercise().getName(), exercise.getExerciseType().toString()));
        else
            holder.tvName.setText(exercise.getExercise().getName());

        if(exerciseList.get(position).getSuperSerie()==1)
            holder.card.setBackgroundColor(ContextCompat.getColor(context, R.color.black_0));
        long restSeconds = exercise.getRest() != null ? exercise.getRest() : 0L;
        holder.pbCronometer.setMax((int) restSeconds);

        if (activeTimers.containsKey(position)) {
            //Mapeo a milisegundos
            holder.llCronometer.setVisibility(View.VISIBLE);
            long remainingMillis = currentRemainingMillisMap.getOrDefault(position, 0L);
            holder.tvCronometer.setText(getStringRest(remainingMillis / 1000L));
            holder.pbCronometer.setProgress((int) (remainingMillis / 1000));
        } else {
            holder.llCronometer.setVisibility(View.GONE);
            holder.tvCronometer.setText(getStringRest(restSeconds));
            holder.pbCronometer.setProgress((int) restSeconds);
        }

        holder.tvScore.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showScoresDialog(position);
                return true;
            }
        });
        holder.lvSets.setLayoutManager(new LinearLayoutManager(context));
        holder.lvSets.setAdapter(new SetAdapter(context, exercise.getSetTypes(), TrainingAdapter.this, position));
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    @Override
    public void onSetChecked(int exercisePosition, int setPosition, boolean isChecked) {
        // Notificar al listener externo si existe
        if (this.setCheckedListener != null && this.setCheckedListener != this) {
            this.setCheckedListener.onSetChecked(exercisePosition, setPosition, isChecked);
        }

        if (recyclerView == null || exercisePosition < 0 || exercisePosition >= exerciseList.size()) {
            return;
        }

        ExerciseRoutine exercise = exerciseList.get(exercisePosition);
        final long totalRestSeconds = exercise.getRest() != null ? exercise.getRest() : 0L;
        final long totalRestMillis = totalRestSeconds * 1000;

        // Cancela cualquier cronómetro existente para este ítem
        if (activeTimers.containsKey(exercisePosition)) {
            CountDownTimer existingTimer = activeTimers.get(exercisePosition);
            if (existingTimer != null) {
                existingTimer.cancel();
            }
            activeTimers.remove(exercisePosition);
            currentRemainingMillisMap.remove(exercisePosition);
        }

        ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(exercisePosition);

        // CheckBox marcado, iniciar cronómetro
        if (isChecked) {
            if (totalRestMillis <= 0) { // No iniciar si no hay tiempo de descanso
                if (holder != null) holder.llCronometer.setVisibility(View.GONE);
                return;
            }

            if (holder != null) {
                holder.llCronometer.setVisibility(View.VISIBLE);
                holder.pbCronometer.setMax((int) totalRestSeconds);
                holder.pbCronometer.setProgress((int) totalRestSeconds);
                holder.tvCronometer.setText(getStringRest(totalRestSeconds));
            }

            currentRemainingMillisMap.put(exercisePosition, totalRestMillis);

            CountDownTimer timer = new CountDownTimer(totalRestMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    currentRemainingMillisMap.put(exercisePosition, millisUntilFinished);
                    ViewHolder currentHolder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(exercisePosition);
                    if (currentHolder != null) {
                        currentHolder.tvCronometer.setText(getStringRest(millisUntilFinished / 1000L));
                        currentHolder.pbCronometer.setProgress((int) (millisUntilFinished / 1000));
                        if (currentHolder.llCronometer.getVisibility() == View.GONE)
                            currentHolder.llCronometer.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFinish() {
                    activeTimers.remove(exercisePosition);
                    currentRemainingMillisMap.remove(exercisePosition);
                    ViewHolder currentHolder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(exercisePosition);
                    if (currentHolder != null) {
                        currentHolder.llCronometer.setVisibility(View.GONE);;
                        currentHolder.tvCronometer.setText(getStringRest(totalRestSeconds));
                        currentHolder.pbCronometer.setProgress((int) totalRestSeconds);
                    }
                }
            };
            activeTimers.put(exercisePosition, timer);
            timer.start();

        } else if (holder != null) {
            holder.llCronometer.setVisibility(View.GONE);
            holder.tvCronometer.setText(getStringRest(totalRestSeconds));
            holder.pbCronometer.setProgress((int) totalRestSeconds);
        }
    }

    public List<ExerciseRoutine> getExerciseList() {
        return exerciseList;
    }

    private String getStringRest(Long rest){
        if (rest == null || rest < 0) return "00:00";

        long minutes = rest / 60;
        long seconds = rest % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void showScoresDialog(int exercisePosition) {
        ExerciseRoutine currentExercise = exerciseList.get(exercisePosition);
        // Inflo la vista personalizada
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams")
        View customDialogView = inflater.inflate(R.layout.dialog_score_view, null);
        TextView tvDialogTitle = customDialogView.findViewById(R.id.dialogTitle);
        tvDialogTitle.setText(currentExercise.getExercise().getName());
        TextView tvEmpty = customDialogView.findViewById(R.id.tvEmpty);
        tvEmpty.setVisibility(View.GONE);
        ListView lvScores = customDialogView.findViewById(R.id.lvScores);

        //Recojo otras marcas de este ejercicio
        ScoreClient client = new ScoreClient();
        client.getScore(SharedPrefs.getString(context, "id", ""), currentExercise.getExercise().getExerciseId(), new ScoreClient.GetScoreCallback() {
            @Override
            public void onScorePosted(List<Score> scores) {
                if(scores==null||scores.isEmpty())
                    tvEmpty.setVisibility(View.VISIBLE);
                else {
                    //Crear adaptador y pasar a lvScores
                    lvScores.setAdapter(new ScoreAdapter(context, scores));
                }
            }

            @Override
            public void onError(Exception e) {
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });



        new AlertDialog.Builder(context)
                .setView(customDialogView)
                .show();
    }

}