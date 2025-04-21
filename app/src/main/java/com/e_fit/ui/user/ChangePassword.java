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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.e_fit.R;
import com.e_fit.api.UserClient;
import com.e_fit.enities.User;
import com.e_fit.util.MenuActivity;

public class ChangePassword extends AppCompatActivity {

    private EditText etEmail, etPassword, etPasswordConfirm;
    private TextView tvError;
    private Button btnConfirm;
    private UserClient client;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
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
        context= this;
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        tvError = findViewById(R.id.tvError);
        btnConfirm = findViewById(R.id.btnUpdate);
        btnConfirm.setOnClickListener(i->{update();});
        checkError();
    }

    public void update(){
        //Comprobar contraseñas
        if(!etPassword.getText().toString().isEmpty())
            if(etPassword.getText().toString().equals(etPasswordConfirm.getText().toString()))
                if(!etEmail.getText().toString().isEmpty())
                    client.getUser("mail", etEmail.getText().toString(), new UserClient.UserCallback() {
                        @Override
                        public void onUserReceived(User user) {
                            user.setPassword(etPassword.getText().toString());
                            client.updateUser(user, new UserClient.UserCallback() {
                                @Override
                                public void onUserReceived(User user) {}

                                @Override
                                public void onUserSent() {startActivity(new Intent(context, LoginActivity.class));}

                                @Override
                                public void onError(Exception e) {
                                    tvError.setText(getString(R.string.connection_error));
                                }
                            });
                        }

                        @Override
                        public void onUserSent() {}

                        @Override
                        public void onError(Exception e) {
                            tvError.setText(getString(R.string.invalid_username));
                        }
                    });
                else
                    tvError.setText(getString(R.string.invalid_empty_username));
            else
                tvError.setText(getString(R.string.fail_repeat_pasword));
        else
            tvError.setText(getString(R.string.invalid_empty_password));
        checkError();
    }

    public void checkError() {
        if (tvError.getText().toString().trim().isEmpty())
            tvError.setVisibility(View.GONE);
        else
            tvError.setVisibility(View.VISIBLE);
    }
}