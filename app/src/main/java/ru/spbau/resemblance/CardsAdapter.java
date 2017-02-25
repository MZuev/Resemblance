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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CardsAdapter extends BaseAdapter {
    private final Activity activity;
    private final List<ImageStorage.ImageWrapped> cards;
    private final int cardSide;
    private final List<ImageView> views;
    private GridView.LayoutParams sizeLayoutParams;

    public CardsAdapter(final Activity activity, final List<ImageStorage.ImageWrapped> cards, int cardSide) {
        this.activity = activity;
        this.cards = cards;
        this.cardSide = cardSide;

        //First we fill the grid with placeholders
        ImageView placeholder = new ImageView(activity);
        placeholder.setImageResource(android.R.drawable.ic_menu_report_image);
        placeholder.setScaleType(ImageView.ScaleType.CENTER_CROP);
        sizeLayoutParams = new GridView.LayoutParams(CardsAdapter.this.cardSide,
                CardsAdapter.this.cardSide);
        placeholder.setLayoutParams(sizeLayoutParams);
        views = new ArrayList<>();
        for (int i = 0; i < cards.size(); i++) {
            views.add(placeholder);
        }

        //Then we replace them with real pictures
        //This is made for performance improvements
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < cards.size(); i++) {
                    //Setting picture from file and its parameters
                    try {
                        ImageView view = cards.get(i).getPreview(CardsAdapter.this.activity,
                                CardsAdapter.this.cardSide);
                        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        view.setLayoutParams(sizeLayoutParams);
                        views.set(i, view);
                    } catch (FileNotFoundException e) {
                        Log.d("CardsAdapter", "File not found: " + cards.get(i).getUriImage());
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
        return cards.size();
    }

    @Override
    public long getItemId(int position) {
        return cards.get(position).getIdImage();
    }

    @Override
    public Object getItem(int position) {
        return cards.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return views.get(position);
    }

    public void addImage(ImageStorage.ImageWrapped image) {
        try{
            cards.add(image);
            ImageView view = image.getPreview(activity, cardSide);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setLayoutParams(sizeLayoutParams);
            views.add(view);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CardsAdapter.this.notifyDataSetChanged();
                }
            });
        } catch (FileNotFoundException e) {
            Log.d("CardsAdapter", "File not found: " + image.getUriImage());
        }
    }
}