package ru.spbau.resemblance;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements ListView.OnItemClickListener {
    public static final String PREFERENCES = "preferences.xml";
    public static final String NICKNAME_PREF = "nickname";
    public static final String PASSWORD_PREF = "password";
    public static final String RATING_PREF = "rating";
    public static final String IP_PREF = "ip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        String[] options = getResources().getStringArray(R.array.settings_options);
        ListView optionsList = (ListView)findViewById(R.id.settingsOptionsList);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, options);

        optionsList.setAdapter(adapter);
        optionsList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: {
                Intent changePassword = new Intent(this, PasswordChangeActivity.class);
                startActivity(changePassword);
                break;
            }
            case 1: {
                SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(NICKNAME_PREF);
                editor.remove(PASSWORD_PREF);
                editor.remove(RATING_PREF);
                editor.apply();

                Intent quit = new Intent(this, LoginActivity.class);
                startActivity(quit);
                break;
            }
            case 2: {
                SetServerIPDialog dialog = new SetServerIPDialog();
                dialog.show(getSupportFragmentManager(), "DeletePlayerDialog");
                break;
            }
        }
    }

    public static class SetServerIPDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View field = inflater.inflate(R.layout.server_ip_change_dialog, null);
            builder.setView(field)
                    .setTitle(R.string.new_ip)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNeutralButton(R.string.restore, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SendMessageModule.resetIP(getActivity());
                        }
                    })
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText ipField = (EditText) field.findViewById(R.id.serverIPField);
                            SendMessageModule.setIP(getActivity(), ipField.getText().toString());
                        }
                    });
            return builder.create();
        }
    }
}
