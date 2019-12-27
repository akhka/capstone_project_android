package com.example.chatapp.ui.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chatapp.R;
import com.example.chatapp.model.Contact;
import com.example.chatapp.ui.activities.ChatActivity;
import com.example.chatapp.utils.Constants;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private View chatsListView;
    private FirebaseAuth mAuth;
    private DatabaseReference chatsRefrence, usersReference;
    private String currentUID;

    @BindView(R.id.private_chats_list_rv)
    RecyclerView chatsList;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chatsListView = inflater.inflate(R.layout.fragment_chat, container, false);

        ButterKnife.bind(this, chatsListView);
        mAuth = FirebaseAuth.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();
        chatsRefrence = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUID);
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));
        return chatsListView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contact> options =
                new FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(chatsRefrence, Contact.class)
                .build();

        FirebaseRecyclerAdapter<Contact, ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contact, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ChatsViewHolder holder, int position, @NonNull Contact model) {
                        String userUId = getRef(position).getKey();
                        usersReference.child(userUId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String userName = dataSnapshot.child("name").getValue().toString();
                                String userStatus = dataSnapshot.child("status").getValue().toString();

                                holder.username.setText(userName);
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                        chatIntent.putExtra(Constants.SELECTED_PROFILE, userUId);
                                        chatIntent.putExtra(Constants.USERNAME, userName);
                                        startActivity(chatIntent);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout, parent, false);
                        ChatsViewHolder vh = new ChatsViewHolder(view);
                        return vh;
                    }
                };
        chatsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder{

        private TextView username, status;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.display_name);
            status = itemView.findViewById(R.id.display_status);
        }
    }
}
