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
import com.example.chatapp.ui.activities.ProfileActivity;
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
public class ContactsFragment extends Fragment {

    private View contactsView;
    private FirebaseAuth mAuth;
    private DatabaseReference contactsReference, usersRefrence;
    private String currentUID;

    @BindView(R.id.contacts_list_rv)
    RecyclerView contactsListRv;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactsView = inflater.inflate(R.layout.fragment_contacts, container, false);
        ButterKnife.bind(this, contactsView);

        mAuth = FirebaseAuth.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();
        contactsReference = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUID);
        usersRefrence = FirebaseDatabase.getInstance().getReference().child("Users");
        contactsListRv.setLayoutManager(new LinearLayoutManager(getContext()));

        return contactsView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(contactsReference, Contact.class)
                .build();

        FirebaseRecyclerAdapter<Contact, ContactsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contact, ContactsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ContactsViewHolder holder, int position, @NonNull Contact model) {

                        String userID = getRef(position).getKey();
                        usersRefrence.child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String profileName = dataSnapshot.child("name").getValue().toString();
                                String profileStatus = dataSnapshot.child("status").getValue().toString();

                                holder.username.setText(profileName);
                                holder.status.setText(profileStatus);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout, parent, false);
                        ContactsViewHolder vh = new ContactsViewHolder(view);
                        return vh;
                    }
                };

        contactsListRv.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder{

        private TextView username, status;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.display_name);
            status = itemView.findViewById(R.id.display_status);
        }
    }
}
