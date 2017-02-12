package ru.spbau.resemblance;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class GameFinishActivity extends AppCompatActivity {
    protected static final String LAST_ANSWER_PARAM = "last_answer";
    protected static final String FINAL_SCORES_PARAM = "final_scores";
    protected static final String OLD_RATINGS_PARAM = "old_ratings";
    protected static final String NEW_RATINGS_PARAM = "new_ratings";
    protected static final String PLAYERS_NAMES_PARAM = "players_names";

    private ListView resultsList;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_finish);

        Intent callingIntent = getIntent();
        ArrayList<String> playerNames = callingIntent.getStringArrayListExtra(PLAYERS_NAMES_PARAM);
        ArrayList<Integer> finalScores = callingIntent.getIntegerArrayListExtra(FINAL_SCORES_PARAM);
        ArrayList<Integer> oldRatings = callingIntent.getIntegerArrayListExtra(OLD_RATINGS_PARAM);
        ArrayList<Integer> newRatings = callingIntent.getIntegerArrayListExtra(NEW_RATINGS_PARAM);
        long lastAnswer = callingIntent.getLongExtra(LAST_ANSWER_PARAM, -1);

        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setTitle(R.string.game_finish_title);

        View[] viewsArray = new View[playerNames.size() * 2 + 4];

        showScores(viewsArray, playerNames, finalScores);
        showRatings(viewsArray, playerNames, oldRatings, newRatings);

        //Showing the last association picture
        viewsArray[playerNames.size() * 2 + 2] = new TextView(this);
        ((TextView)viewsArray[playerNames.size() * 2 + 2]).setText(R.string.game_finish_last_association);
        ((TextView)viewsArray[playerNames.size() * 2 + 2]).setTextAppearance(this,
                android.R.style.TextAppearance_DeviceDefault_SearchResult_Title);
        ((TextView)viewsArray[playerNames.size() * 2 + 2]).setTextSize(25);

        viewsArray[playerNames.size() * 2 + 3] = ImageStorage.ImageWrapped.createById((int)lastAnswer)
                .getImageView(this);
        ((ImageView)viewsArray[playerNames.size() * 2 + 3]).setAdjustViewBounds(true);

        resultsList = (ListView) findViewById(R.id.resultsList);
        ViewArrayAdapter adapter = new ViewArrayAdapter(viewsArray);
        resultsList.setAdapter(adapter);
    }

    private void showScores(View[] viewsArray, ArrayList<String> playerNames, ArrayList<Integer> finalScores) {
        viewsArray[0] = new TextView(this);
        ((TextView)viewsArray[0]).setText(R.string.game_finish_final_score);
        ((TextView)viewsArray[0]).setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault_SearchResult_Title);
        ((TextView)viewsArray[0]).setTextSize(25);
        for (int i = 0; i < playerNames.size(); i++) {
            View playerScoreView = inflater.inflate(R.layout.score_list_item, resultsList, false);
            TextView playerView = (TextView) playerScoreView.findViewById(R.id.player);
            TextView scoreView = (TextView) playerScoreView.findViewById(R.id.score);

            playerView.setText(playerNames.get(i));
            scoreView.setText(String.valueOf(finalScores.get(i)));
            viewsArray[i + 1] = playerScoreView;
        }
    }

    private void showRatings(View[] viewsArray, ArrayList<String> playerNames,
                             ArrayList<Integer> oldRatings, ArrayList<Integer> newRatings) {
        viewsArray[playerNames.size() + 1] = new TextView(this);
        ((TextView)viewsArray[playerNames.size() + 1]).setText(R.string.game_finish_ratings);
        ((TextView)viewsArray[playerNames.size() + 1]).setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault_SearchResult_Title);
        ((TextView)viewsArray[playerNames.size() + 1]).setTextSize(25);
        for (int i = 0; i < playerNames.size(); i++) {
            View playerRatingView = inflater.inflate(R.layout.rating_list_item, resultsList, false);
            ((TextView)playerRatingView.findViewById(R.id.player)).setText(playerNames.get(i));
            ((TextView)playerRatingView.findViewById(R.id.oldRating))
                    .setText(String.valueOf(oldRatings.get(i)));
            ((TextView)playerRatingView.findViewById(R.id.newRating))
                    .setText(String.valueOf(newRatings.get(i)));

            viewsArray[playerNames.size() + 2 + i] = playerRatingView;
        }
    }
}
