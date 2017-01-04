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

public class PasswordChangeActivity extends AppCompatActivity implements
        Message.PasswordChangeListener {
    private EditText oldPasswordField = null;
    private EditText newPasswordField1 = null;
    private EditText newPasswordField2 = null;
    private String newPasswordHash = null;
    private volatile boolean waiting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        oldPasswordField = (EditText)findViewById(R.id.changePasswordOldField);
        newPasswordField1 = (EditText)findViewById(R.id.changePasswordNewField2);
        newPasswordField2 = (EditText)findViewById(R.id.changePasswordNewField1);

        Message.setPasswordChangeListener(this);
    }

    public void onChangePasswordClick(View v) {
        if (!newPasswordField1.getText().toString().equals(newPasswordField2.getText().toString())) {
            Toast.makeText(this, R.string.passwords_differ, Toast.LENGTH_LONG).show();
        } else {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                byte[] oldHash = messageDigest.digest(oldPasswordField.getText().toString().getBytes());
                String oldPasswordHash = new BigInteger(1, oldHash).toString();
                byte[] newHash = messageDigest.digest(newPasswordField1.getText().toString().getBytes());
                newPasswordHash = new BigInteger(1, newHash).toString();

                if (!waiting) {
                    Message.sendPasswordChangeMessage(oldPasswordHash, newPasswordHash);
                    waiting = true;
                } else {
                    Toast.makeText(this, R.string.password_change_waiting, Toast.LENGTH_SHORT).show();
                }
            } catch (NoSuchAlgorithmException e) {
                Log.d("MD5", "MD5 Algorithm not found.");
            }
        }
    }

    @Override
    public void onPasswordChangeResponse(int code) {
        final int PASSWORD_CHANGE_SUCCESS = 1;
        final int WRONG_PASSWORD = 2;
        switch (code) {
            case PASSWORD_CHANGE_SUCCESS: {
                SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(SettingsActivity.PASSWORD_PREF, newPasswordHash);
                editor.apply();
                finish();
            }
            case WRONG_PASSWORD: {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PasswordChangeActivity.this,
                                R.string.password_change_wrong_old_password, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        waiting = false;
    }

    @Override
    public void onBackPressed() {
        if (waiting) {
            Toast.makeText(this, R.string.password_change_waiting, Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }
}
