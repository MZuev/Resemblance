package ru.spbau.resemblance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListOfSetsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_sets);

        ListView setsView = (ListView)findViewById(R.id.setsList);

        ArrayList<String> sets = new ArrayList<>();
        for (ImageStorage.SetCardsWrapped set: ImageStorage.getAllSetsCards()) {
            sets.add(String.format(getString(R.string.list_of_sets_item), set.getNameSetCards(),
                    set.getSizeSetCards()));
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, sets);
        setsView.setAdapter(adapter);

        setsView.setOnItemClickListener(this);

        setTitle(R.string.sets_title);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent showCards = new Intent(ListOfSetsActivity.this, GalleryActivity.class);
        ArrayList<ImageStorage.ImageWrapped> cards =
                ImageStorage.getAllSetsCards().get(position).getListOfCards();
        long[] cardIds = new long[cards.size()];
        for (int i = 0; i < cardIds.length; i++) {
            cardIds[i] = cards.get(i).getIdImage();
        }
        showCards.putExtra(GalleryActivity.CARDS_PARAM, cardIds);
        startActivity(showCards);
    }
}
