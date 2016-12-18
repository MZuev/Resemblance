package ru.spbau.resemblance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class GamePreparationActivity extends AppCompatActivity implements
        Message.FriendGamePreparationListener {
    private ListView playersListView;
    private ArrayAdapter<String> playersAdapter;
    private ArrayList<String> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_preparation);

        Message.setFriendGamePreparationListener(this);

        players = new ArrayList<String>();
        playersListView = (ListView)findViewById(R.id.preparationListOfPlayers);
        playersAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, players);
        playersListView.setAdapter(playersAdapter);
    }

    public void onStartClick(View v) {
        Message.sendStartFriendGameMessage();

        Intent expectGame = new Intent(this, GameExpectationActivity.class);
        startActivity(expectGame);
    }

    public void onCancelClick(View v) {

    }

    public void onNewPlayer(String name) {
        players.add(name);
        //playersAdapter.notifyDataSetChanged();
        //TODO: eventually find a way to update information after messages are received
    }
}
