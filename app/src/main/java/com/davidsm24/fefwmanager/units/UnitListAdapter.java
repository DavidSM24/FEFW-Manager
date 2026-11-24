package com.davidsm24.fefwmanager.units;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private final Consumer<UnitLabel> onUnitDetails;
    @Nullable private final Consumer<UnitLabel> onChangeClass;
    @Nullable private final Consumer<UnitClass> onClassDetails;
    @Nullable private final Consumer<UnitLabel> onRemove;
    private final UnitClassIcons classIcons = new UnitClassIcons();
    private Map<String, UnitClass> assignedClasses = Collections.emptyMap();
    private boolean canEdit;

    public UnitListAdapter(LayoutInflater inflater, @ColorInt int accent,
            Consumer<UnitLabel> onUnitDetails,
            @Nullable Consumer<UnitLabel> onChangeClass, @Nullable Consumer<UnitClass> onClassDetails,
            @Nullable Consumer<UnitLabel> onRemove) {
        this.inflater = inflater;
        this.accent = accent;
        this.onUnitDetails = onUnitDetails;
        this.onChangeClass = onChangeClass;
        this.onClassDetails = onClassDetails;
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
        View.OnClickListener selectUnit = onRemove == null ? selectItem(parent, row.getRoot()) : null;
        row.getRoot().setOnClickListener(selectUnit);
        UnitPortraits.enableDetails(row.unitPortrait, unit, onUnitDetails, selectUnit);
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
        HoldImageButton view = row.recruitedClassIcon;
        view.cancelHold();
        classIcons.bind(view, unitClass);
        boolean hasClass = unitClass != null;
        String name = hasClass ? unitClass.name : null;
        view.setVisibility(hasClass ? View.VISIBLE : View.GONE);
        view.setContentDescription(hasClass
                ? view.getContext().getString(R.string.change_class_description, unit.name(), name)
                : null);
        view.setOnClickListener(canEdit ? icon -> onChangeClass.accept(unit) : null);
        view.setClickable(canEdit);
        boolean canInspect = hasClass && onClassDetails != null;
        view.setOnLongClickListener(canInspect ? icon -> {
            onClassDetails.accept(unitClass);
            return true;
        } : null);
        view.setLongClickable(canInspect);
        row.assignClass.setVisibility(hasClass ? View.GONE : View.VISIBLE);
        row.assignClass.setEnabled(canEdit);
        row.assignClass.setContentDescription(
                view.getContext().getString(R.string.assign_class_description, unit.name()));
        row.assignClass.setOnClickListener(button -> onChangeClass.accept(unit));
    }
}
