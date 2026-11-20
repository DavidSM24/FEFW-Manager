package com.davidsm24.fefwmanager.units;

import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;

import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.databinding.DialogUnitPickerBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public final class UnitPicker {
    private UnitPicker() {
    }

    public static AlertDialog show(AlertDialog.Builder builder, @ColorInt int accent,
            List<UnitLabel> choices, @StringRes int emptyMessage, Consumer<UnitLabel> onChoose) {
        builder.setNegativeButton(R.string.cancel_action, null);
        if (choices.isEmpty()) {
            return builder.setMessage(emptyMessage).show();
        }

        LayoutInflater inflater = LayoutInflater.from(builder.getContext());
        DialogUnitPickerBinding binding = DialogUnitPickerBinding.inflate(inflater);
        UnitListAdapter adapter = new UnitListAdapter(inflater, accent, null);
        adapter.show(choices, false);
        binding.unitChoices.setEmptyView(binding.unitSearchEmpty);
        binding.unitChoices.setAdapter(adapter);
        binding.unitSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                adapter.show(matchingUnits(choices, text.toString()), false);
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
            UnitLabel selected = adapter.getItem(position);
            dialog.dismiss();
            onChoose.accept(selected);
        });
        dialog.show();
        return dialog;
    }

    private static List<UnitLabel> matchingUnits(List<UnitLabel> choices, String text) {
        String query = text.trim().toLowerCase(Locale.ROOT);
        if (query.isEmpty()) {
            return choices;
        }
        List<UnitLabel> matches = new ArrayList<>();
        for (UnitLabel unit : choices) {
            if (unit.name().toLowerCase(Locale.ROOT).contains(query)) {
                matches.add(unit);
            }
        }
        return matches;
    }
}
