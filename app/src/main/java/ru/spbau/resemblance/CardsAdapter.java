package ru.spbau.resemblance;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.util.Arrays;

public class CardsAdapter extends BaseAdapter {
    private final Activity activity;
    private final ImageStorage.ImageWrapped[] cards;
    private final int cardSide;
    private final ImageView[] views;

    public CardsAdapter(final Activity activity, final ImageStorage.ImageWrapped[] cards, int cardSide) {
        this.activity = activity;
        this.cards = cards;
        this.cardSide = cardSide;
        views = new ImageView[cards.length];

        //First we fill the grid with placeholders
        ImageView placeholder = new ImageView(activity);
        placeholder.setImageResource(android.R.drawable.ic_menu_report_image);
        Arrays.fill(views, placeholder);
        //Then we replace them with real pictures
        //This is made for performance improvements
        new Thread(new Runnable() {
            @Override
            public void run() {
                GridView.LayoutParams sizeLayoutParams = new GridView.LayoutParams(CardsAdapter.this.cardSide,
                        CardsAdapter.this.cardSide);
                for (int i = 0; i < cards.length; i++) {
                    //Setting picture from file and its parameters
                    try {
                        views[i] = cards[i].getPreview(CardsAdapter.this.activity, CardsAdapter.this.cardSide);
                        views[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
                        views[i].setLayoutParams(sizeLayoutParams);
                    } catch (FileNotFoundException e) {
                        Log.d("CardsAdapter", "File not found: " + cards[i].getUriImage());
                    }
                    //Updating screen
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CardsAdapter.this.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public int getCount() {
        return cards.length;
    }

    @Override
    public long getItemId(int position) {
        return cards[position].getIdImage();
    }

    @Override
    public Object getItem(int position) {
        return cards[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return views[position];
    }
}