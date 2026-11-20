package com.davidsm24.fefwmanager.supports;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.davidsm24.fefwmanager.FefwApplication;
import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.data.SupportPair;
import com.davidsm24.fefwmanager.databinding.ActivitySupportPairsBinding;
import com.davidsm24.fefwmanager.databinding.DialogSupportPortraitsBinding;
import com.davidsm24.fefwmanager.route.Route;
import com.davidsm24.fefwmanager.ui.ImperialMotion;
import com.davidsm24.fefwmanager.units.UnitCatalog;
import com.davidsm24.fefwmanager.units.UnitLabel;
import com.davidsm24.fefwmanager.units.UnitPicker;
import com.davidsm24.fefwmanager.units.UnitPortraits;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public final class SupportPairsActivity extends ComponentActivity {
    private static final String EXTRA_ROUTE_ID = "route_id";

    private ActivitySupportPairsBinding binding;
    private SupportPairsViewModel viewModel;
    private LiveData<UnitCatalog.LoadResult> catalog;
    private SupportPairAdapter adapter;
    private AlertDialog dialog;
    private int accent;

    public static Intent intent(Context context, Route route) {
        return new Intent(context, SupportPairsActivity.class).putExtra(EXTRA_ROUTE_ID, route.id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Route route = Route.fromId(getIntent().getStringExtra(EXTRA_ROUTE_ID));
        if (route == null) {
            finish();
            return;
        }

        binding = ActivitySupportPairsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.pageHeader.ledgerTitle.setText(R.string.support_pairs_title);
        binding.pageHeader.ledgerRoute.setText(
                getString(R.string.route_title, getString(route.nameResource)));
        UnitPortraits.bind(binding.pageHeader.routePortrait, route);
        if (savedInstanceState == null) {
            ImperialMotion.reveal(binding.pageHeader.getRoot());
        }
        binding.backToRoute.setOnClickListener(view -> finish());
        binding.addSupport.setOnClickListener(view -> chooseFirst());
        accent = getColor(route.accentResource);
        adapter = new SupportPairAdapter(getLayoutInflater(), accent, this::confirmRemoval);
        binding.supportPairList.setAdapter(adapter);

        FefwApplication app = (FefwApplication) getApplication();
        catalog = app.unitCatalog();
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return modelClass.cast(new SupportPairsViewModel(route, app.routeProgress(),
                        app.progressIo()));
            }
        }).get(SupportPairsViewModel.class);

        catalog.observe(this, ignored -> render());
        viewModel.pairs().observe(this, ignored -> render());
        viewModel.recruits().observe(this, ignored -> render());
        viewModel.writes().observe(this, ignored -> render());
    }

    private void render() {
        UnitCatalog.LoadResult result = catalog.getValue();
        List<SupportPair> pairs = viewModel.pairs().getValue();
        List<String> recruits = viewModel.recruits().getValue();
        if (result == null || pairs == null || recruits == null) {
            binding.addSupport.setEnabled(false);
            showNotice(R.string.loading_supports);
            return;
        }

        SupportPairsViewModel.WriteState write = viewModel.writes().getValue();
        boolean writing = write == SupportPairsViewModel.WriteState.SAVING;
        UnitCatalog units = result.catalog();
        binding.addSupport.setEnabled(!writing && units != null && recruits.size() >= 2);
        Set<String> recruitedIds = new HashSet<>(recruits);
        List<SupportPairAdapter.Row> rows = new ArrayList<>();
        for (SupportPair pair : pairs) {
            SupportPairAdapter.Member first = member(units, recruitedIds, pair.firstUnitId);
            SupportPairAdapter.Member second = member(units, recruitedIds, pair.secondUnitId);
            rows.add(new SupportPairAdapter.Row(pair, first, second));
        }
        adapter.show(rows, !writing);
        binding.supportPairList.setVisibility(rows.isEmpty() ? View.GONE : View.VISIBLE);

        if (write == SupportPairsViewModel.WriteState.DUPLICATE) {
            showNotice(R.string.duplicate_support);
        } else if (write == SupportPairsViewModel.WriteState.MISSING_RECRUITS) {
            showNotice(R.string.support_recruits_changed);
        } else if (write == SupportPairsViewModel.WriteState.FAILED) {
            showNotice(R.string.support_write_error);
        } else if (units == null) {
            showNotice(R.string.unit_catalog_error);
        } else if (recruits.size() < 2) {
            showNotice(R.string.recruit_two_for_support);
        } else if (rows.isEmpty()) {
            showNotice(R.string.no_support_pairs);
        } else {
            binding.supportNotice.setVisibility(View.GONE);
        }
    }

    private static SupportPairAdapter.Member member(@Nullable UnitCatalog catalog,
            Set<String> recruitedIds, String id) {
        UnitLabel unit = catalog == null ? new UnitLabel(id, id) : catalog.labelFor(id);
        return new SupportPairAdapter.Member(unit, recruitedIds.contains(id));
    }

    private void showNotice(int text) {
        binding.supportNotice.setText(text);
        binding.supportNotice.setVisibility(View.VISIBLE);
    }

    private void chooseFirst() {
        showUnitPicker(getString(R.string.choose_first_support), 0, choicesExcept(null),
                this::chooseSecond);
    }

    private void chooseSecond(UnitLabel first) {
        showUnitPicker(getString(R.string.choose_second_support, first.name()),
                UnitPortraits.resourceFor(first.id()), choicesExcept(first.id()),
                second -> viewModel.create(first, second));
    }

    private List<UnitLabel> choicesExcept(@Nullable String excludedId) {
        UnitCatalog units = catalog.getValue().catalog();
        List<UnitLabel> choices = new ArrayList<>();
        for (String id : viewModel.recruits().getValue()) {
            if (!id.equals(excludedId)) {
                choices.add(units.labelFor(id));
            }
        }
        choices.sort(UnitLabel.BY_NAME);
        return choices;
    }

    private void showUnitPicker(String title, @DrawableRes int icon,
            List<UnitLabel> choices, Consumer<UnitLabel> choose) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setIcon(icon);
        dialog = UnitPicker.show(builder, accent, choices, R.string.support_recruits_changed, choose);
    }

    private void confirmRemoval(SupportPairAdapter.Row row) {
        DialogSupportPortraitsBinding portraits =
                DialogSupportPortraitsBinding.inflate(getLayoutInflater());
        UnitPortraits.bind(portraits.firstPortrait, row.first().unit(), accent);
        UnitPortraits.bind(portraits.secondPortrait, row.second().unit(), accent);
        dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.remove_support)
                .setMessage(getString(R.string.remove_support_confirmation,
                        row.first().unit().name(), row.second().unit().name()))
                .setView(portraits.getRoot())
                .setNegativeButton(R.string.cancel_action, null)
                .setPositiveButton(R.string.remove_unit, (ignored, which) -> viewModel.remove(row.pair()))
                .show();
    }

    @Override
    protected void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
        }
        super.onDestroy();
    }
}
