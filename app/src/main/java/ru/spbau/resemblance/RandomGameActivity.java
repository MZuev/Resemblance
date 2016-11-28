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
        //TODO ask server to put us in the waiting list

        //This is temporary, to test cards grid:

        //ImageStorage.SetCardsWrapped cardsSet = ImageStorage.getAllSetsCards().get(0);
        //Bundle bundle = new Bundle();
        //bundle.pu
        Intent intent = new Intent(this, GameIntermediateActivity.class);
        intent.putExtra(GameIntermediateActivity.CARD_SET_PARAM, 0);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        //TODO tell server that we're not about to play anymore
        super.onDestroy();
    }
}
