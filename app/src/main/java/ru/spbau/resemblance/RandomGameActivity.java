package ru.spbau.resemblance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RandomGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_game);
        //TODO ask server to put us in the waiting list
    }

    @Override
    protected void onDestroy() {
        //TODO tell server that we're not about to play anymore
        super.onDestroy();
    }
}
