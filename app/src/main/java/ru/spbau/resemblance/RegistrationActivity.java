package ru.spbau.resemblance;

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

public class RegistrationActivity extends AppCompatActivity {
    private EditText nicknameField = null;
    private EditText passwordField1 = null;
    private EditText passwordField2 = null;
    public static final String PASSWORDS_DIFFER = "Первый и второй пароль не совпадают.";
    public static final String SUCCESSFUL_REGISTRATION = "Регистрация успешно завершена.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        nicknameField = (EditText)findViewById(R.id.registrationNickField);
        passwordField1 = (EditText)findViewById(R.id.registrationPasswordField1);
        passwordField2 = (EditText)findViewById(R.id.registrationPasswordField2);
    }

    public void onRegisterTouch(View v) {
        if (!(passwordField1.getText().toString().equals(passwordField2.getText().toString()))) {
            Toast.makeText(this, PASSWORDS_DIFFER, Toast.LENGTH_SHORT).show();
        } else {
            String nickname = nicknameField.getText().toString();
            String password = passwordField1.getText().toString();
            String passwordHash = null;
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                byte[] hash = messageDigest.digest(password.getBytes());
                passwordHash = new BigInteger(1, hash).toString();
            } catch (NoSuchAlgorithmException e) {
                Log.d("MD5", "MD5 Algorithm not found.");
            }

            Message.sendRegisterMessage(nickname, passwordHash);

            SharedPreferences preferences = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString(SettingsActivity.NICKNAME_PREF, nickname);
            editor.putString(SettingsActivity.PASSWORD_PREF, password); // ??? passwordHash
            editor.putInt(SettingsActivity.RATING_PREF, 100500);
            editor.commit();

            Toast.makeText(this, SUCCESSFUL_REGISTRATION, Toast.LENGTH_SHORT).show();

            finish();
        }
    }
}
