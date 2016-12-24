package ru.spbau.resemblance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static ru.spbau.resemblance.R.id.joinGameJoinButton;
import static ru.spbau.resemblance.R.id.joinGameNameField;

public class JoinGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
    }

    public void onJoinPressed(View v) {
        String gameCreatorName = ((EditText)findViewById(joinGameNameField)).getText().toString();
        Message.sendJoinFriendGameMessage(gameCreatorName);

        Intent waitForGame = new Intent(this, GameExpectationActivity.class);
        waitForGame.putExtra(GameExpectationActivity.RANDOM_GAME_EXTRA, false);
        startActivity(waitForGame);
    }
}
