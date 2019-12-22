package com.example.chatapp.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.ui.activities.GroupChatActivity;
import com.example.chatapp.utils.Constants;

import java.util.List;

public class GroupsListAdapter extends RecyclerView.Adapter<GroupsListAdapter.ViewHolder> {

    private Context context;
    private List<String> groups;

    public GroupsListAdapter(Context context, List<String> groups){
        this.context = context;
        this.groups = groups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_list_element_layout, parent, false);
        ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentGroupName = groups.get(vh.getAdapterPosition());
                Intent groupIntent = new Intent(context, GroupChatActivity.class);
                groupIntent.putExtra(Constants.GROUP_NAME, currentGroupName);
                context.startActivity(groupIntent);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.groupNameTv.setText(groups.get(position));
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView groupNameTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameTv = itemView.findViewById(R.id.group_name_tv);
        }
    }
}
