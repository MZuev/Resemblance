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

public class LoginActivity extends AppCompatActivity implements Message.LoginMessageListener {
    public static final int NETWORK_ERROR = -1;
    public static final int SUCCESSFUL_LOGIN = 0;
    public static final int NICKNAME_ERROR = 1;
    public static final int PASSWORD_ERROR = 2;

    private EditText nicknameField = null;
    private EditText passwordField = null;
    private String passwordHash = null;
    private String nickname = null;

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
        String password = passwordField.getText().toString();

        //Getting the hash of the password.
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] hash = messageDigest.digest(password.getBytes());
            passwordHash = new BigInteger(1, hash).toString();
            Message.sendLoginMessage(nickname, passwordHash);
        } catch (NoSuchAlgorithmException e) {
            Log.d("MD5", "MD5 Algorithm not found.");
        }
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
                editor.apply();

                finish();
                break;
            }
            case PASSWORD_ERROR: {
                showToast(R.string.login_password_error);
                break;
            }
            case NICKNAME_ERROR: {
                showToast(R.string.login_nickname_error);
                break;
            }
            case NETWORK_ERROR: {
                showToast(R.string.login_network_error);
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void showToast(final int resId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, resId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onIPChangeClick(View v) {
        SettingsActivity.SetServerIPDialog dialog = new SettingsActivity.SetServerIPDialog();
        dialog.show(getSupportFragmentManager(), "DeletePlayerDialog");
    }
}
