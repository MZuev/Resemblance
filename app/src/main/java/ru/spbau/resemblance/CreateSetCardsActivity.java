package ru.spbau.resemblance;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

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
        Intent showCards = new Intent(this, ShowImageFromListActivity.class);
        ArrayList<ImageStorage.ImageWrapped> listImages = ImageStorage.getAllImages();
        ArrayList<Integer> idImagesList = new ArrayList<Integer>();
        for (ImageStorage.ImageWrapped curImage : listImages) {
            idImagesList.add(curImage.getIdImage());
        }
        showCards.putExtra("listImage", idImagesList);
        startActivity(showCards);
    }

    public void onAddCardsClick(View v) {
        //TODO
    }
}
