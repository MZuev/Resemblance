package ru.spbau.resemblance;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

class ViewArrayAdapter extends BaseAdapter {
    private View[] views;

    ViewArrayAdapter(View[] views) {
        this.views = views;
    }

    public void setArray(View[] views) {
        this.views = views;
    }

    @Override
    public int getCount() {
        return views.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return views[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
}
