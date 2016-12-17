package ru.spbau.resemblance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class GameIntermediateActivity extends AppCompatActivity implements Message.GameMessageListener {
    public static final String OUR_CARDS_PARAM = "our_cards";
    public static final String SUGGESTION_PARAM = "suggestion";
    public static final String ROUNDS_NUMBER_PARAM = "rounds_number";
    public static final String PLAYERS_NUMBER_PARAM = "players_number";
    public static final String PLAYERS_NAMES_PARAM = "players_names";
    public static final int LEADING_ASSOCIATION_REQUEST = 1;
    public static final int CHOICE_REQUEST = 2;
    public static final int VOTE_REQUEST = 3;

    private static final String CHOOSE_SUGGESTION = "Ваша карта. Ассоциация: ";
    private static final String VOTE_SUGGESTION = "Голосование. Ассоциация: ";
    private static final String ROUND_PREFIX = "Раунд: ";
    private static final String SCORE_PREFIX = "Счёт:";

    private ArrayList<Long> cards = new ArrayList<>();
    private int roundsNumber = -1;
    private int currentRound = -1;
    private TextView roundText = null;
    private TextView scoreText = null;
    private int playersNumber = -1;
    private ArrayList<String> playersNames = null;
    private ArrayList<Integer> scores = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_intermediate);

        Message.setGameListener(this);

        Intent callingIntent = getIntent();
        roundsNumber = callingIntent.getIntExtra(ROUNDS_NUMBER_PARAM, -1);
        currentRound = 1;
        playersNumber = callingIntent.getIntExtra(PLAYERS_NUMBER_PARAM, -1);
        playersNames = callingIntent.getStringArrayListExtra(PLAYERS_NAMES_PARAM);
        scores = new ArrayList<>();
        for (int i = 0; i < playersNumber; i++) {
            scores.add(0);
        }

        roundText = (TextView)findViewById(R.id.intermediateRoundText);
        scoreText = (TextView)findViewById(R.id.intermediateScoreText);
    }

    @Override
    protected void onResume() {
        super.onResume();

        roundText.setText(ROUND_PREFIX + currentRound + "/" + roundsNumber);
        String scoresText = SCORE_PREFIX;
        for (int i = 0; i < playersNumber; i++) {
            scoresText += "\n" + playersNames.get(i) + " - " +scores.get(i);
        }
        scoreText.setText(scoresText);
    }

    @Override
    public void onLeadRequest() {
        Intent lead = new Intent(this, LeadingCardsGridActivity.class);
        lead.putExtra(OUR_CARDS_PARAM, getCardsArr());
        startActivityForResult(lead, LEADING_ASSOCIATION_REQUEST);
    }

    @Override
    public void onChoiceRequest(String association) {
        Intent choose = new Intent(this, CardPickerActivity.class);
        choose.putExtra(SUGGESTION_PARAM, CHOOSE_SUGGESTION + association);
        choose.putExtra(OUR_CARDS_PARAM, getCardsArr());
        startActivityForResult(choose, CHOICE_REQUEST);
    }

    @Override
    public void onVoteRequest(String association, long[] candidates) {
        Intent vote = new Intent(this, CardPickerActivity.class);
        vote.putExtra(SUGGESTION_PARAM, VOTE_SUGGESTION + association);
        vote.putExtra(OUR_CARDS_PARAM, candidates);
        startActivityForResult(vote, VOTE_REQUEST);
    }

    @Override
    public void onSendCard(long card) {
        synchronized (cards) {
            cards.add(card);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        long pictureId = data.getLongExtra(LeadingCardsGridActivity.PICTURE_PARAM, -1L);
        switch (requestCode) {
            case LEADING_ASSOCIATION_REQUEST: {
                //Now we need to send the association and remove the card fom our cards list.
                String association = data.getStringExtra(LeadingCardsGridActivity.ASSOCIATION_PARAM);

                cards.remove(cards.indexOf(pictureId));

                Message.sendLeadAssociationMessage(pictureId, association);
                break;
            }

            case CHOICE_REQUEST: {
                //Sending player's choice and removing the chosen card.
                cards.remove(cards.indexOf(pictureId));

                Message.sendChoiceMessage(pictureId);
                break;
            }

            case VOTE_REQUEST: {
                Message.sendVoteMessage(pictureId);
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
}
