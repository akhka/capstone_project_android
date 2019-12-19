package com.example.chatapp.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.register_button)
    Button registerButton;
    @BindView(R.id.register_email)
    EditText registerEmailField;
    @BindView(R.id.register_password)
    EditText registerPasswordField;
    @BindView(R.id.go_login)
    TextView goToLogin_et;
    @BindView(R.id.register_corlay)
    CoordinatorLayout registerCorlay;
    @BindView(R.id.register_progressBar)
    ProgressBar progressBar;

    private Handler registerHandler;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);
        registerHandler = new Handler();
        mAuth = FirebaseAuth.getInstance();

        onClicksInitilize();


    }

    private void sendUserToLoginActivity() {

        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }


    private void onClicksInitilize(){
        goToLogin_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToLoginActivity();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });

    }

    private void createNewAccount(){
        String email = registerEmailField.getText().toString();
        String password = registerPasswordField.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            if (TextUtils.isEmpty(email)){
                registerEmailField.setError("Please enter email");
            }
            if (TextUtils.isEmpty(password)){
                registerPasswordField.setError("Please enter password");
            }
        }
        else {
            registerHandler.post(new Runnable() {
                @Override
                public void run() {
                    registerButton.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            progressBar.setVisibility(View.INVISIBLE);

                            if (task.isSuccessful()) {
                                messageShow("User registered successfully");
                                registerHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendUserToLoginActivity();
                                    }
                                }, 2000);
                            }
                            else {
                                messageShow("Error: " + task.getException().toString());
                                registerHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        registerButton.setEnabled(true);
                                    }
                                });
                            }

                        }
                    });
        }
    }


    private void messageShow(String message){
        final Snackbar snackbar = Snackbar.make(registerCorlay,
                message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}
