package com.davidsm24.fefwmanager.units;

import android.widget.BaseAdapter;

import java.util.Collections;
import java.util.List;

abstract class ChoiceListAdapter<T> extends BaseAdapter {
    private List<T> choices = Collections.emptyList();

    final void showChoices(List<T> choices) {
        this.choices = choices;
        notifyDataSetChanged();
    }

    @Override
    public final int getCount() {
        return choices.size();
    }

    @Override
    public final T getItem(int position) {
        return choices.get(position);
    }

    @Override
    public final long getItemId(int position) {
        return position;
    }
}
