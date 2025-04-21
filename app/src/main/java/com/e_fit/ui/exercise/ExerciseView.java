package com.e_fit.ui.exercise;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.e_fit.R;
import com.e_fit.enities.Exercise;
import com.e_fit.util.MenuActivity;

import java.io.InputStream;

public class ExerciseView extends MenuActivity {

    private Exercise exercise;
    private TextView tvName, tvMuscularGroup, tvDescription;
    private ImageView ivExercise;
    private Button btnReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercise_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadElements();
    }

    private void loadElements(){
        tvName = findViewById(R.id.tvName);
        tvMuscularGroup = findViewById(R.id.tvMuscularGroup);
        tvDescription = findViewById(R.id.tvDescription);
        ivExercise = findViewById(R.id.ivExercise);
        btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(i->{
            super.onBackPressed();
        });
        getExercise();
    }

    private void getExercise(){
        exercise = getIntent().getParcelableExtra("exercise");
        if (exercise != null) {
            // Mostrar los datos del ejercicio en la vista
            tvName.setText(exercise.getName());
            tvMuscularGroup.setText(exercise.getMuscularGroup().toString());
            tvDescription.setText(exercise.getDescription());
            //ImageView
            try {
                InputStream ims = this.getAssets().open(exercise.getName()+".png");
                ivExercise.setImageDrawable(Drawable.createFromStream(ims, null));
            } catch (Exception e) {
                e.printStackTrace();
                ivExercise.setVisibility(View.GONE);
            }
        }
    }
}