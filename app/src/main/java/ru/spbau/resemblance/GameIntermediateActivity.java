package ru.spbau.resemblance;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GameIntermediateActivity extends AppCompatActivity {
    public static final String CARD_SET_PARAM = "cards_set";
    public static final String OUR_CARDS_PARAM = "our_cards";
    int cards_set = -1;
    ArrayList<Integer> ourCards = null;
    public static final int LEADING_ASSOCIATION_REQUEST = 1;
    public static final int USUAL_ASSOCIATION_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_intermediate);

        Intent callingIntent = getIntent();
        cards_set = callingIntent.getIntExtra(CARD_SET_PARAM, -1);

        //Very temporary solution for testing other gameplay activities
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        ourCards = new ArrayList<>();
        List <ImageStorage.ImageWrapped> set = ImageStorage.getAllSetsCards().get(0).getListOfCards();
        for (ImageStorage.ImageWrapped w: set) {
            ourCards.add(w.getIdImage());
        }

        //Intent lead = new Intent(this, LeadingCardsGridActivity.class);
        //lead.putIntegerArrayListExtra(OUR_CARDS_PARAM, ourCards);
        //startActivityForResult(lead, LEADING_ASSOCIATION_REQUEST);

        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }

        Intent getCard = new Intent(this, CardPickerActivity.class);
        getCard.putIntegerArrayListExtra(OUR_CARDS_PARAM, ourCards);
        startActivityForResult(getCard, USUAL_ASSOCIATION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LEADING_ASSOCIATION_REQUEST) {
            String association = data.getStringExtra(LeadingCardsGridActivity.ASSOCIATION_PARAM);
            long pictureId = data.getLongExtra(LeadingCardsGridActivity.PICTURE_PARAM, -1L);
            Toast.makeText(this, association + String.valueOf(pictureId), Toast.LENGTH_SHORT).show();
        }
        if (requestCode == USUAL_ASSOCIATION_REQUEST) {
            long pictureId = data.getLongExtra(LeadingCardsGridActivity.PICTURE_PARAM, -1L);
            Toast.makeText(this, String.valueOf(pictureId), Toast.LENGTH_SHORT).show();
        }
    }
}
