package com.e_fit;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.e_fit.ui.training.RoutineSelector;
import com.e_fit.ui.user.LoginActivity;
import com.e_fit.util.SharedPrefs;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onInit();
        finish();
    }
    private void onInit() {
        //Según el valor del ID se carga una pantalla diferente
        if(SharedPrefs.getString(this,"id","").isEmpty())
            startActivity(new Intent(this, LoginActivity.class));
        else
            startActivity(new Intent(this, RoutineSelector.class));
    }
}