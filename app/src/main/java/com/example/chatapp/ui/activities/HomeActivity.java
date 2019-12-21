package com.example.chatapp.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatapp.R;
import com.example.chatapp.ui.adapters.HomeTabsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.home_toolbar)
    Toolbar homeToolbar;
    @BindView(R.id.home_tabs_pager)
    ViewPager homePager;
    @BindView(R.id.home_tabs)
    TabLayout homeTabLayout;
    @BindView(R.id.home_corlay)
    CoordinatorLayout corlay;

    private HomeTabsAdapter tabsAdapter;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference appReferance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        appReferance = FirebaseDatabase.getInstance().getReference();

        setSupportActionBar(homeToolbar);
        getSupportActionBar().setTitle("Chat App");

        tabsAdapter = new HomeTabsAdapter(getSupportFragmentManager());
        homePager.setAdapter(tabsAdapter);

        homeTabLayout.setupWithViewPager(homePager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null){
            sendUserToLoginActivity();
        }
        else {
            verifyUser();
        }
    }

    private void verifyUser() {
        String currentUserID = mAuth.getCurrentUser().getUid();
        appReferance.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child("name").exists())){

                }
                else {
                    forceUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToLoginActivity() {

        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }

    private void sendUserToSettingsActivity() {

        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private void forceUserToSettingsActivity() {

        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.home_find_people_option){

        }

        if (item.getItemId() == R.id.home_create_group_option){
            requestNewGroup();
        }

        if (item.getItemId() == R.id.home_settings_option){
            sendUserToSettingsActivity();
        }

        if (item.getItemId() == R.id.home_logout_option){
            mAuth.signOut();
            sendUserToLoginActivity();
        }

        return true;
    }

    private void requestNewGroup() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);
        builder.setTitle("Enter New Group Name:");
        final EditText groupNameField = new EditText(this);
        groupNameField.setHint("Group name here...");
        builder.setView(groupNameField);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(groupName)){
                    Toast.makeText(HomeActivity.this, "Please enter grop name", Toast.LENGTH_SHORT).show();
                }
                else {
                    createNewGroup(groupName);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void createNewGroup(String groupName) {

        appReferance.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(HomeActivity.this, groupName + " group is created successfully", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(HomeActivity.this, "Error: " + task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}
