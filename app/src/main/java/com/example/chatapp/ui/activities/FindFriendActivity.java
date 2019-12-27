package com.example.chatapp.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chatapp.R;
import com.example.chatapp.model.Contact;
import com.example.chatapp.utils.Constants;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FindFriendActivity extends AppCompatActivity {

    @BindView(R.id.find_friends_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.find_friends_list_rv)
    RecyclerView friendsListView;

    private DatabaseReference usersReferance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        ButterKnife.bind(this);

        usersReferance = FirebaseDatabase.getInstance().getReference().child("Users");

        friendsListView.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friend");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contact> options = new FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(usersReferance, Contact.class)
                .build();

        FirebaseRecyclerAdapter<Contact, FindFriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contact, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, int position, @NonNull Contact model) {
                        holder.username.setText(model.getName());
                        holder.status.setText(model.getStatus());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String selected_user_id = getRef(position).getKey();
                                Intent profileIntent = new Intent(FindFriendActivity.this, ProfileActivity.class);
                                profileIntent.putExtra(Constants.SELECTED_PROFILE, selected_user_id);
                                startActivity(profileIntent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout, parent, false);
                        FindFriendViewHolder vh = new FindFriendViewHolder(view);
                        return vh;
                    }
                };

        friendsListView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder{

        private TextView username, status;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.display_name);
            status = itemView.findViewById(R.id.display_status);
        }
    }
}
