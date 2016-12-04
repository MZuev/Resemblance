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
    public static final int LEADING_ASSOCIATION_REQUEST = 1;
    public static final int USUAL_ASSOCIATION_REQUEST = 2;

    private ArrayList<Long> cards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_intermediate);

        Intent callingIntent = getIntent();
        //cards_set = callingIntent.getIntExtra(CARD_SET_PARAM, -1);

        //Very temporary solution for testing other gameplay activities
        new Thread(new TestActivities()).start();
        //ourCards = new ArrayList<>();
        //List <ImageStorage.ImageWrapped> set = ImageStorage.getAllSetsCards().get(0).getListOfCards();
        //for (ImageStorage.ImageWrapped w: set) {
        //    ourCards.add(w.getIdImage());
        //}

    }

    public void lead() {
        Intent lead = new Intent(this, LeadingCardsGridActivity.class);
        lead.putExtra(OUR_CARDS_PARAM, getCardsArr());
        startActivityForResult(lead, LEADING_ASSOCIATION_REQUEST);
    }

    public void chooseCard() {
        Intent choose = new Intent(this, CardPickerActivity.class);
        choose.putExtra(OUR_CARDS_PARAM, getCardsArr());
        startActivityForResult(choose, USUAL_ASSOCIATION_REQUEST);
    }

    public void vote() {
        Intent vote = new Intent(this, CardPickerActivity.class);
        vote.putExtra(OUR_CARDS_PARAM, getCardsArr());
        startActivityForResult(vote, USUAL_ASSOCIATION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LEADING_ASSOCIATION_REQUEST) {
            //Now we need to send the association and remove the card fom our cards list.
            String association = data.getStringExtra(LeadingCardsGridActivity.ASSOCIATION_PARAM);
            long pictureId = data.getLongExtra(LeadingCardsGridActivity.PICTURE_PARAM, -1L);

            int cardIndex = 0;
            while (cards.get(cardIndex) != pictureId) {
                cardIndex++;
            }
            cards.remove(cardIndex);

            //TODO: send the association
            Toast.makeText(this, association + String.valueOf(pictureId), Toast.LENGTH_SHORT).show();
        }
        if (requestCode == USUAL_ASSOCIATION_REQUEST) {
            //Sending player's choice and removing the chosen card.
            long pictureId = data.getLongExtra(LeadingCardsGridActivity.PICTURE_PARAM, -1L);

            int cardIndex = 0;
            while (cards.get(cardIndex) != pictureId) {
                cardIndex++;
            }
            cards.remove(cardIndex);

            //TODO: send choice
            Toast.makeText(this, String.valueOf(pictureId), Toast.LENGTH_SHORT).show();
        }
    }

    private long[] getCardsArr() {
        long[] cardsArr = new long[cards.size()];
        for (int i = 0; i < cards.size(); i++) {
            cardsArr[i] = cards.get(i);
        }
        return cardsArr;
    }

    private class TestActivities implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 8; i++) {
                cards.add(i + 1L);
            }

            try {
                Thread.sleep(2000);
            } catch (Exception e) {}

            lead();

            try {
                Thread.sleep(2000);
            } catch (Exception e) {}

            chooseCard();

            try {
                Thread.sleep(2000);
            } catch (Exception e) {}

            vote();
        }
    }
}
