package ru.spbau.resemblance;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
            SharedPreferences preferences = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString(SettingsActivity.NICKNAME_PREF, nicknameField.getText().toString());
            editor.putString(SettingsActivity.PASSWORD_PREF, passwordField1.getText().toString());
            editor.putInt(SettingsActivity.RATING_PREF, 100500);
            editor.commit();

            Toast.makeText(this, SUCCESSFUL_REGISTRATION, Toast.LENGTH_SHORT).show();

            finish();
        }
    }
}
