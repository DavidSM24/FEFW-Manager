package com.davidsm24.fefwmanager.units;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.data.UnitClass;
import com.davidsm24.fefwmanager.databinding.ItemClassTierBinding;
import com.davidsm24.fefwmanager.databinding.ItemUnitClassBinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

final class UnitClassListAdapter extends ChoiceListAdapter<UnitClassListAdapter.Row> {
    sealed interface Row {
        record Heading(ClassTier tier) implements Row {}
        record Choice(UnitClass unitClass) implements Row {}
    }

    private static final int TYPE_CLASS = 0;
    private static final int TYPE_HEADING = 1;

    private final LayoutInflater inflater;
    private final Consumer<UnitClass> onClassDetails;
    private final UnitClassIcons icons = new UnitClassIcons();

    UnitClassListAdapter(LayoutInflater inflater, Consumer<UnitClass> onClassDetails) {
        this.inflater = inflater;
        this.onClassDetails = onClassDetails;
    }

    static List<Row> groupedRows(List<UnitClass> choices) {
        List<UnitClass> sorted = new ArrayList<>(choices);
        sorted.sort(Comparator.comparing((UnitClass unitClass) -> ClassRoleCatalog.tierForClass(unitClass.id))
                .thenComparing(unitClass -> unitClass.name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(unitClass -> unitClass.id));
        List<Row> rows = new ArrayList<>();
        ClassTier previousTier = null;
        for (UnitClass unitClass : sorted) {
            ClassTier tier = ClassRoleCatalog.tierForClass(unitClass.id);
            if (tier != previousTier) {
                rows.add(new Row.Heading(tier));
                previousTier = tier;
            }
            rows.add(new Row.Choice(unitClass));
        }
        return rows;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position) instanceof Row.Choice;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return isEnabled(position) ? TYPE_CLASS : TYPE_HEADING;
    }

    @Override
    public View getView(int position, View recycled, ViewGroup parent) {
        Row item = getItem(position);
        if (item instanceof Row.Heading heading) {
            TextView label = recycled == null
                    ? ItemClassTierBinding.inflate(inflater, parent, false).getRoot() : (TextView) recycled;
            label.setText(titleFor(heading.tier()));
            return label;
        }

        ItemUnitClassBinding row;
        if (recycled == null) {
            row = ItemUnitClassBinding.inflate(inflater, parent, false);
            row.getRoot().setTag(row);
        } else {
            row = (ItemUnitClassBinding) recycled.getTag();
        }

        UnitClass unitClass = ((Row.Choice) item).unitClass();
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

    @StringRes
    private static int titleFor(ClassTier tier) {
        return switch (tier) {
            case INITIAL -> R.string.class_tier_initial;
            case INTERMEDIATE -> R.string.class_tier_intermediate;
            case ADVANCED -> R.string.class_tier_advanced;
            case MASTER -> R.string.class_tier_master;
            case DIVINE -> R.string.class_tier_divine;
            case UNCLASSIFIED -> R.string.class_tier_unclassified;
        };
    }
}
