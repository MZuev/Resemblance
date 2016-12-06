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
    private final int NETWORK_ERROR = -1;
    private final int SUCCESS = 0;
    private final int NICKNAME_ERROR = 1;
    private static final String PASSWORDS_DIFFER = "Первый и второй пароль не совпадают.";
    private static final String SUCCESSFUL_REGISTRATION = "Регистрация успешно завершена.";
    private static final String NICKNAME_IN_USE = "Имя пользователя занято.";

    private static RegistrationActivity currentActivity;
    private EditText nicknameField = null;
    private EditText passwordField1 = null;
    private EditText passwordField2 = null;
    String nickname = null;
    String password = null;
    String passwordHash = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        nicknameField = (EditText)findViewById(R.id.registrationNickField);
        passwordField1 = (EditText)findViewById(R.id.registrationPasswordField1);
        passwordField2 = (EditText)findViewById(R.id.registrationPasswordField2);
    }

    @Override
    protected void onResume() {
        super.onResume();

        currentActivity = this;
    }

    public void onRegisterTouch(View v) {
        if (!(passwordField1.getText().toString().equals(passwordField2.getText().toString()))) {
            Toast.makeText(this, PASSWORDS_DIFFER, Toast.LENGTH_SHORT).show();
        } else {
            nickname = nicknameField.getText().toString();
            password = passwordField1.getText().toString();
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                byte[] hash = messageDigest.digest(password.getBytes());
                passwordHash = new BigInteger(1, hash).toString();
            } catch (NoSuchAlgorithmException e) {
                Log.d("MD5", "MD5 Algorithm not found.");
            }

            Message.sendRegisterMessage(nickname, passwordHash);
        }
    }

    public static void onResponse(int code) {
        currentActivity.onResponseImpl(code);
}

    private void onResponseImpl(int code) {
        switch (code) {
            case SUCCESS: {
                SharedPreferences preferences = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(SettingsActivity.NICKNAME_PREF, nickname);
                editor.putString(SettingsActivity.PASSWORD_PREF, passwordHash);
                editor.commit();

                Toast.makeText(this, SUCCESSFUL_REGISTRATION, Toast.LENGTH_SHORT).show();

                finish();
            }
            case NICKNAME_ERROR: {
                Toast.makeText(this, NICKNAME_IN_USE, Toast.LENGTH_SHORT).show();
            }
            case NETWORK_ERROR: {
                Toast.makeText(this, "Ошибка сети.", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
}
