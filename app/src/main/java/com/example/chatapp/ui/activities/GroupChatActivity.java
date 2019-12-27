package com.example.chatapp.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.chatapp.R;
import com.example.chatapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupChatActivity extends AppCompatActivity {

    @BindView(R.id.group_chat_bar_layout)
    Toolbar mToolbar;
    @BindView(R.id.send_group_chat_button)
    Button sendMsgButton;
    @BindView(R.id.group_chat_sv)
    NestedScrollView groupScrollView;
    @BindView(R.id.group_chat_input_layout)
    LinearLayout inputLayout;
    @BindView(R.id.group_message_input)
    EditText inputField;
    @BindView(R.id.group_chat_textView)
    TextView messageView;

    private FirebaseAuth mAuth;
    private DatabaseReference userReferance, groupNameRef, groupMsgKeyRef;
    private String groupName, userID, userName, currentDate, currentTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        ButterKnife.bind(this);

        groupName = getIntent().getStringExtra(Constants.GROUP_NAME);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        userReferance = FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupName);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(groupName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getUserInfo();

        initializeOnClicks();
    }

    @Override
    protected void onStart() {
        super.onStart();
        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    getMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    getMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getMessages(DataSnapshot dataSnapshot) {

        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext()){
            String msgDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String msgContent = (String) ((DataSnapshot)iterator.next()).getValue();
            String msgTime = (String) ((DataSnapshot)iterator.next()).getValue();
            String msgUsername = (String) ((DataSnapshot)iterator.next()).getValue();

            messageView.append(msgUsername + ":\n" + msgContent + "\n\n\n");
            groupScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }

    }


    private void getUserInfo() {

        userReferance.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    userName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initializeOnClicks() {

        sendMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsgToDB();
                inputField.setText("");
                groupScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        inputField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                groupScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }

    private void sendMsgToDB() {

        String msg = inputField.getText().toString();
        String msgKey = groupNameRef.push().getKey();

        if (TextUtils.isEmpty(msg)){
            return;
        }
        else {
            Calendar calendarDate = Calendar.getInstance();
            SimpleDateFormat formatDate = new SimpleDateFormat(Constants.DATE_MMM_DD_YYYY);
            currentDate = formatDate.format(calendarDate.getTime());

            Calendar calendarTime = Calendar.getInstance();
            SimpleDateFormat formatTime = new SimpleDateFormat(Constants.TIME_HH_MM);
            currentTime = formatTime.format(calendarTime.getTime());

            HashMap<String, Object> groupMsgKey = new HashMap<>();
            groupNameRef.updateChildren(groupMsgKey);
            groupMsgKeyRef = groupNameRef.child(msgKey);

            HashMap<String, Object> msgInfo = new HashMap<>();
            msgInfo.put(Constants.USERNAME, userName);
            msgInfo.put(Constants.MESSAGE, msg);
            msgInfo.put(Constants.DATE, currentDate);
            msgInfo.put(Constants.TIME, currentTime);

            groupMsgKeyRef.updateChildren(msgInfo);
        }

    }
}
