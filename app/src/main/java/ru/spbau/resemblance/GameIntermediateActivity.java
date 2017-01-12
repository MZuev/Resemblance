package ru.spbau.resemblance;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class GameIntermediateActivity extends AppCompatActivity implements Message.GameMessageListener {
    public static final String ROUNDS_NUMBER_PARAM = "rounds_number";
    public static final String PLAYERS_NUMBER_PARAM = "players_number";
    public static final String PLAYERS_NAMES_PARAM = "players_names";
    public static final String EXPECTATION_TIME_PARAM = "expectation_time";
    public static final int LEADING_ASSOCIATION_REQUEST = 1;
    public static final int CHOICE_REQUEST = 2;
    public static final int VOTE_REQUEST = 3;

    private final ArrayList<Long> cards = new ArrayList<>();
    private int roundsNumber = -1;
    private int currentRound = -1;
    private TextView roundText = null;
    private TextView scoreText = null;
    private ImageView answerView = null;
    private int playersNumber = -1;
    private ArrayList<String> playersNames = null;
    private int[] scores = null;
    private long answer = -1;
    private long expectationTime;

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
        expectationTime = callingIntent.getLongExtra(EXPECTATION_TIME_PARAM, -1);
        scores = new int[playersNumber];

        roundText = (TextView)findViewById(R.id.intermediateRoundText);
        scoreText = (TextView)findViewById(R.id.intermediateScoreText);
        answerView = (ImageView)findViewById(R.id.intermediateAnswerView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateScreen();
    }

    @Override
    public void onLeadRequest() {
        Intent lead = new Intent(this, LeadingCardsGridActivity.class);
        lead.putExtra(LeadingCardsGridActivity.OUR_CARDS_PARAM, getCardsArr());
        startActivityForResult(lead, LEADING_ASSOCIATION_REQUEST);
    }

    @Override
    public void onChoiceRequest(String association) {
        Intent choose = new Intent(this, CardPickerActivity.class);
        choose.putExtra(CardPickerActivity.SUGGESTION_PARAM,
                String.format(getString(R.string.game_association_suggestion), association));
        choose.putExtra(CardPickerActivity.TITLE_PARAM, getString(R.string.choice_title));
        choose.putExtra(CardPickerActivity.OUR_CARDS_PARAM, getCardsArr());
        choose.putExtra(CardPickerActivity.EXPECTATION_TIME_PARAM, expectationTime);
        startActivityForResult(choose, CHOICE_REQUEST);
    }

    @Override
    public void onVoteRequest(String association, long[] candidates) {
        Intent vote = new Intent(this, CardPickerActivity.class);
        vote.putExtra(CardPickerActivity.SUGGESTION_PARAM,
                String.format(getString(R.string.game_association_suggestion), association));
        vote.putExtra(CardPickerActivity.TITLE_PARAM, getString(R.string.vote_title));
        vote.putExtra(CardPickerActivity.OUR_CARDS_PARAM, candidates);
        vote.putExtra(CardPickerActivity.EXPECTATION_TIME_PARAM, expectationTime);
        startActivityForResult(vote, VOTE_REQUEST);
    }

    @Override
    public void onSendCard(long card) {
        synchronized (cards) {
            cards.add(card);
        }
    }

    @Override
    public void onRoundEnd(long leadersAssociation, int[] scores){
        this.scores = scores;
        answer = leadersAssociation;
        currentRound++;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateScreen();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        long pictureId = data.getLongExtra(LeadingCardsGridActivity.PICTURE_PARAM, -1L);
        if (pictureId >= 0) {
            switch (requestCode) {
                case LEADING_ASSOCIATION_REQUEST: {
                    //Now we need to send the association and remove the card fom our cards list.
                    String association = data.getStringExtra(LeadingCardsGridActivity.ASSOCIATION_PARAM);

                    synchronized (cards) {
                        cards.remove(pictureId);
                    }

                    Message.sendLeadAssociationMessage(pictureId, association);
                    break;
                }

                case CHOICE_REQUEST: {
                    //Sending player's choice and removing the chosen card.
                    synchronized (cards) {
                        cards.remove(cards.indexOf(pictureId));
                    }

                    Message.sendChoiceMessage(pictureId);
                    break;
                }

                case VOTE_REQUEST: {
                    Message.sendVoteMessage(pictureId);
                    break;
                }
            }
        }
    }

    private long[] getCardsArr() {
        long[] cardsArr = new long[cards.size()];
        synchronized (cards) {
            for (int i = 0; i < cards.size(); i++) {
                cardsArr[i] = cards.get(i);
            }
        }
        return cardsArr;
    }

    private void updateScreen() {
        roundText.setText(String.format(getString(R.string.game_round_text),
                currentRound + "/" + roundsNumber));
        StringBuilder scoresText = new StringBuilder();
        scoresText.append(getString(R.string.game_score_prefix));
        for (int i = 0; i < playersNumber; i++) {
            scoresText.append("\n");
            scoresText.append(playersNames.get(i));
            scoresText.append(" - ");
            scoresText.append(scores[i]);
        }
        scoreText.setText(scoresText.toString());

        if (answer != -1) {
            ImageStorage.ImageWrapped answerPic = ImageStorage.ImageWrapped.createById((int)answer);
            answerView.setImageURI(Uri.parse(answerPic.getUriImage()));
        }
    }

    @Override
    protected void onDestroy() {
        Message.unsetGameListener();

        super.onDestroy();
    }

    @Override
    public void onGameFinish(long lastAnswer, ArrayList<Integer> finalScores,
                             ArrayList<Integer> oldRatings, ArrayList<Integer> newRatings) {
        Intent showGameFinish = new Intent(this, GameFinishActivity.class);
        showGameFinish.putExtra(GameFinishActivity.LAST_ANSWER_PARAM, lastAnswer);
        showGameFinish.putIntegerArrayListExtra(GameFinishActivity.FINAL_SCORES_PARAM, finalScores);
        showGameFinish.putIntegerArrayListExtra(GameFinishActivity.OLD_RATINGS_PARAM, oldRatings);
        showGameFinish.putIntegerArrayListExtra(GameFinishActivity.NEW_RATINGS_PARAM, newRatings);
        showGameFinish.putStringArrayListExtra(GameFinishActivity.PLAYERS_NAMES_PARAM, playersNames);
        startActivity(showGameFinish);

        finish();
    }

    public void onMessageButtonClick(View v) {
        Intent chatIntent = new Intent(this, ChatActivity.class);
        startActivity(chatIntent);
    }
}
