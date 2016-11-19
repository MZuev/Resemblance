package ru.spbau.resemblance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import static ru.spbau.resemblance.R.id.createGameRoundsText;

public class CreateGameActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private int roundsNumber = 5;
    private TextView roundsText = null;
    private final String ROUNDS_TEXT_PREFIX = "Число раундов: ";
    private SeekBar roundsBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        roundsText = (TextView)findViewById(R.id.createGameRoundsText);
        roundsBar = (SeekBar)findViewById(R.id.createGameRoundsSeekBar);
        roundsBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onStartTrackingTouch(SeekBar bar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar bar) {

    }

    @Override
    public void onProgressChanged(SeekBar bar, int val, boolean byUser) {
        roundsNumber = val;
        roundsText.setText(ROUNDS_TEXT_PREFIX + String.valueOf(roundsNumber));
    }
}
