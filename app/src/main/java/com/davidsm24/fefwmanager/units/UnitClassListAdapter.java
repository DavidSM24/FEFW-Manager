package com.davidsm24.fefwmanager.units;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davidsm24.fefwmanager.data.UnitClass;
import com.davidsm24.fefwmanager.databinding.ItemUnitClassBinding;

final class UnitClassListAdapter extends ChoiceListAdapter<UnitClass> {
    private final LayoutInflater inflater;
    private final UnitClassIcons icons = new UnitClassIcons();

    UnitClassListAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getView(int position, View recycled, ViewGroup parent) {
        ItemUnitClassBinding row;
        if (recycled == null) {
            row = ItemUnitClassBinding.inflate(inflater, parent, false);
            row.getRoot().setTag(row);
        } else {
            row = (ItemUnitClassBinding) recycled.getTag();
        }

        UnitClass unitClass = getItem(position);
        row.className.setText(unitClass.name);
        icons.bind(row.classIcon, unitClass);
        return row.getRoot();
    }
}
