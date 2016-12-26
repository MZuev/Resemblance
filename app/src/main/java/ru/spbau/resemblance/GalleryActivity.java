package ru.spbau.resemblance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static final String CARDS_PARAM = "our_cards";
    private final static int COLUMNS_NUMBER = 3;

    private ImageStorage.ImageWrapped[] cardViews;
    private GridView grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        grid = (GridView) findViewById(R.id.galleryGrid);

        Intent callingIntent = getIntent();

        List<Long> cardIds = new ArrayList<>();
        long[] cardsArr = callingIntent.getLongArrayExtra(CARDS_PARAM);
        for(long card: cardsArr) {
            cardIds.add(card);
        }

        cardViews = new ImageStorage.ImageWrapped[cardIds.size()];
        for (int i = 0; i < cardIds.size(); i++) {
            cardViews[i] = ImageStorage.ImageWrapped.createById((int)(long)cardIds.get(i));
        }

        ListAdapter cardsAdapter = new CardsAdapter(this, cardViews,
                getResources().getDisplayMetrics().widthPixels / COLUMNS_NUMBER);
        grid.setAdapter(cardsAdapter);
        grid.setNumColumns(COLUMNS_NUMBER);

        grid.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent showPicture = new Intent(this, GalleryCardViewerActivity.class);
        showPicture.putExtra(LeadingAssociationActivity.IMAGE_PARAM, id);
        startActivity(showPicture);
    }
}
