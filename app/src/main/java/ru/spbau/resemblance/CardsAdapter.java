package ru.spbau.resemblance;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class CardsAdapter extends BaseAdapter {
    private Context context = null;
    private ImageStorage.ImageWrapped[] cards = null;
    private int cardSide;

    public CardsAdapter(Context context, ImageStorage.ImageWrapped[] cards, int cardSide) {
        this.context = context;
        this.cards = cards;
        this.cardSide = cardSide;
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
        //return cards[position].getImageView(context);
        ImageView view = cards[position].getImageView(context);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setLayoutParams(new GridView.LayoutParams(cardSide, cardSide));

        return view;
    }
}