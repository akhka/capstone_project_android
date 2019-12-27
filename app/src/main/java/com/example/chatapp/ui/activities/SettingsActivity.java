package com.example.chatapp.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.settings_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.profile_image)
    CircleImageView userProgileImage;
    @BindView(R.id.profile_name_editText)
    EditText nameEditText;
    @BindView(R.id.profile_status_editText)
    EditText statuseEditText;
    @BindView(R.id.update_profile_button)
    Button updateButton;
    @BindView(R.id.settingsCorlay)
    CoordinatorLayout corlay;

    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference appReferance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        appReferance = FirebaseDatabase.getInstance().getReference();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Settings");

        initializeOnClicks();

        retrieveUserInfo();
    }

    private void retrieveUserInfo() {

        appReferance.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image"))){
                            String retrievedName = dataSnapshot.child("name").getValue().toString();
                            String retrievedStatus = dataSnapshot.child("status").getValue().toString();
                            String retrievedImage = dataSnapshot.child("image").getValue().toString();

                            nameEditText.setText(retrievedName);
                            statuseEditText.setText(retrievedStatus);

                        }
                        else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                            String retrievedName = dataSnapshot.child("name").getValue().toString();
                            String retrievedStatus = dataSnapshot.child("status").getValue().toString();

                            nameEditText.setText(retrievedName);
                            statuseEditText.setText(retrievedStatus);
                        }
                        else {
                            Toast.makeText(SettingsActivity.this, "Please update your profile information!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void initializeOnClicks(){
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });
    }

    private void updateUserInfo() {

        String name = nameEditText.getText().toString();
        String status = statuseEditText.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(status)){
            if (TextUtils.isEmpty(name)){
                nameEditText.setError("Please enter your name...");
            }
            if (TextUtils.isEmpty(status)){
                statuseEditText.setError("Please enter your name...");
            }
        }
        else{
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("name", name);
            profileMap.put("status", status);
            appReferance.child("Users").child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(SettingsActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                sendUserToHomeActivity();
                            }
                            else {
                                Snackbar snackbar = Snackbar.make(corlay, task.getException().getMessage(), Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }
                    });
        }

    }


    private void sendUserToHomeActivity() {

        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();

    }
}
