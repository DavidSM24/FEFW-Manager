package com.davidsm24.fefwmanager.units;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davidsm24.fefwmanager.data.UnitClass;
import com.davidsm24.fefwmanager.databinding.ItemUnitClassBinding;

import java.util.HashMap;
import java.util.Map;

final class UnitClassListAdapter extends ChoiceListAdapter<UnitClass> {
    private final LayoutInflater inflater;
    private final Map<String, Bitmap> icons = new HashMap<>();

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
        Bitmap icon = icons.computeIfAbsent(unitClass.id, id ->
                BitmapFactory.decodeByteArray(unitClass.imagePng, 0, unitClass.imagePng.length));
        BitmapDrawable drawable = new BitmapDrawable(parent.getResources(), icon);
        drawable.setFilterBitmap(false);
        row.classIcon.setImageDrawable(drawable);
        return row.getRoot();
    }
}
