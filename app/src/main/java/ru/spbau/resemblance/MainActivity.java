package ru.spbau.resemblance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String RATING_PREFIX = "Рейтинг: ";
    private TextView nicknameText = null;
    private TextView ratingText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nicknameText = (TextView)findViewById(R.id.mainNickname);
        ratingText = (TextView)findViewById(R.id.mainRating);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);
        if (!preferences.contains(SettingsActivity.NICKNAME_PREF)) {
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
        }

        nicknameText.setText(preferences.getString(SettingsActivity.NICKNAME_PREF, ""));
        ratingText.setText(RATING_PREFIX + preferences.getInt(SettingsActivity.RATING_PREF, 0));
    }

    public void onRandomGameClick(View v) {
        Intent randomGame = new Intent(this, RandomGameActivity.class);
        startActivity(randomGame);
    }

    public void onCreateGameClick(View v) {
        Intent newGame = new Intent(this, CreateGameActivity.class);
        startActivity(newGame);
    }

    public void onJoinGameClick(View v) {
        Intent joinGame = new Intent(this, JoinGameActivity.class);
        startActivity(joinGame);
    }

    public void onSettingsClick(View v) {
        Intent settings = new Intent(this, SettingsActivity.class);
        startActivity(settings);
    }
}
