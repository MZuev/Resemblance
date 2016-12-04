package ru.spbau.resemblance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        String nickname = nicknameField.getText().toString();
        String password = passwordField.getText().toString();
        String passwordHash = null;

        //Getting the hash of the password.
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] hash = messageDigest.digest(password.getBytes());
            passwordHash = new BigInteger(1, hash).toString();
        } catch (NoSuchAlgorithmException e) {
            Log.d("MD5", "MD5 Algorithm not found.");
        }


        //SendMessageModule.sendMessage();

        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SettingsActivity.NICKNAME_PREF, nickname);
        editor.putString(SettingsActivity.PASSWORD_PREF, passwordHash);
        editor.putInt(SettingsActivity.RATING_PREF, 100500);
        editor.commit();

        finish();
    }

    public void onRegisterClick(View v) {
        Intent register = new Intent(this, RegistrationActivity.class);
        startActivity(register);
    }
}
