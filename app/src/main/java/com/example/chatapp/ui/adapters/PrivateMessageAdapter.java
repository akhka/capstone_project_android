package com.example.chatapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PrivateMessageAdapter extends RecyclerView.Adapter<PrivateMessageAdapter.ViewHolder> {

    private FirebaseAuth mAuth;
    private DatabaseReference usersReference;
    private List<Message> messages;

    public PrivateMessageAdapter(List<Message> messages){
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.private_message_layout, parent, false);
        mAuth = FirebaseAuth.getInstance();
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String sendrID = mAuth.getCurrentUser().getUid();
        Message message = messages.get(position);

        String fromUID = message.getFrom();
        String fromMessageType = message.getType();

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUID);
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (fromMessageType.equals("text")){
            holder.profileImage.setVisibility(View.INVISIBLE);
            holder.receiverBubble.setVisibility(View.INVISIBLE);
            holder.senderBubble.setVisibility(View.INVISIBLE);

            if (fromUID.equals(sendrID)){
                holder.senderBubble.setVisibility(View.VISIBLE);
                holder.senderBubble.setText(message.getMessage());
            }
            else{
                holder.profileImage.setVisibility(View.VISIBLE);
                holder.receiverBubble.setVisibility(View.VISIBLE);
                holder.receiverBubble.setText(message.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profileImage;
        private TextView receiverBubble, senderBubble;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.receiver_profile_pic);
            receiverBubble = itemView.findViewById(R.id.receiver_message_text);
            senderBubble = itemView.findViewById(R.id.sender_message_text);
        }
    }
}
