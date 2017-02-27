package ru.spbau.resemblance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CreateGameActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
        AdapterView.OnItemSelectedListener {
    private static final int MINUTE = 60;
    private static final long SECOND = 1000;
    private int roundsNumber = 3;
    private TextView roundsText = null;
    private SeekBar roundsBar;
    private ArrayList<ImageStorage.SetCardsWrapped> sets = null;
    private int chosenSet = -1;
    private SeekBar timeBar;
    private int expectationTimeSeconds = 60;
    private TextView timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Spinner setPicker = null;
        String[] setNames = null;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        roundsText = (TextView)findViewById(R.id.createGameRoundsText);
        roundsBar = (SeekBar)findViewById(R.id.createGameRoundsSeekBar);
        roundsBar.setOnSeekBarChangeListener(this);
        timeText = (TextView)findViewById(R.id.createGameTimeText);
        timeBar = (SeekBar)findViewById(R.id.createGameTimeBar);
        timeBar.setOnSeekBarChangeListener(this);
        timeBar.setProgress(1);

        setPicker = (Spinner)findViewById(R.id.createGameSetPicker);
        sets = ImageStorage.getAllSetsCards();
        setNames = new String[sets.size()];
        for (int i = 0; i < sets.size(); i++) {
            setNames[i] = sets.get(i).getNameSetCards();
        }
        ArrayAdapter<String> pickerAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,
                setNames);
        setPicker.setAdapter(pickerAdapter);
        setPicker.setOnItemSelectedListener(this);
        setTitle(R.string.create_game_title);
    }

    @Override
    public void onStartTrackingTouch(SeekBar bar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar bar) {

    }

    @Override
    public void onProgressChanged(SeekBar bar, int val, boolean byUser) {
        if (bar.equals(roundsBar)) {
            roundsNumber = val + 1;
            roundsText.setText(String.format(getString(R.string.create_game_rounds), roundsNumber));
        }
        if (bar.equals(timeBar)) {
            expectationTimeSeconds = (val + 1) * 30;
            String time = "";
            if (expectationTimeSeconds / MINUTE > 0) {
                time += " " + expectationTimeSeconds / MINUTE + getString(R.string.minutes);
            }
            if (expectationTimeSeconds % MINUTE > 0) {
                time += " " + expectationTimeSeconds % MINUTE + getString(R.string.seconds);
            }
            timeText.setText(String.format(getString(R.string.create_game_choice_time_text), time));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenSet = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    public void onCreateClick(View v) {
        if (chosenSet == -1) {
            Toast.makeText(this, R.string.create_game_choose_card_set, Toast.LENGTH_SHORT).show();
        } else {
            Message.sendCreateGameMessage(roundsNumber, sets.get(chosenSet).getListOfCards(),
                    expectationTimeSeconds * SECOND, sets.get(chosenSet).getHash());
            Intent prepareGame = new Intent(this, GamePreparationActivity.class);
            startActivity(prepareGame);
        }
    }
}
