package ru.spbau.resemblance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements Message.LoginMessageListener,
        Message.RatingMessageListener {
    private TextView nicknameText = null;
    private TextView ratingText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nicknameText = (TextView)findViewById(R.id.mainNickname);
        ratingText = (TextView)findViewById(R.id.mainRatingText);

        ImageStorage.createImageStorage(this);
        SendMessageModule.connectToServer(SendMessageModule.getServerIP(this));

        Message.setRatingListener(this);

        SharedPreferences preferences = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);
        if (preferences.contains(SettingsActivity.NICKNAME_PREF)) {
            Message.sendLoginMessage(preferences.getString(SettingsActivity.NICKNAME_PREF, null),
                    preferences.getString(SettingsActivity.PASSWORD_PREF, null));

            Message.setLoginListener(this);
        } else {
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);
        nicknameText.setText(preferences.getString(SettingsActivity.NICKNAME_PREF, ""));
        ratingText.setText(String.format(getString(R.string.main_rating_text),
                preferences.getInt(SettingsActivity.RATING_PREF, -1)));
    }

    public void onRandomGameClick(View v) {
        Intent randomGame = new Intent(this, GameExpectationActivity.class);
        randomGame.putExtra(GameExpectationActivity.RANDOM_GAME_EXTRA, true);
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

    public void onSetCardsClick(View v) {
        Intent setCards = new Intent(this, CardSetsActivity.class);
        startActivity(setCards);
    }

    @Override
    public void onLoginResponse(int code) {
        if (code != LoginActivity.SUCCESSFUL_LOGIN) {
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, R.string.authorisation_required,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onRatingMessage(final int rating) {
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(SettingsActivity.RATING_PREF, rating);
        editor.apply();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ratingText.setText(String.format(getString(R.string.main_rating_text), rating));
            }
        });
    }
}
