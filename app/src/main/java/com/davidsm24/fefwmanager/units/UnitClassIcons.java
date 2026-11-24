package com.davidsm24.fefwmanager.units;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.davidsm24.fefwmanager.data.UnitClass;

import java.util.HashMap;
import java.util.Map;

final class UnitClassIcons {
    private final Map<String, Bitmap> icons = new HashMap<>();

    void bind(ImageView view, @Nullable UnitClass unitClass) {
        if (unitClass == null) {
            view.setImageDrawable(null);
            return;
        }

        Bitmap icon = icons.computeIfAbsent(unitClass.id, id ->
                BitmapFactory.decodeByteArray(unitClass.imagePng, 0, unitClass.imagePng.length));
        BitmapDrawable drawable = new BitmapDrawable(view.getResources(), icon);
        drawable.setFilterBitmap(false);
        view.setImageDrawable(drawable);
    }
}
