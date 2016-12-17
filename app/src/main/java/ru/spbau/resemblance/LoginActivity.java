package ru.spbau.resemblance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity implements Message.LoginMessageListener {
    public static final int NETWORK_ERROR = -1;
    public static final int SUCCESSFUL_LOGIN = 0;
    public static final int NICKNAME_ERROR = 1;
    public static final int PASSWORD_ERROR = 2;

    private EditText nicknameField = null;
    private EditText passwordField = null;
    private String passwordHash = null;
    private String nickname = null;
    private String password = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nicknameField = (EditText)findViewById(R.id.loginNicknameField);
        passwordField = (EditText)findViewById(R.id.loginPasswordField);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Message.setLoginListener(this);
    }

    public void onLoginClick(View v) {
        nickname = nicknameField.getText().toString();
        password = passwordField.getText().toString();

        //Getting the hash of the password.
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] hash = messageDigest.digest(password.getBytes());
            passwordHash = new BigInteger(1, hash).toString();
        } catch (NoSuchAlgorithmException e) {
            Log.d("MD5", "MD5 Algorithm not found.");
        }

        Message.sendLoginMessage(nickname, passwordHash);
    }

    public void onRegisterClick(View v) {
        Intent register = new Intent(this, RegistrationActivity.class);
        startActivity(register);
    }

    @Override
    public void onLoginResponse(int code) {
        switch (code) {
            case SUCCESSFUL_LOGIN: {
                SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(SettingsActivity.NICKNAME_PREF, nickname);
                editor.putString(SettingsActivity.PASSWORD_PREF, passwordHash);
                editor.commit();

                finish();
            }
            case PASSWORD_ERROR: {
                Toast.makeText(this, "Неправильный пароль.", Toast.LENGTH_SHORT).show();
                break;
            }
            case NICKNAME_ERROR: {
                Toast.makeText(this, "Неправильный логин.", Toast.LENGTH_SHORT).show();
                break;
            }
            case NETWORK_ERROR: {
                Toast.makeText(this, "Ошибка сети.", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
}
