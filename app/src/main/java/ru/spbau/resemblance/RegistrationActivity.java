package ru.spbau.resemblance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegistrationActivity extends AppCompatActivity implements Message.RegisterMessageListener {
    private EditText nicknameField = null;
    private EditText passwordField1 = null;
    private EditText passwordField2 = null;
    private String passwordHash = null;

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

        Message.setRegisterListener(this);
    }

    public void onRegisterTouch(View v) {
        if (!(passwordField1.getText().toString().equals(passwordField2.getText().toString()))) {
            Toast.makeText(this, R.string.passwords_differ, Toast.LENGTH_SHORT).show();
        } else {
            String nickname = nicknameField.getText().toString();
            String password = passwordField1.getText().toString();
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

    @Override
    public void onRegisterResponse(int code) {
        final int NETWORK_ERROR = -1;
        final int SUCCESS = 0;
        final int NICKNAME_ERROR = 1;
        int messageId = R.string.registration_unknown_error;
        switch (code) {
            case SUCCESS: {
                finish();
                messageId = R.string.registration_success;
                break;
            }
            case NICKNAME_ERROR: {
                messageId = R.string.registration_nickname_used_error;
                break;
            }
        }
        final String messageToShow = getString(messageId);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegistrationActivity.this, messageToShow, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
