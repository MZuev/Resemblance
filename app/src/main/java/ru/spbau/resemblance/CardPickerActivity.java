package ru.spbau.resemblance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CardPickerActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static final String PICTURE_PARAM = "picture_id";
    public static final String OUR_CARDS_PARAM = "our_cards";
    public static final String SUGGESTION_PARAM = "suggestion";
    public static final String TITLE_PARAM = "title";
    public static final String EXPECTATION_TIME_PARAM = "expectation_time";

    private final static int COLUMNS_NUMBER = 2;
    private final static int CARD_REQUEST = 1;
    private final static long SECOND = 1000;

    private TextView timeView;
    private Timer timer;
    private long deadline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_picker);

        GridView grid = (GridView) findViewById(R.id.cardPickedGrid);
        TextView suggestion = (TextView) findViewById(R.id.cardPickerText);
        timeView = (TextView) findViewById(R.id.cardPickerTimeView);

        Intent callingIntent = getIntent();

        suggestion.setText(callingIntent.getStringExtra(SUGGESTION_PARAM));
        setTitle(callingIntent.getStringExtra(TITLE_PARAM));
        long timeout = callingIntent.getLongExtra(EXPECTATION_TIME_PARAM, -1);

        List <Long> cardIds = new ArrayList<>();
        long[] cardsArr = callingIntent.getLongArrayExtra(OUR_CARDS_PARAM);
        for(long card: cardsArr) {
            cardIds.add(card);
        }

        ImageStorage.ImageWrapped[] cardViews = new ImageStorage.ImageWrapped[cardIds.size()];
        for (int i = 0; i < cardIds.size(); i++) {
            cardViews[i] = ImageStorage.ImageWrapped.createById((int)(long)cardIds.get(i));
        }

        ListAdapter cardsAdapter = new CardsAdapter(this, cardViews,
                getResources().getDisplayMetrics().widthPixels / COLUMNS_NUMBER);
        grid.setAdapter(cardsAdapter);
        grid.setNumColumns(COLUMNS_NUMBER);

        grid.setOnItemClickListener(this);

        timer = new Timer();
        deadline = System.currentTimeMillis() + timeout;
        timer.scheduleAtFixedRate(new TimeUpdater(), 10, 200);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent showPicture = new Intent(this, CardViewerActivity.class);
        showPicture.putExtra(LeadingAssociationActivity.IMAGE_PARAM, id);
        startActivityForResult(showPicture, CARD_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        long pictureId = data.getLongExtra(PICTURE_PARAM, -1L);
        if (pictureId >= 0) {
            Intent ret = new Intent();
            ret.putExtra(LeadingCardsGridActivity.PICTURE_PARAM, pictureId);
            setResult(RESULT_OK, ret);
            timer.cancel();
            finish();
        }
    }

    @Override
    public void onBackPressed() {}

    private class TimeUpdater extends TimerTask {
        final int MINUTE = 60;

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (System.currentTimeMillis() > deadline) {
                        Intent ret = new Intent();
                        ret.putExtra(LeadingCardsGridActivity.PICTURE_PARAM, -1L);
                        setResult(RESULT_OK, ret);
                        timer.cancel();
                        finish();
                    } else {
                        long rest = (deadline - System.currentTimeMillis()) / SECOND;
                        timeView.setText(String.format(getString(R.string.card_picker_time), rest / MINUTE + ":" + rest % MINUTE));
                    }
                }
            });
        }
    }
}
