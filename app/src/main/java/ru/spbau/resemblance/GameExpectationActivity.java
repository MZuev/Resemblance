package ru.spbau.resemblance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class GameExpectationActivity extends AppCompatActivity implements
        Message.GameExpectationMessageListener {
    public static final String RANDOM_GAME_EXTRA = "random_game";

    private boolean randomGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_expectation);

        Message.setGameExpectationListener(this);
        setTitle("Ожидание игры");

        randomGame = getIntent().getBooleanExtra(RANDOM_GAME_EXTRA, false);
        if (randomGame) {
            //Ask server to put us in the waiting list
            Message.sendJoinRandomGameMessage();
        }
    }

    @Override
    public void onStartGameMessage(int roundsNumber, int playersNumber,
                                    ArrayList<String> names) {
        Intent startGame = new Intent(this, GameIntermediateActivity.class);
        startGame.putExtra(GameIntermediateActivity.ROUNDS_NUMBER_PARAM, roundsNumber);
        startGame.putExtra(GameIntermediateActivity.PLAYERS_NUMBER_PARAM, playersNumber);
        startGame.putStringArrayListExtra(GameIntermediateActivity.PLAYERS_NAMES_PARAM, names);
        startActivity(startGame);
        finish();
    }

    @Override
    public void onGameCancelled() {
        finish();
    }

    public void onCancelClick(View v) {
        if (randomGame) {
            Message.sendQuitRandomGameMessage();
        } else {
            Message.sendQuitFriendGameMessage();
        }
        finish();
    }

    @Override
    public void onBackPressed() {}

    @Override
    protected void onDestroy() {
        Message.unSetGameExpectationListener();

        super.onDestroy();
    }
}
