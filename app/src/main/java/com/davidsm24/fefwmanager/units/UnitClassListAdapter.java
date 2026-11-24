package com.davidsm24.fefwmanager.units;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.data.UnitClass;
import com.davidsm24.fefwmanager.databinding.ItemUnitClassBinding;

import java.util.function.Consumer;

final class UnitClassListAdapter extends ChoiceListAdapter<UnitClass> {
    private final LayoutInflater inflater;
    private final Consumer<UnitClass> onClassDetails;
    private final UnitClassIcons icons = new UnitClassIcons();

    UnitClassListAdapter(LayoutInflater inflater, Consumer<UnitClass> onClassDetails) {
        this.inflater = inflater;
        this.onClassDetails = onClassDetails;
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
        row.classIcon.cancelHold();
        icons.bind(row.classIcon, unitClass);
        row.classIcon.setContentDescription(parent.getContext().getString(
                R.string.select_class_description, unitClass.name));
        View.OnClickListener selectClass = selectItem(parent, row.getRoot());
        row.getRoot().setOnClickListener(selectClass);
        row.classIcon.setOnClickListener(selectClass);
        row.classIcon.setOnLongClickListener(icon -> {
            onClassDetails.accept(unitClass);
            return true;
        });
        return row.getRoot();
    }
}
