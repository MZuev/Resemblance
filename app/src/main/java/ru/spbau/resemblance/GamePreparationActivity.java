package ru.spbau.resemblance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class GamePreparationActivity extends AppCompatActivity implements
        Message.FriendGamePreparationListener {
    private ListView playersListView;
    private ArrayAdapter<String> playersAdapter;
    private ArrayList<String> players;
    private BroadcastReceiver receiver;
    //TODO: player removal
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_preparation);

        Message.setFriendGamePreparationListener(this);

        players = new ArrayList<String>();
        playersListView = (ListView)findViewById(R.id.preparationListOfPlayers);
        playersAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, players);
        playersListView.setAdapter(playersAdapter);

        receiver = new NewPlayerMessageReceiver();
        IntentFilter filter = new IntentFilter("ru.spbau.resemblance.NEW_FRIEND_GAME_PLAYER");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    public void onStartClick(View v) {
        Message.sendStartFriendGameMessage();

        Intent expectGame = new Intent(this, GameExpectationActivity.class);
        startActivity(expectGame);
    }

    public void onCancelClick(View v) {
        //TODO: game cancellation
    }

    public void onNewPlayer(String name) {
        players.add(name);

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("ru.spbau.resemblance.NEW_FRIEND_GAME_PLAYER"));
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    public class NewPlayerMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            playersAdapter.notifyDataSetChanged();
        }
    }
}
