package ru.spbau.resemblance;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordChangeActivity extends AppCompatActivity {
    private EditText oldPasswordField = null;
    private EditText newPasswordField1 = null;
    private EditText newPasswordField2 = null;
    private final String WRONG_PASSWORD = "Старый пароль введён неверно.";
    private static final String PASSWORDS_DIFFER = "Первый и второй пароль не совпадают.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        oldPasswordField = (EditText)findViewById(R.id.changePasswordOldField);
        newPasswordField1 = (EditText)findViewById(R.id.changePasswordNewField2);
        newPasswordField2 = (EditText)findViewById(R.id.changePasswordNewField1);
    }

    public void onChangePasswordClick(View v) {
        SharedPreferences preferences = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);
        if (!preferences.getString(SettingsActivity.PASSWORD_PREF, "").toString().equals(oldPasswordField.getText().toString())) {
            Toast.makeText(this, WRONG_PASSWORD, Toast.LENGTH_LONG).show();
        } else if (!newPasswordField1.getText().toString().equals(newPasswordField2.getText().toString())) {
            Toast.makeText(this, PASSWORDS_DIFFER, Toast.LENGTH_LONG).show();
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(SettingsActivity.PASSWORD_PREF, newPasswordField1.getText().toString());
            editor.commit();



            finish();
        }
    }
}
