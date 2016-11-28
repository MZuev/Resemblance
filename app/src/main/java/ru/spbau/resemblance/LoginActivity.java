package ru.spbau.resemblance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private EditText nicknameField = null;
    private EditText passwordField = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nicknameField = (EditText)findViewById(R.id.loginNicknameField);
        passwordField = (EditText)findViewById(R.id.loginPasswordField);
    }

    public void onLoginClick(View v) {
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SettingsActivity.NICKNAME_PREF, nicknameField.getText().toString());
        editor.putString(SettingsActivity.PASSWORD_PREF, passwordField.getText().toString());
        editor.putInt(SettingsActivity.RATING_PREF, 100500);
        editor.commit();

        finish();
    }

    public void onRegisterClick(View v) {
        Intent register = new Intent(this, RegistrationActivity.class);
        startActivity(register);
    }
}
