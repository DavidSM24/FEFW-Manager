package com.davidsm24.fefwmanager.units;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.databinding.ItemUnitBinding;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public final class UnitListAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final int accent;
    @Nullable private final Consumer<UnitLabel> onRemove;
    private List<UnitLabel> units = Collections.emptyList();
    private boolean canRemove;

    public UnitListAdapter(LayoutInflater inflater, @ColorInt int accent,
            @Nullable Consumer<UnitLabel> onRemove) {
        this.inflater = inflater;
        this.accent = accent;
        this.onRemove = onRemove;
    }

    public void show(List<UnitLabel> units, boolean canRemove) {
        this.units = units;
        this.canRemove = canRemove;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return units.size();
    }

    @Override
    public UnitLabel getItem(int position) {
        return units.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View recycled, ViewGroup parent) {
        ItemUnitBinding row;
        if (recycled == null) {
            row = ItemUnitBinding.inflate(inflater, parent, false);
            row.getRoot().setTag(row);
        } else {
            row = (ItemUnitBinding) recycled.getTag();
        }

        UnitLabel unit = getItem(position);
        String name = unit.name();
        row.unitName.setText(name);
        UnitPortraits.bind(row.unitPortrait, unit, accent);
        if (onRemove == null) {
            row.removeUnit.setVisibility(View.GONE);
        } else {
            row.removeUnit.setVisibility(View.VISIBLE);
            row.removeUnit.setEnabled(canRemove);
            row.removeUnit.setContentDescription(
                    parent.getContext().getString(R.string.remove_unit_named, name));
            row.removeUnit.setOnClickListener(view -> onRemove.accept(unit));
        }
        return row.getRoot();
    }
}
