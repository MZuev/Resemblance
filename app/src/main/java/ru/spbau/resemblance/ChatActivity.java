package ru.spbau.resemblance;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ChatActivity extends AppCompatActivity {
    private EditText messageField;
    private String ourName;
    private ListView messagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messageField = (EditText) findViewById(R.id.chatNewMessageField);
        messagesList = (ListView) findViewById(R.id.chatMessagesList);


        SharedPreferences preferences = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);
        ourName = preferences.getString(SettingsActivity.NICKNAME_PREF, "");
        setTitle(R.string.chat_title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        messagesList.setAdapter(Chat.setActivity(this));
    }

    public void onSendClick(View v) {
        Chat.sendMessage(ourName, messageField.getText().toString());
        messageField.setText("");
    }

    @Override
    protected void onPause() {
        Chat.unsetActivity();
        super.onPause();
    }
}
