package com.e_fit.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.e_fit.R;
import com.e_fit.api.UserClient;
import com.e_fit.enities.User;
import com.e_fit.util.MenuActivity;
import com.e_fit.util.SharedPrefs;

public class UserFormActivity extends MenuActivity {

    private EditText etName, etSurname, etEmail, etPassword;
    private TextView tvError;
    private Button btnUpdate, btnClearSession;
    private UserClient client;
    private Context context;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_form);
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
        this.client=new UserClient();
        context= this;
        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvError = findViewById(R.id.tvError);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnClearSession = findViewById(R.id.btnClearSession);
        // Deshabilitar los EditText
        etName.setEnabled(false);
        etSurname.setEnabled(false);
        etEmail.setEnabled(false);
        etPassword.setEnabled(false);
    }

    private void loadEvents() {
        checkError();
        btnUpdate.setOnClickListener(i->{update();});
        btnClearSession.setOnClickListener(i->{clearSession();});

    }

    private void onInit() {
        //Comprobar SharedPreferences
        String id=SharedPrefs.getString(this,"id","");
        client.getUser("", id, new UserClient.UserCallback() {
            @Override
            public void onUserReceived(User retrivedUser) {
                user = retrivedUser;
                etName.setText(user.getName());
                etSurname.setText(user.getSurname());
                etEmail.setText(user.getEmail());
                etPassword.setText(user.getPassword());
            }

            @Override
            public void onUserSent() {
            }

            @Override
            public void onError(Exception e) {
                tvError.setText(getString(R.string.connection_error));
            }
        });
    }

    public void update(){
        //Habilitar que el usuario para editar campos
        etName.setEnabled(true);
        etSurname.setEnabled(true);
        etEmail.setEnabled(true);
        etPassword.setEnabled(true);
        //Cambio el botón
        btnUpdate.setText(R.string._confirm);
        btnUpdate.setBackgroundColor(getResources().getColor(R.color.resalt));
        btnUpdate.setOnClickListener(i->{confirmUpdate();});
    }

    public void confirmUpdate(){
        // Actualizar el objeto user
        user.setName(etName.getText().toString().trim());
        user.setSurname(etSurname.getText().toString().trim());
        user.setEmail(etEmail.getText().toString().trim());
        user.setPassword(etPassword.getText().toString().trim());
        //Habilitar que el usuario para editar campos
        if(updateUser()) {
            etName.setEnabled(false);
            etSurname.setEnabled(false);
            etEmail.setEnabled(false);
            etPassword.setEnabled(false);
            //Cambio el botón
            btnUpdate.setText(R.string._update);
            btnUpdate.setBackgroundColor(getResources().getColor(R.color.black_2));
            btnUpdate.setOnClickListener(i -> {
                update();
            });
        }
    }

    public boolean updateUser(){
        //Validar campos
        if(user.getName().isEmpty())
            tvError.setText(getString(R.string.invalid_empty_username));
        else if(user.getSurname().isEmpty())
            tvError.setText(getString(R.string.invalid_empty_password));
        else if(user.getEmail().isEmpty())
            tvError.setText(getString(R.string.invalid_empty_username));
        else if(user.getPassword().isEmpty())
            tvError.setText(getString(R.string.invalid_empty_password));
        else {
            client.updateUser(user,new UserClient.UserCallback() {
                @Override
                public void onUserReceived(User retrivedUser) {}
                @Override
                public void onUserSent() {Toast.makeText(context,"Usuario modificado correctamente",Toast.LENGTH_SHORT).show();}
                @Override
                public void onError(Exception e) {
                    tvError.setText(getString(R.string.user_update_error));
                }
            });
            return true;
        }
        return false;
    }

    public void checkError() {
        if (tvError.getText().toString().trim().isEmpty())
            tvError.setVisibility(View.GONE);
        else
            tvError.setVisibility(View.VISIBLE);
    }

    private void clearSession(){
        SharedPrefs.clearAll(this);
        startActivity(new Intent(this, LoginActivity.class));
    }
}