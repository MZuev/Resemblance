package ru.spbau.resemblance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class GamePreparationActivity extends AppCompatActivity implements
        Message.FriendGamePreparationListener, AdapterView.OnItemClickListener {
    private static final int MIN_PLAYERS_NUMBER = 3;
    private ArrayAdapter<String> playersAdapter;
    private ArrayList<String> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_preparation);

        Message.setFriendGamePreparationListener(this);

        players = new ArrayList<>();
        ListView playersListView = (ListView) findViewById(R.id.preparationListOfPlayers);
        playersAdapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, players);
        playersListView.setAdapter(playersAdapter);
        playersListView.setOnItemClickListener(this);
    }

    public void onStartClick(View v) {
        if (players.size() + 1 >= MIN_PLAYERS_NUMBER) {
            Message.sendStartFriendGameMessage();

            Intent expectGame = new Intent(this, GameExpectationActivity.class);
            startActivity(expectGame);
            finish();
        } else {
            Toast.makeText(this, R.string.not_enough_players, Toast.LENGTH_SHORT).show();
        }
    }

    public void onCancelClick(View v) {
        Message.sendCancelFriendGameMessage();
        finish();
    }

    private void updateList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playersAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onNewPlayer(String name) {
        players.add(name);
        updateList();
    }

    @Override
    public void onGonePlayer(String name) {
        players.remove(name);
        updateList();
    }

    @Override
    public void onPause() {
        Message.unSetFriendGamePreparationListener();

        super.onPause();
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
        updateList();
    }

    public static class DeletePlayerDialog extends DialogFragment {
        private int playerIndex;
        private GamePreparationActivity activity;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.delete_player)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.removePlayer(playerIndex);
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
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
