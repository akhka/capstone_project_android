package com.example.chatapp.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chatapp.R;
import com.example.chatapp.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUID, senderUID, currentState;
    private DatabaseReference userReference, chatRequestRef, contactReference;
    private FirebaseAuth mAuth;

    @BindView(R.id.selected_profile_name)
    TextView profileName;
    @BindView(R.id.selected_profile_status)
    TextView profileStatus;
    @BindView(R.id.send_message)
    Button sendMessage;
    @BindView(R.id.decline_message_request)
    Button declineMsgReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat_Requests");
        contactReference = FirebaseDatabase.getInstance().getReference().child("Contacts");
        receiverUID = getIntent().getStringExtra(Constants.SELECTED_PROFILE);
        senderUID = mAuth.getCurrentUser().getUid();
        currentState = "new";

        getUserInfo();
    }

    private void getUserInfo() {
        userReference.child(receiverUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();

                    profileName.setText(userName);
                    profileStatus.setText(userStatus);

                    handleChatRequest();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void handleChatRequest() {
        chatRequestRef.child(senderUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(receiverUID)){
                    String request_status = dataSnapshot.child(receiverUID).child("request_status").getValue().toString();
                    if(request_status.equals(Constants.SENT)){
                        currentState = Constants.REQUEST_SENT;
                        sendMessage.setText(Constants.CANCEL_CHAT_REQUEST);
                    } else if (request_status.equals(Constants.RECEIVED)) {
                        currentState = Constants.REQUEST_RECEIVED;
                        sendMessage.setText(Constants.ACCEPT_CHAT_REQUEST);
                        declineMsgReq.setVisibility(View.VISIBLE);
                        declineMsgReq.setEnabled(true);
                        declineMsgReq.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelChatRequest();
                            }
                        });
                    }
                }
                else {
                    contactReference.child(senderUID)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(receiverUID)){
                                        currentState = Constants.FRIENDS;
                                        sendMessage.setText(Constants.DELETE_CONTACT);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (!receiverUID.equals(senderUID)){
            sendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage.setEnabled(false);

                    if (currentState.equals(Constants.NEW)){
                        requestChat();
                    }
                    if (currentState.equals(Constants.REQUEST_SENT)){
                        cancelChatRequest();
                    }
                    if (currentState.equals(Constants.REQUEST_RECEIVED)){
                        acceptChatRequest();
                    }
                    if (currentState.equals(Constants.FRIENDS)){
                        deleteContact();
                    }
                }
            });
        }
        else {
            sendMessage.setVisibility(View.GONE);
        }
    }

    private void deleteContact() {
        contactReference.child(senderUID).child(receiverUID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            contactReference.child(receiverUID).child(senderUID).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                sendMessage.setEnabled(true);
                                                currentState = Constants.NEW;
                                                sendMessage.setText(Constants.SEND_CHAT_REQUEST);
                                                declineMsgReq.setVisibility(View.INVISIBLE);
                                                declineMsgReq.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void acceptChatRequest() {
        contactReference.child(senderUID).child(receiverUID)
                .child("Contacts").setValue(Constants.SAVED)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            contactReference.child(receiverUID).child(senderUID)
                                    .child("Contacts").setValue(Constants.SAVED)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                chatRequestRef.child(senderUID).child(receiverUID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    chatRequestRef.child(receiverUID).child(senderUID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()){
                                                                                        sendMessage.setEnabled(true);
                                                                                        currentState = Constants.FRIENDS;
                                                                                        sendMessage.setText(Constants.DELETE_CONTACT);
                                                                                        declineMsgReq.setVisibility(View.INVISIBLE);
                                                                                        declineMsgReq.setEnabled(false);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void cancelChatRequest() {
        chatRequestRef.child(senderUID).child(receiverUID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            chatRequestRef.child(receiverUID).child(senderUID).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                sendMessage.setEnabled(true);
                                                currentState = Constants.NEW;
                                                sendMessage.setText(Constants.SEND_CHAT_REQUEST);
                                                declineMsgReq.setVisibility(View.INVISIBLE);
                                                declineMsgReq.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void requestChat() {
        chatRequestRef.child(senderUID).child(receiverUID)
                .child("request_status").setValue(Constants.SENT)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            chatRequestRef.child(receiverUID).child(senderUID)
                                    .child("request_status").setValue(Constants.RECEIVED)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                sendMessage.setEnabled(true);
                                                currentState = Constants.REQUEST_SENT;
                                                sendMessage.setText(Constants.CANCEL_CHAT_REQUEST);

                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
