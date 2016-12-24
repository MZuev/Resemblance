package ru.spbau.resemblance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class GamePreparationActivity extends AppCompatActivity implements
        Message.FriendGamePreparationListener, AdapterView.OnItemClickListener {
    private ListView playersListView;
    private ArrayAdapter<String> playersAdapter;
    private ArrayList<String> players;
    private BroadcastReceiver receiver;
    private static final String PLAYERS_LIST_UPDATE_MESSAGE =
            "ru.spbau.resemblance.FRIEND_GAME_PLAYERS_LIST_UPDATE";

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
        playersListView.setOnItemClickListener(this);

        receiver = new PlayersListUpdateMessageReceiver();
        IntentFilter filter = new IntentFilter(PLAYERS_LIST_UPDATE_MESSAGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    public void onStartClick(View v) {
        Message.sendStartFriendGameMessage();

        Intent expectGame = new Intent(this, GameExpectationActivity.class);
        startActivity(expectGame);
        finish();
    }

    public void onCancelClick(View v) {
        Message.sendCancelFriendGameMessage();
        finish();
    }

    public void onNewPlayer(String name) {
        players.add(name);

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(PLAYERS_LIST_UPDATE_MESSAGE));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        Message.unSetFriendGamePreparationListener();

        super.onPause();
    }

    public class PlayersListUpdateMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            playersAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DeletePlayerDialog dialog = new DeletePlayerDialog();
        dialog.setPlayer(position);
        dialog.setActivity(this);
        dialog.show(getSupportFragmentManager(), "DeletePlayerDialog");
    }

    @Override
    public void onBackPressed() {}

    private void removePlayer(int playerIndex) {
        Message.sendRemovePlayerMessage(players.get(playerIndex));
        players.remove(playerIndex);
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(PLAYERS_LIST_UPDATE_MESSAGE));
    }

    public static class DeletePlayerDialog extends DialogFragment {
        private int playerIndex;
        private GamePreparationActivity activity;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Удалить игрока?")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.removePlayer(playerIndex);
                        }
                    })
                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            return builder.create();
        }

        public void setPlayer(int playerIndex) {
            this.playerIndex = playerIndex;
        }

        public void setActivity(GamePreparationActivity activity) {
            this.activity = activity;
        }
    }
}
