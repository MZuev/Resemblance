package ru.spbau.resemblance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements Message.LoginMessageListener,
        Message.RatingMessageListener {
    private static final String RATING_PREFIX = "Рейтинг: ";
    private TextView nicknameText = null;
    private TextView ratingText = null;
    private static final String RATING_UPDATE_MESSAGE = "ru.spbau.resemblance.UPDATE_RATING";
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nicknameText = (TextView)findViewById(R.id.mainNickname);
        ratingText = (TextView)findViewById(R.id.mainRatingText);

        ImageStorage.createImageStorage(this);
        SendMessageModule.connectToServer();

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
        ratingText.setText(RATING_PREFIX + preferences.getInt(SettingsActivity.RATING_PREF, -1));

        receiver = new RatingUpdateMessageReceiver();
        IntentFilter filter = new IntentFilter(RATING_UPDATE_MESSAGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        //findViewById(R.id.mainScrollView).setNestedScrollingEnabled(false);
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
        Intent setCards = new Intent(this, CreateSetCardsActivity.class);
        startActivity(setCards);
    }

    @Override
    public void onLoginResponse(int code) {
        if (code != LoginActivity.SUCCESSFUL_LOGIN) {
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
        }
    }

    @Override
    public void onRatingMessage(int rating) {SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(SettingsActivity.RATING_PREF, rating);
        editor.commit();

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(RATING_UPDATE_MESSAGE));
    }

    public class RatingUpdateMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences preferences = getSharedPreferences(SettingsActivity.PREFERENCES, MODE_PRIVATE);
            ratingText.setText(RATING_PREFIX + preferences.getInt(SettingsActivity.RATING_PREF, -1));
        }
    }
}
