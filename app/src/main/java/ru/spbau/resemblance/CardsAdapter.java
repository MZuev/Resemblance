package ru.spbau.resemblance;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.FileNotFoundException;

public class CardsAdapter extends BaseAdapter {
    private final Context context;
    private final ImageStorage.ImageWrapped[] cards;
    private final int cardSide;
    private final ImageView[] views;

    public CardsAdapter(Context context, ImageStorage.ImageWrapped[] cards, int cardSide) {
        this.context = context;
        this.cards = cards;
        this.cardSide = cardSide;
        views = new ImageView[cards.length];
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
        if (views[position] == null) {
            try {
                views[position] = cards[position].getPreview(context, cardSide);
            } catch (FileNotFoundException e) {
                views[position] = new ImageView(context);
                views[position].setImageResource(R.drawable.empty);
            }
            views[position].setScaleType(ImageView.ScaleType.CENTER_CROP);
            views[position].setLayoutParams(new GridView.LayoutParams(cardSide, cardSide));
        }

        return views[position];
    }
}