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
    @Nullable private final Consumer<UnitLabel> onChangeClass;
    @Nullable private final Consumer<UnitLabel> onRemove;
    private final UnitClassIcons classIcons = new UnitClassIcons();
    private Map<String, UnitClass> assignedClasses = Collections.emptyMap();
    private boolean canEdit;

    public UnitListAdapter(LayoutInflater inflater, @ColorInt int accent,
            @Nullable Consumer<UnitLabel> onChangeClass, @Nullable Consumer<UnitLabel> onRemove) {
        this.inflater = inflater;
        this.accent = accent;
        this.onChangeClass = onChangeClass;
        this.onRemove = onRemove;
    }

    void show(List<UnitLabel> units, Map<String, UnitClass> assignedClasses, boolean canEdit) {
        this.assignedClasses = assignedClasses;
        this.canEdit = canEdit;
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
        bindClassAction(row, unit, assignedClasses.get(unit.id()));
        if (onRemove == null) {
            row.removeUnit.setVisibility(View.GONE);
        } else {
            row.removeUnit.setVisibility(View.VISIBLE);
            row.removeUnit.setEnabled(canEdit);
            row.removeUnit.setContentDescription(
                    parent.getContext().getString(R.string.remove_unit_named, name));
            row.removeUnit.setOnClickListener(view -> onRemove.accept(unit));
        }
        return row.getRoot();
    }

    private void bindClassAction(ItemUnitBinding row, UnitLabel unit, @Nullable UnitClass unitClass) {
        if (onChangeClass == null) {
            return;
        }
        ImageView view = row.recruitedClassIcon;
        classIcons.bind(view, unitClass);
        boolean hasClass = unitClass != null;
        String name = hasClass ? unitClass.name : null;
        view.setVisibility(hasClass ? View.VISIBLE : View.GONE);
        view.setEnabled(canEdit);
        view.setContentDescription(hasClass
                ? view.getContext().getString(R.string.change_class_description, unit.name(), name)
                : null);
        view.setOnClickListener(icon -> onChangeClass.accept(unit));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            view.setTooltipText(name);
        } else {
            view.setOnLongClickListener(name == null ? null : icon -> {
                Toast.makeText(icon.getContext(), name, Toast.LENGTH_SHORT).show();
                return true;
            });
            view.setLongClickable(name != null);
        }
        row.assignClass.setVisibility(hasClass ? View.GONE : View.VISIBLE);
        row.assignClass.setEnabled(canEdit);
        row.assignClass.setContentDescription(
                view.getContext().getString(R.string.assign_class_description, unit.name()));
        row.assignClass.setOnClickListener(button -> onChangeClass.accept(unit));
    }
}
