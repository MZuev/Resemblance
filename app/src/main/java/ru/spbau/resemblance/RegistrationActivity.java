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
    private final int NETWORK_ERROR = -1;
    private final int SUCCESS = 0;
    private final int NICKNAME_ERROR = 1;
    private static final String PASSWORDS_DIFFER = "Первый и второй пароль не совпадают.";
    private static final String NICKNAME_IN_USE_ERROR = "Имя пользователя занято.";
    private static final String UNKNOWN_ERROR = "Ошибка";
    private static final String SUCCESSFUL_REGISTRATION = "Регистрация успешно завершена.";
    private static final String REGISTRATION_RESPONSE_MESSAGE =
            "ru.spbau.resemblance.REGISTRATION_RESPONSE";

    private EditText nicknameField = null;
    private EditText passwordField1 = null;
    private EditText passwordField2 = null;
    private String nickname = null;
    private String password = null;
    private String passwordHash = null;
    private MessageToastMaker toastMaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        nicknameField = (EditText)findViewById(R.id.registrationNickField);
        passwordField1 = (EditText)findViewById(R.id.registrationPasswordField1);
        passwordField2 = (EditText)findViewById(R.id.registrationPasswordField2);

        toastMaker = new MessageToastMaker(this, REGISTRATION_RESPONSE_MESSAGE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Message.setRegisterListener(this);
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

    @Override
    public void onRegisterResponse(int code) {
        switch (code) {
            case SUCCESS: {
                finish();
                toastMaker.showToast(SUCCESSFUL_REGISTRATION);
                break;
            }
            case NICKNAME_ERROR: {
                toastMaker.showToast(NICKNAME_IN_USE_ERROR);
                break;
            }
            case NETWORK_ERROR: {
                toastMaker.showToast(UNKNOWN_ERROR);
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        toastMaker.close();

        super.onDestroy();
    }
}
