package ru.spbau.resemblance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GameExpectationActivity extends AppCompatActivity {
    public static final String RANDOM_GAME_EXTRA = "random_game";

    private boolean randomGame;
    private static GameExpectationActivity expecting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_expectation);

        randomGame = getIntent().getBooleanExtra(RANDOM_GAME_EXTRA, false);

        if (randomGame) {
            //Ask server to put us in the waiting list
            Message.sendJoinRandomGameMessage();
        }
    }

    @Override
    protected void onResume() {
        expecting = this;
    }

    public static void startGame() {
        expecting.startGameImpl();
    }

    private void startGameImpl() {
        Intent intent = new Intent(this, GameIntermediateActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        if (randomGame) {
            //Tell server that we're not about to play anymore
            Message.sendQuitRandomGameMessage();
        }
        super.onDestroy();
    }
}
