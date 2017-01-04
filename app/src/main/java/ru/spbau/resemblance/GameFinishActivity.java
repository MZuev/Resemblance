package ru.spbau.resemblance;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class GameFinishActivity extends AppCompatActivity {
    protected static final String LAST_ANSWER_PARAM = "last_answer";
    protected static final String FINAL_SCORES_PARAM = "final_scores";
    protected static final String OLD_RATINGS_PARAM = "old_ratings";
    protected static final String NEW_RATINGS_PARAM = "new_ratings";
    protected static final String PLAYERS_NAMES_PARAM = "players_names";

    private TextView scoresTextView;
    private TextView ratingsTextView;
    private ArrayList<String> playerNames;
    private ArrayList<Integer> finalScores;
    private ArrayList<Integer> oldRatings;
    private ArrayList<Integer> newRatings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_finish);

        scoresTextView = (TextView) findViewById(R.id.gameFinishScoreText);
        ratingsTextView = (TextView) findViewById(R.id.gameFinishRatingText);
        ImageView lastAnswerView = (ImageView) findViewById(R.id.gameFinishLastCard);

        Intent callingIntent = getIntent();
        playerNames = callingIntent.getStringArrayListExtra(PLAYERS_NAMES_PARAM);
        finalScores = callingIntent.getIntegerArrayListExtra(FINAL_SCORES_PARAM);
        oldRatings = callingIntent.getIntegerArrayListExtra(OLD_RATINGS_PARAM);
        newRatings = callingIntent.getIntegerArrayListExtra(NEW_RATINGS_PARAM);
        long lastAnswer = callingIntent.getLongExtra(LAST_ANSWER_PARAM, -1);

        showScores();
        showRatings();
        ImageStorage.ImageWrapped lastAnswerPic = ImageStorage.ImageWrapped.createById((int)lastAnswer);
        lastAnswerView.setImageURI(Uri.parse(lastAnswerPic.getUriImage()));

        setTitle(R.string.game_finish_title);
    }

    private void showScores() {
        StringBuilder scoresText = new StringBuilder();
        for (int i = 0; i < playerNames.size(); i++) {
            if (i > 0) {
                scoresText.append("\n");
            }
            scoresText.append(playerNames.get(i));
            scoresText.append(": ");
            scoresText.append(finalScores.get(i));
        }
        scoresTextView.setText(scoresText.toString());
    }

    private void showRatings() {
        StringBuilder ratingsText = new StringBuilder();
        for (int i = 0; i < playerNames.size(); i++) {
            if (i > 0) {
                ratingsText.append("\n");
            }
            ratingsText.append(playerNames.get(i));
            ratingsText.append(": ");
            ratingsText.append(oldRatings.get(i));
            ratingsText.append(" -> ");
            ratingsText.append(newRatings.get(i));
        }
        ratingsTextView.setText(ratingsText.toString());
    }
}
