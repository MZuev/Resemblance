package ru.spbau.resemblance;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LeadingCardsGridActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static final String ASSOCIATION_PARAM = "association";
    public static final String OUR_CARDS_PARAM = "our_cards";
    public static final String PICTURE_PARAM = "picture_id";

    private ImageStorage.ImageWrapped[] cardViews;
    private final int COLUMNS_NUMBER = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leading_cards_grid);

        GridView grid = (GridView) findViewById(R.id.leadingCardsGrid);

        List <Long> cardIds = new ArrayList<>();
        long[] cardsArr = getIntent().getLongArrayExtra(OUR_CARDS_PARAM);
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
        setTitle("Вы водите");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent showPicture = new Intent(this, LeadingAssociationActivity.class);
        showPicture.putExtra(LeadingAssociationActivity.IMAGE_PARAM, id);
        startActivityForResult(showPicture, GameIntermediateActivity.LEADING_ASSOCIATION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        long pictureId = data.getLongExtra(PICTURE_PARAM, -1L);
        if (pictureId >= 0) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public void onBackPressed() {}
}
