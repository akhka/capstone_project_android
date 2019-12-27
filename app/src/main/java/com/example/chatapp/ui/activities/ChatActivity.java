package com.example.chatapp.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.R;
import com.example.chatapp.model.Message;
import com.example.chatapp.ui.adapters.PrivateMessageAdapter;
import com.example.chatapp.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference DBReference;

    private String receiverUID, senderUID, contactName;

    private List<Message> messageList = new ArrayList<>();
    private PrivateMessageAdapter messagesAdapter;

    private TextView chat_title;
    private Toolbar mToolbar;

    @BindView(R.id.send_private_chat_button)
    Button sendMessage;
    @BindView(R.id.private_message_input)
    EditText messageInput;
    @BindView(R.id.private_messages_list_rv)
    RecyclerView messagesLister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        senderUID = mAuth.getCurrentUser().getUid();
        DBReference = FirebaseDatabase.getInstance().getReference();

        receiverUID = getIntent().getStringExtra(Constants.SELECTED_PROFILE);
        contactName = getIntent().getStringExtra(Constants.USERNAME);

        setToolbar();

        messagesAdapter = new PrivateMessageAdapter(messageList);
        messagesLister.setLayoutManager(new LinearLayoutManager(this));
        messagesLister.setAdapter(messagesAdapter);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTheMessage();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        DBReference.child("Messages").child(senderUID).child(receiverUID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Message message = dataSnapshot.getValue(Message.class);
                        messageList.add(message);
                        messagesAdapter.notifyDataSetChanged();

                        messagesLister.smoothScrollToPosition(messagesLister.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

    private void sendTheMessage() {
        String message = messageInput.getText().toString();
        if (!TextUtils.isEmpty(message)){
            String messageSenderRef = "Messages/" + senderUID + "/" + receiverUID;
            String messageReceiverRef = "Messages/" + receiverUID + "/" + senderUID;

            DatabaseReference usrMsgKeyRef = DBReference.child("Messages")
                    .child(senderUID).child(receiverUID).push();

            String msgPushID = usrMsgKeyRef.getKey();

            Map msgTxtBody = new HashMap();
            msgTxtBody.put(Constants.MESSAGE, message);
            msgTxtBody.put("type", "text");
            msgTxtBody.put("from", senderUID);

            Map msgBodyDetail = new HashMap();
            msgBodyDetail.put(messageSenderRef + "/" + msgPushID, msgTxtBody);
            msgBodyDetail.put(messageReceiverRef + "/" + msgPushID, msgTxtBody);

            DBReference.updateChildren(msgBodyDetail).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){

                    }
                    else {
                        Toast.makeText(ChatActivity.this, "Error while sending!", Toast.LENGTH_SHORT).show();
                    }

                    messageInput.setText("");
                }
            });
        }
    }

    private void setToolbar() {
        mToolbar = findViewById(R.id.chat_toolbar);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.chat_bar, null);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarView);

        chat_title = findViewById(R.id.chat_contact_name);
        chat_title.setText(contactName);
    }
}
