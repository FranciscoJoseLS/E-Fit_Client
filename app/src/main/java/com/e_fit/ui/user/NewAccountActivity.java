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
import com.e_fit.util.MenuActivity;

public class NewAccountActivity extends AppCompatActivity {

    private EditText etName, etSurname, etEmail, etPassword;
    private TextView tvError;
    private Button btnCreate;
    private UserClient client;
    private Context context;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_account);
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
        this.user = new User();
        context= this;
        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvError = findViewById(R.id.tvError);
        btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(i->{createAccount();});
        checkError();
    }

    public void createAccount(){
        //Agragar al usuario
        user.setName(etName.getText().toString().trim());
        user.setSurname(etSurname.getText().toString().trim());
        user.setEmail(etEmail.getText().toString().trim());
        user.setPassword(etPassword.getText().toString().trim());
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
            client.createUser(user,new UserClient.UserCallback() {
                @Override
                public void onUserReceived(User retrivedUser) {}
                @Override
                public void onUserSent() {
                    startActivity(new Intent(context,LoginActivity.class));
                }
                @Override
                public void onError(Exception e) {
                    tvError.setText(getString(R.string.user_update_error));
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