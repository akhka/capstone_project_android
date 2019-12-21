package com.example.chatapp.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.chatapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupChatActivity extends AppCompatActivity {

    @BindView(R.id.group_chat_bar_layout)
    Toolbar mToolbar;
    @BindView(R.id.send_group_chat_button)
    Button sendMsgButton;
    @BindView(R.id.group_chat_sv)
    ScrollView groupScrollView;
    @BindView(R.id.group_chat_input_layout)
    LinearLayout inputLayout;
    @BindView(R.id.group_message_input)
    EditText inputField;
    @BindView(R.id.group_chat_textView)
    TextView messageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Group Name");
    }
}
