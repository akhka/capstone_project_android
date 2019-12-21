package com.example.chatapp.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatapp.R;
import com.example.chatapp.utils.Constants;
import com.example.chatapp.utils.ResultTeller;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    //private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private Handler loginHandler;
    private RequestQueue queue;
    private ResultTeller responseResult;

    @BindView(R.id.login_button)
    Button loginButton;
    @BindView(R.id.phone_login_button)
    Button phoneLoginButton;
    @BindView(R.id.login_email)
    EditText emailLoginField;
    @BindView(R.id.login_password)
    EditText passwordLoginField;
    @BindView(R.id.forgot_password)
    TextView forgotPassword;
    @BindView(R.id.go_register_new_account)
    TextView goCreateAccount_et;
    @BindView(R.id.login_progressBar)
    ProgressBar progressBar;
    @BindView(R.id.logCorlay)
    CoordinatorLayout logCorLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        //currentUser = mAuth.getCurrentUser();
        loginHandler = new Handler();
        queue = Volley.newRequestQueue(getApplicationContext());

        initializeOnClicks();
    }

    /*@Override
    protected void onStart() {
        super.onStart();

        if (currentUser != null){
            sendUserToHomeActivity();
        }
    }*/


    private void initializeOnClicks(){

        goCreateAccount_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToRegisterActivity();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

    }

    private void sendUserToHomeActivity() {

        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();

    }


    private void sendUserToRegisterActivity() {

        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
        finish();

    }


    private void loginUser(){
        String email = emailLoginField.getText().toString();
        String password = passwordLoginField.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            if (TextUtils.isEmpty(email)){
                emailLoginField.setError("Please enter email");
            }
            if (TextUtils.isEmpty(password)){
                passwordLoginField.setError("Please enter password");
            }
        }
        else{
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setOnClickListener(null);
            phoneLoginButton.setOnClickListener(null);

            recaptchaCheck(email, password);


        }
    }

    private void recaptchaCheck(final String email, final String password){
        SafetyNet.getClient(LoginActivity.this).verifyWithRecaptcha(Constants.RECAPTCHA_SITE_KEY)
                .addOnSuccessListener(LoginActivity.this,
                        new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                            @Override
                            public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                                // Indicates communication with reCAPTCHA service was
                                // successful.
                                String userResponseToken = response.getTokenResult();
                                Log.e("response", userResponseToken);
                                if (!userResponseToken.isEmpty()) {
                                    // Validate the user response token using the
                                    // reCAPTCHA siteverify API.
                                    handleCaptchaResult(userResponseToken, email, password);
                                }
                            }
                        })
                .addOnFailureListener(LoginActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            // An error occurred when communicating with the
                            // reCAPTCHA service. Refer to the status code to
                            // handle the error appropriately.
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            Log.e(LoginActivity.class.getName(), "Error: " + CommonStatusCodes
                                    .getStatusCodeString(statusCode));
                            responseResult = new ResultTeller(false, e.getMessage());
                            finalResult(email, password);
                        } else {
                            // A different, unknown type of error occurred.
                            Log.e(LoginActivity.class.getName(), "Error: " + e.getMessage());
                            responseResult = new ResultTeller(false, e.getMessage());
                            finalResult(email, password);
                        }
                    }
                });
    }


    // To handle ReCaptcha validation
    private void handleCaptchaResult(final String responseToken, final String email, final String password) {
        String url = Constants.RECAPTCHA_VALIDATION_URL;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getBoolean("success")){
                                Toast.makeText(LoginActivity.this, "Humanity Verified", Toast.LENGTH_SHORT).show();
                                responseResult = new ResultTeller(true, "Humanity Verified");
                                finalResult(email, password);
                            }
                        } catch (Exception ex) {
                            Log.d(LoginActivity.class.getName(), "Error message: " + ex.getMessage());
                            responseResult = new ResultTeller(false, ex.getMessage());
                            finalResult(email, password);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LoginActivity.class.getName(), "Error message: " + error.getMessage());
                        responseResult = new ResultTeller(false, error.getMessage());
                        finalResult(email, password);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret", Constants.RECAPTCHA_SECRET_KEY);
                params.put("response", responseToken);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }


    private void finalResult(String email, String password){
        if (responseResult.isSuccess()){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.INVISIBLE);
                                sendUserToHomeActivity();
                                Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                progressBar.setVisibility(View.INVISIBLE);
                                initializeOnClicks();
                                final Snackbar snackbar = Snackbar.make(logCorLay, "Error: " + task.getException(), Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        snackbar.dismiss();
                                    }
                                });
                                snackbar.show();
                            }
                        }
                    });
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
            initializeOnClicks();
            Snackbar snackbar = Snackbar.make(logCorLay, responseResult.getMessage(), Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }
    }
}
