package ru.spbau.resemblance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class GameExpectationActivity extends AppCompatActivity {
    public static final String RANDOM_GAME_EXTRA = "random_game";

    private boolean randomGame;
    private volatile boolean started = false;
    private static GameExpectationActivity expecting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_expectation);

        randomGame = getIntent().getBooleanExtra(RANDOM_GAME_EXTRA, false);
        started = false;

        if (randomGame) {
            //Ask server to put us in the waiting list
            Message.sendJoinRandomGameMessage();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        expecting = this;
    }

    public static void startGame(int roundsNumber, int playersNumber,
                                 ArrayList<String> names) {
        expecting.startGameImpl(roundsNumber, playersNumber, names);
    }

    private void startGameImpl(int roundsNumber, int playersNumber,
                               ArrayList<String> names) {
        started = true;
        Intent startGame = new Intent(this, GameIntermediateActivity.class);
        startGame.putExtra(GameIntermediateActivity.ROUNDS_NUMBER_PARAM, roundsNumber);
        startGame.putExtra(GameIntermediateActivity.PLAYERS_NUMBER_PARAM, playersNumber);
        startGame.putStringArrayListExtra(GameIntermediateActivity.PLAYERS_NAMES_PARAM, names);
        startActivity(startGame);
    }

    @Override
    protected void onPause() {
        if (randomGame && !started) {
            //Tell server that we're not about to play anymore
            Message.sendQuitRandomGameMessage();
        }
        super.onPause();
    }
}
