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
    private final String WRONG_PASSWORD = "Старый пароль введён неверно.";
    private static final String PASSWORDS_DIFFER = "Первый и второй пароль не совпадают.";
    private static final String WAITING_TEXT = "Ожидание ответа.";
    private static final String PASSWORD_CHANGE_RESPONSE_MESSAGE =
            "ru.spbau.resemblance.REGISTRATION_RESPONSE";
    private final int PASSWORD_CHANGE_SUCCESS = 1;
    private final int PASSWORD_CHANGE_FAILURE = 2;

    private EditText oldPasswordField = null;
    private EditText newPasswordField1 = null;
    private EditText newPasswordField2 = null;
    private String oldPasswordHash = null;
    private String newPasswordHash = null;
    private volatile boolean waiting = false;
    private MessageToastMaker toastMaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        oldPasswordField = (EditText)findViewById(R.id.changePasswordOldField);
        newPasswordField1 = (EditText)findViewById(R.id.changePasswordNewField2);
        newPasswordField2 = (EditText)findViewById(R.id.changePasswordNewField1);

        toastMaker = new MessageToastMaker(this, PASSWORD_CHANGE_RESPONSE_MESSAGE);
        Message.setPasswordChangeListener(this);
    }

    public void onChangePasswordClick(View v) {
        if (!newPasswordField1.getText().toString().equals(newPasswordField2.getText().toString())) {
            Toast.makeText(this, PASSWORDS_DIFFER, Toast.LENGTH_LONG).show();
        } else {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                byte[] oldHash = messageDigest.digest(oldPasswordField.getText().toString().getBytes());
                oldPasswordHash = new BigInteger(1, oldHash).toString();
                byte[] newHash = messageDigest.digest(newPasswordField1.getText().toString().getBytes());
                newPasswordHash = new BigInteger(1, newHash).toString();
            } catch (NoSuchAlgorithmException e) {
                Log.d("MD5", "MD5 Algorithm not found.");
            }

            if (!waiting) {
                Message.sendPasswordChangeMessage(oldPasswordHash, newPasswordHash);
                waiting = true;
            } else {
                Toast.makeText(this, WAITING_TEXT, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPasswordChangeResponse(int code) {
        if (code == PASSWORD_CHANGE_SUCCESS) {
            SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(SettingsActivity.PASSWORD_PREF, newPasswordHash);
            editor.commit();
            finish();
        } else {
            toastMaker.showToast(WRONG_PASSWORD);
        }
        waiting = false;
    }

    @Override
    protected void onDestroy() {
        toastMaker.close();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (waiting) {
            Toast.makeText(this, WAITING_TEXT, Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }
}
