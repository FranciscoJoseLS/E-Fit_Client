package com.e_fit.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.e_fit.R;
import com.e_fit.api.UserClient;
import com.e_fit.enities.User;
import com.e_fit.ui.exercise.ExerciseList;
import com.e_fit.ui.routine.RoutineList;
import com.e_fit.util.SharedPrefs;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private TextView tvError, tvForgotPass, tvNewAccount;
    private Button btnLogin;
    private UserClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        loadElements();
    }

    private void loadElements() {
        this.client=new UserClient();
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvError = findViewById(R.id.tvError);
        tvError.setVisibility(View.GONE);
        tvForgotPass = findViewById(R.id.tvForgotPass);
        tvForgotPass.setOnClickListener(
                i->{startActivity(new Intent(this, ChangePassword.class));});
        tvNewAccount = findViewById(R.id.tvNewAccount);
        tvNewAccount.setOnClickListener(
                i->{startActivity(new Intent(this, NewAccountActivity.class));});
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(i->{login();});

    }

    public void login(){
        btnLogin.setVisibility(View.GONE);
        String mail=etEmail.getText().toString();
        String password=etPassword.getText().toString();
        Context c= this;

        if(mail.isEmpty()){
            btnLogin.setVisibility(View.VISIBLE);
            tvError.setText(getString(R.string.invalid_empty_username));
        } else if(password.isEmpty()){
            btnLogin.setVisibility(View.VISIBLE);
            tvError.setText(getString(R.string.invalid_empty_password));
        } else {
            client.getUser("mail", mail, new UserClient.UserCallback() {
                @Override
                public void onUserReceived(User user) {
                    if (password.equalsIgnoreCase(user.getPassword())) {
                        // Almacenar el id en shared preference
                        SharedPrefs.saveString(c, "id", String.valueOf(user.getUserId()));
                        //  Ir al listado de ejercicios
                        startActivity(new Intent(c, RoutineList.class));
                    } else {
                        btnLogin.setVisibility(View.VISIBLE);
                        tvError.setText(getString(R.string.invalid_password));
                    }
                }

                @Override
                public void onUserSent() {}

                @Override
                public void onError(Exception e) {
                    btnLogin.setVisibility(View.VISIBLE);
                    tvError.setText(getString(R.string.invalid_username));
                }
            });

        }

        checkError();
    }

    public void checkError() {
        if (tvError.getText().toString().trim().isEmpty())
            tvError.setVisibility(View.GONE);
        else
            tvError.setVisibility(View.VISIBLE);
    }

}