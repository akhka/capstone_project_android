package com.example.chatapp.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.example.chatapp.R;
import com.example.chatapp.ui.adapters.HomeTabsAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.home_toolbar)
    Toolbar homeToolbar;
    @BindView(R.id.home_tabs_pager)
    ViewPager homePager;
    @BindView(R.id.home_tabs)
    TabLayout homeTabLayout;

    private HomeTabsAdapter tabsAdapter;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

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
    }

    private void sendUserToLoginActivity() {

        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }
}
