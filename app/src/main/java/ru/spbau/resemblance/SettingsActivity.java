package ru.spbau.resemblance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SettingsActivity extends AppCompatActivity implements ListView.OnItemClickListener {
    public static final String PREFERENCES = "preferences.xml";
    public static final String NICKNAME_PREF = "nickname";
    public static final String PASSWORD_PREF = "password";
    public static final String RATING_PREF = "rating";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        String[] options = getResources().getStringArray(R.array.settings_options);
        ListView optionsList = (ListView)findViewById(R.id.settingsOptionsList);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, options);

        optionsList.setAdapter(adapter);
        optionsList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: {
                Intent changePassword = new Intent(this, PasswordChangeActivity.class);
                startActivity(changePassword);
                break;
            }
            case 1: {
                SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(NICKNAME_PREF);
                editor.remove(PASSWORD_PREF);
                editor.remove(RATING_PREF);
                editor.apply();

                Intent quit = new Intent(this, LoginActivity.class);
                startActivity(quit);
                break;
            }
        }
    }
}
