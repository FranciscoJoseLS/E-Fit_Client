package com.e_fit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.e_fit.ui.routine.RoutineList;
import com.e_fit.ui.training.RoutineSelector;
import com.e_fit.ui.user.LoginActivity;
import com.e_fit.util.MenuActivity;
import com.e_fit.util.SharedPrefs;

public class MainActivity extends MenuActivity {

    private EditText et;
    private TextView tv;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadElements();
        loadEvents();
        onInit();
    }

    private void loadElements() {
        et = findViewById(R.id.et);
        btn = findViewById(R.id.btn);
    }

    private void loadEvents() {
        btn.setOnClickListener(i -> {
            SharedPrefs.clearAll(this);
            startActivity(new Intent(this, LoginActivity.class));
        });
    }

    private void onInit() {
        //Según el valor del ID se carga una pantalla diferente
        if(SharedPrefs.getString(this,"id","").isEmpty())
            startActivity(new Intent(this, LoginActivity.class));
        else
            startActivity(new Intent(this, RoutineSelector.class));
    }
}