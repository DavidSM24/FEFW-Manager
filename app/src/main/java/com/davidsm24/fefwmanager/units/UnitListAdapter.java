package com.davidsm24.fefwmanager.units;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.data.UnitClass;
import com.davidsm24.fefwmanager.databinding.ItemUnitBinding;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class UnitListAdapter extends ChoiceListAdapter<UnitLabel> {
    private final LayoutInflater inflater;
    private final int accent;
    @Nullable private final Consumer<UnitLabel> onRemove;
    private final UnitClassIcons classIcons = new UnitClassIcons();
    private Map<String, UnitClass> assignedClasses = Collections.emptyMap();
    private boolean canRemove;

    public UnitListAdapter(LayoutInflater inflater, @ColorInt int accent,
            @Nullable Consumer<UnitLabel> onRemove) {
        this.inflater = inflater;
        this.accent = accent;
        this.onRemove = onRemove;
    }

    void show(List<UnitLabel> units, Map<String, UnitClass> assignedClasses, boolean canRemove) {
        this.assignedClasses = assignedClasses;
        this.canRemove = canRemove;
        showChoices(units);
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
        bindClassIcon(row.recruitedClassIcon, assignedClasses.get(unit.id()));
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

    private void bindClassIcon(ImageView view, @Nullable UnitClass unitClass) {
        classIcons.bind(view, unitClass);
        String name = unitClass == null ? null : unitClass.name;
        view.setVisibility(name == null ? View.GONE : View.VISIBLE);
        view.setContentDescription(name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            view.setTooltipText(name);
        } else {
            view.setOnLongClickListener(name == null ? null : icon -> {
                Toast.makeText(icon.getContext(), name, Toast.LENGTH_SHORT).show();
                return true;
            });
            view.setLongClickable(name != null);
        }
    }
}
