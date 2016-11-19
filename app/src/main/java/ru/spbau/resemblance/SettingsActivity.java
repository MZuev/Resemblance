package ru.spbau.resemblance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {
    public static final String PREFERENCES = "preferences.xml";
    public static final String NICKNAME_PREF = "nickname";
    public static final String PASSWORD_PREF = "password";
    public static final String RATING_PREF = "rating";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void onPasswordChangeClick(View v) {
        Intent changePassword = new Intent(this, PasswordChangeActivity.class);
        startActivity(changePassword);
    }

    public void onQuitClick(View v) {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(NICKNAME_PREF);
        editor.remove(PASSWORD_PREF);
        editor.remove(RATING_PREF);
        editor.commit();

        Intent quit = new Intent(this, LoginActivity.class);
        startActivity(quit);
    }
}
