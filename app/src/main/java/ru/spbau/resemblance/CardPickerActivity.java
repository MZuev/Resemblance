package ru.spbau.resemblance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

import java.util.List;

public class CardPickerActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static String PICTURE_PARAM = "picture_id";
    private ImageStorage.ImageWrapped[] cardViews;
    private final int COLUMNS_NUMBER = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_picker);

        GridView grid = (GridView) findViewById(R.id.cardPickedGrid);

        List<Integer> cardIds = getIntent().getIntegerArrayListExtra(GameIntermediateActivity.OUR_CARDS_PARAM);

        cardViews = new ImageStorage.ImageWrapped[cardIds.size()];
        for (int i = 0; i < cardIds.size(); i++) {
            cardViews[i] = ImageStorage.ImageWrapped.createById(cardIds.get(i));
        }

        ListAdapter cardsAdapter = new CardsAdapter(this, cardViews);

        grid.setAdapter(cardsAdapter);
        grid.setNumColumns(COLUMNS_NUMBER);
        //grid.setVerticalSpacing(-200);
        grid.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent showPicture = new Intent(this, CardViewerActivity.class);
        showPicture.putExtra(LeadingAssociationActivity.IMAGE_PARAM, id);
        startActivityForResult(showPicture, GameIntermediateActivity.USUAL_ASSOCIATION_REQUEST);
        //finish();
        //Toast.makeText(this, String.valueOf(id), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //String association = data.getStringExtra(ASSOCIATION_PARAM);
        long pictureId = data.getLongExtra(PICTURE_PARAM, -1L);
        //Toast.makeText(this, association + String.valueOf(pictureId), Toast.LENGTH_SHORT).show();
        if (pictureId >= 0) {
            setResult(RESULT_OK, data);
            finish();
        }
    }
}