package com.davidsm24.fefwmanager.units;

import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;

import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.data.UnitClass;
import com.davidsm24.fefwmanager.databinding.DialogUnitPickerBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

public final class UnitPicker {
    private record SearchLabels(@StringRes int hint, @StringRes int noMatches, @StringRes int empty) {
    }

    private UnitPicker() {
    }

    public static AlertDialog show(AlertDialog.Builder builder, @ColorInt int accent,
            List<UnitLabel> choices, @StringRes int emptyMessage, Consumer<UnitLabel> onChoose) {
        UnitListAdapter adapter = new UnitListAdapter(LayoutInflater.from(builder.getContext()), accent,
                null);
        return show(builder, adapter, choices, UnitLabel::name,
                new SearchLabels(R.string.search_units, R.string.no_matching_units, emptyMessage),
                onChoose);
    }

    static AlertDialog showClasses(AlertDialog.Builder builder, List<UnitClass> choices,
            Consumer<UnitClass> onChoose) {
        UnitClassListAdapter adapter = new UnitClassListAdapter(LayoutInflater.from(builder.getContext()));
        return show(builder, adapter, choices, unitClass -> unitClass.name,
                new SearchLabels(R.string.search_classes, R.string.no_matching_classes,
                        R.string.no_available_classes), onChoose);
    }

    private static <T> AlertDialog show(AlertDialog.Builder builder, ChoiceListAdapter<T> adapter,
            List<T> choices, Function<T, String> name, SearchLabels labels, Consumer<T> onChoose) {
        builder.setNegativeButton(R.string.cancel_action, null);
        if (choices.isEmpty()) {
            return builder.setMessage(labels.empty()).show();
        }

        LayoutInflater inflater = LayoutInflater.from(builder.getContext());
        DialogUnitPickerBinding binding = DialogUnitPickerBinding.inflate(inflater);
        binding.unitSearch.setHint(labels.hint());
        binding.unitSearchEmpty.setText(labels.noMatches());
        adapter.showChoices(choices);
        binding.unitChoices.setEmptyView(binding.unitSearchEmpty);
        binding.unitChoices.setAdapter(adapter);
        binding.unitSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                adapter.showChoices(matchingChoices(choices, name, text.toString()));
                binding.unitChoices.setSelection(0);
            }

            @Override
            public void afterTextChanged(Editable text) {
            }
        });

        AlertDialog dialog = builder.setView(binding.getRoot()).create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        binding.unitChoices.setOnItemClickListener((parent, row, position, id) -> {
            T selected = adapter.getItem(position);
            dialog.dismiss();
            onChoose.accept(selected);
        });
        dialog.show();
        return dialog;
    }

    private static <T> List<T> matchingChoices(List<T> choices, Function<T, String> name, String text) {
        String query = text.trim().toLowerCase(Locale.ROOT);
        if (query.isEmpty()) {
            return choices;
        }
        List<T> matches = new ArrayList<>();
        for (T choice : choices) {
            if (name.apply(choice).toLowerCase(Locale.ROOT).contains(query)) {
                matches.add(choice);
            }
        }
        return matches;
    }
}
