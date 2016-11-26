package ru.spbau.resemblance;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CardsAdapter extends BaseAdapter {
    private Context context = null;
    private ImageStorage.ImageWrapped[] cards = null;

    public CardsAdapter(Context context, ImageStorage.ImageWrapped[] cards) {
        this.context = context;
        this.cards = cards;
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
        return cards[position].getImageView(context);
    }
}