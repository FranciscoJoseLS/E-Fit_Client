package com.e_fit.util;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.e_fit.MainActivity;
import com.e_fit.R;
import com.e_fit.ui.exercise.ExerciseList;
import com.e_fit.ui.routine.RoutineList;
import com.e_fit.ui.training.RoutineSelector;
import com.e_fit.ui.user.UserFormActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        //super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId()==R.id.itemMenuHome){
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }else if(item.getItemId()==R.id.itemUser){
            startActivity(new Intent(this, UserFormActivity.class));
            return true;
        }else if(item.getItemId()==R.id.itemExercise){
            startActivity(new Intent(this, ExerciseList.class));
            return true;
        }else if(item.getItemId()==R.id.itemRoutine){
            startActivity(new Intent(this, RoutineList.class));
            return true;
        }else if(item.getItemId()==R.id.itemTraining){
            startActivity(new Intent(this, RoutineSelector.class));
            return true;
        }else
            return false;
    }

}