package ru.spbau.resemblance;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
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
    private int playersNumber = -1;
    private ArrayList<String> playersNames = null;
    private int[] scores = null;
    private long answer = -1;
    private long expectationTime;
    private LayoutInflater inflater;
    private ListView stateList;
    private ViewArrayAdapter adapter;

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

        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        stateList = (ListView) findViewById(R.id.gameStateList);

        View[] listViews = new View[playersNumber + 2];
        updateInfo(listViews);

        adapter = new ViewArrayAdapter(listViews);
        stateList.setAdapter(adapter);
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
        View[] listViews = new View[playersNumber + 3];
        updateInfo(listViews);
        listViews[playersNumber + 2] = ImageStorage.ImageWrapped.createById((int)answer).getImageView(this);
        ((ImageView)listViews[playersNumber + 2]).setScaleType(ImageView.ScaleType.CENTER);
        ((ImageView)listViews[playersNumber + 2]).setAdjustViewBounds(true);
        adapter.setArray(listViews);
        adapter.notifyDataSetChanged();
    }

    private void updateInfo(View[] listViews) {
        listViews[0] = new TextView(this);
        ((TextView)listViews[0]).setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault_Large);
        ((TextView)listViews[0]).setText(String.format(getString(R.string.game_round_text),
                currentRound + "/" + roundsNumber));

        listViews[1] = new TextView(this);
        ((TextView)listViews[1]).setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault_Large);
        ((TextView)listViews[1]).setText(R.string.game_score_prefix);

        for (int i = 0; i < playersNumber; i++) {
            View playerScoreView = inflater.inflate(R.layout.score_list_item, stateList, false);
            ((TextView)playerScoreView.findViewById(R.id.player)).setText(playersNames.get(i));
            ((TextView)playerScoreView.findViewById(R.id.score)).setText(String.valueOf(scores[i]));
            listViews[i + 2] = playerScoreView;
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
        Chat.clear();
        Intent showGameFinish = new Intent(this, GameFinishActivity.class);
        showGameFinish.putExtra(GameFinishActivity.LAST_ANSWER_PARAM, lastAnswer);
        showGameFinish.putIntegerArrayListExtra(GameFinishActivity.FINAL_SCORES_PARAM, finalScores);
        showGameFinish.putIntegerArrayListExtra(GameFinishActivity.OLD_RATINGS_PARAM, oldRatings);
        showGameFinish.putIntegerArrayListExtra(GameFinishActivity.NEW_RATINGS_PARAM, newRatings);
        showGameFinish.putStringArrayListExtra(GameFinishActivity.PLAYERS_NAMES_PARAM, playersNames);
        startActivity(showGameFinish);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent chatIntent = new Intent(this, ChatActivity.class);
        startActivity(chatIntent);
        return true;
    }
}