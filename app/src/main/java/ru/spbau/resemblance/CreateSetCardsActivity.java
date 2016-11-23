package ru.spbau.resemblance;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class CreateSetCardsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_set_cards);
    }

    public void onListOfSetsClick(View v) {
        Intent listOfSets = new Intent(this, ListOfSetsActivity.class);
        startActivity(listOfSets);
    }

    public void onAddSetsCardsClick(View v) {
        //TODO
    }

    public void onListOfCardsClick(View v) {
        //TODO
      /*  Intent listOfCards = new Intent(this, ListOfCardsActivity.class);
        startActivity(listOfCards);*/
    }

    public void onAddCardsClick(View v) {
        //TODO
    }
}
