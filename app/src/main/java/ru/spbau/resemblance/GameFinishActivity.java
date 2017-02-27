package ru.spbau.resemblance;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

        View[] viewsArray = new View[playerNames.size() + 3];

        showResults(viewsArray, preparePlayersData(playerNames, finalScores, oldRatings, newRatings));
        showLastAssociation(viewsArray, (int)lastAnswer, playerNames.size() + 1);

        resultsList = (ListView) findViewById(R.id.resultsList);
        ViewArrayAdapter adapter = new ViewArrayAdapter(viewsArray);
        resultsList.setAdapter(adapter);
    }
    
    private ArrayList<PlayerResult> preparePlayersData(ArrayList<String> playerNames,
                                                       ArrayList<Integer> finalScores,
                                                       ArrayList<Integer> oldRatings,
                                                       ArrayList<Integer> newRatings) {
        ArrayList<PlayerResult> playerResults = new ArrayList<>();
        for (int i = 0; i < playerNames.size(); i++) {
            playerResults.add(new PlayerResult(playerNames.get(i), finalScores.get(i), 
                    oldRatings.get(i), newRatings.get(i)));
        }

        Collections.sort(playerResults, new Comparator<PlayerResult>() {
            @Override
            public int compare(PlayerResult o1, PlayerResult o2) {
                return o2.getScore() - o1.getScore();
            }
        });
        return playerResults;
    }
    
    private void showResults(View[] viewsArray, ArrayList<PlayerResult> playerResults) {
        viewsArray[0] = inflater.inflate(R.layout.results_header, resultsList);
        for (int i = 0; i < playerResults.size(); i++) {
            View playerResultView = inflater.inflate(R.layout.result_list_item, resultsList, false);
            ((TextView)playerResultView.findViewById(R.id.player)).setText(playerResults.get(i).getName());
            ((TextView)playerResultView.findViewById(R.id.finalScore))
                    .setText(playerResults.get(i).getScoreString());
            ((TextView)playerResultView.findViewById(R.id.oldRating))
                    .setText(playerResults.get(i).getOldRatingString());
            ((TextView)playerResultView.findViewById(R.id.newRating))
                    .setText(playerResults.get(i).getNewRatingString());
            viewsArray[i + 1] = playerResultView;
        }
    }

    private void showLastAssociation(View[] viewsArray, int answer, int pos) {
        viewsArray[pos] = new TextView(this);
        ((TextView)viewsArray[pos]).setText(R.string.game_finish_last_association);
        ((TextView)viewsArray[pos]).setTextAppearance(this,
                android.R.style.TextAppearance_DeviceDefault_Large);

        viewsArray[pos + 1] = ImageStorage.ImageWrapped.createById(answer)
                .getImageView(this);
        ((ImageView)viewsArray[pos + 1]).setAdjustViewBounds(true);
    }
    
    private class PlayerResult {
        private final String name;
        private final int score;
        private final int oldRating;
        private final int newRating;
        
        PlayerResult(String name, int score, int oldRating, int newRating) {
            this.name = name;
            this.score = score;
            this.oldRating = oldRating;
            this.newRating = newRating;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }

        public String getScoreString() {
            return String.valueOf(score);
        }

        public String getOldRatingString() {
            return String.valueOf(oldRating);
        }

        public String getNewRatingString() {
            return String.valueOf(newRating);
        }
    } 
}
