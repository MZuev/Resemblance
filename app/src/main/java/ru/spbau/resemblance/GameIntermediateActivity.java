package ru.spbau.resemblance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class GameIntermediateActivity extends AppCompatActivity {
    //public static final String CARD_SET_PARAM = "cards_set";
    public static final String OUR_CARDS_PARAM = "our_cards";
    public static final String SUGGESTION_PARAM = "suggestion";
    public static final int LEADING_ASSOCIATION_REQUEST = 1;
    public static final int CHOICE_REQUEST = 2;
    public static final int VOTE_REQUEST = 3;

    private static final String CHOOSE_SUGGESTION = "Ваша карта. Ассоциация: ";
    private static final String VOTE_SUGGESTION = "Голосование. Ассоциация: ";
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

    public void chooseCard(String association) {
        Intent choose = new Intent(this, CardPickerActivity.class);
        choose.putExtra(SUGGESTION_PARAM, CHOOSE_SUGGESTION + association);
        choose.putExtra(OUR_CARDS_PARAM, getCardsArr());
        startActivityForResult(choose, CHOICE_REQUEST);
    }

    public void vote(String association) {
        Intent vote = new Intent(this, CardPickerActivity.class);
        vote.putExtra(SUGGESTION_PARAM, VOTE_SUGGESTION + association);
        vote.putExtra(OUR_CARDS_PARAM, getCardsArr());
        startActivityForResult(vote, VOTE_REQUEST);
    }

    public void addCard(long card) {
        cards.add(card);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LEADING_ASSOCIATION_REQUEST: {
                //Now we need to send the association and remove the card fom our cards list.
                String association = data.getStringExtra(LeadingCardsGridActivity.ASSOCIATION_PARAM);
                long pictureId = data.getLongExtra(LeadingCardsGridActivity.PICTURE_PARAM, -1L);

                cards.remove(cards.indexOf(pictureId));

                //TODO: send the association
                Toast.makeText(this, association + String.valueOf(pictureId), Toast.LENGTH_SHORT).show();
                break;
            }

            case CHOICE_REQUEST: {
                //Sending player's choice and removing the chosen card.
                long pictureId = data.getLongExtra(LeadingCardsGridActivity.PICTURE_PARAM, -1L);

                cards.remove(cards.indexOf(pictureId));

                //TODO: send choice
                Toast.makeText(this, String.valueOf(pictureId), Toast.LENGTH_SHORT).show();
                break;
            }

            case VOTE_REQUEST: {
                long pictureId = data.getLongExtra(LeadingCardsGridActivity.PICTURE_PARAM, -1L);

                //TODO: send choice
                Toast.makeText(this, String.valueOf(pictureId), Toast.LENGTH_SHORT).show();
                break;
            }
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
                Thread.sleep(1000);
            } catch (Exception e) {}

            lead();

            try {
                Thread.sleep(3000);
            } catch (Exception e) {}

            chooseCard("name");

            try {
                Thread.sleep(3000);
            } catch (Exception e) {}

            vote("name");
        }
    }
}
