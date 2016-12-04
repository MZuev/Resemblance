package ru.spbau.resemblance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class RandomGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_game);

        //This is temporary, to test cards grid:

        Intent intent = new Intent(this, GameIntermediateActivity.class);
        //intent.putExtra(GameIntermediateActivity.CARD_SET_PARAM, 0);
        startActivity(intent);
    }
}
