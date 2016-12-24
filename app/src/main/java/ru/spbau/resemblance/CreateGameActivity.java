package ru.spbau.resemblance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static ru.spbau.resemblance.R.id.createGameRoundsText;

public class CreateGameActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
        AdapterView.OnItemSelectedListener {
    private int roundsNumber = 3;
    private TextView roundsText = null;
    private final String ROUNDS_TEXT_PREFIX = "Число раундов: ";
    private SeekBar roundsBar = null;
    private Spinner setPicker = null;
    private String[] setNames = null;
    private ArrayList<ImageStorage.SetCardsWrapped> sets = null;
    private int chosenSet = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        roundsText = (TextView)findViewById(R.id.createGameRoundsText);
        roundsBar = (SeekBar)findViewById(R.id.createGameRoundsSeekBar);
        roundsBar.setOnSeekBarChangeListener(this);

        setPicker = (Spinner)findViewById(R.id.createGameSetPicker);
        sets = ImageStorage.getAllSetsCards();
        setNames = new String[sets.size()];
        for (int i = 0; i < sets.size(); i++) {
            setNames[i] = sets.get(i).getNameSetCards();
        }
        ArrayAdapter<String> pickerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, setNames);
        setPicker.setAdapter(pickerAdapter);
        setPicker.setOnItemSelectedListener(this);
        setTitle("Новая игра");
    }

    @Override
    public void onStartTrackingTouch(SeekBar bar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar bar) {

    }

    @Override
    public void onProgressChanged(SeekBar bar, int val, boolean byUser) {
        roundsNumber = val + 1;
        roundsText.setText(ROUNDS_TEXT_PREFIX + String.valueOf(roundsNumber));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenSet = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    public void onCreateClick(View v) {
        if (chosenSet == -1) {
            Toast.makeText(this, "Выберите набор карт.", Toast.LENGTH_SHORT).show();
        } else {
            Message.sendCreateGameMessage(roundsNumber, sets.get(chosenSet).getListOfCards());
            Intent prepareGame = new Intent(this, GamePreparationActivity.class);
            startActivity(prepareGame);
        }
    }
}
