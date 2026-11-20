package com.davidsm24.fefwmanager.units;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.davidsm24.fefwmanager.FefwApplication;
import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.databinding.ActivityUnitRosterBinding;
import com.davidsm24.fefwmanager.route.Route;
import com.davidsm24.fefwmanager.ui.ImperialMotion;

import java.util.ArrayList;
import java.util.List;

public final class UnitRosterActivity extends ComponentActivity {
    private static final String EXTRA_ROUTE_ID = "route_id";

    private ActivityUnitRosterBinding binding;
    private UnitRosterViewModel viewModel;
    private UnitListAdapter rosterAdapter;
    private Route route;
    private AlertDialog dialog;

    public static Intent intent(Context context, Route route) {
        return new Intent(context, UnitRosterActivity.class).putExtra(EXTRA_ROUTE_ID, route.id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        route = Route.fromId(getIntent().getStringExtra(EXTRA_ROUTE_ID));
        if (route == null) {
            finish();
            return;
        }

        binding = ActivityUnitRosterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.pageHeader.ledgerTitle.setText(R.string.unit_roster_title);
        binding.pageHeader.ledgerRoute.setText(
                getString(R.string.route_title, getString(route.nameResource)));
        UnitPortraits.bind(binding.pageHeader.routePortrait, route);
        if (savedInstanceState == null) {
            ImperialMotion.reveal(binding.pageHeader.getRoot());
        }
        binding.backToRoute.setOnClickListener(view -> finish());
        binding.addUnit.setOnClickListener(view -> showAvailableUnits());
        rosterAdapter = new UnitListAdapter(getLayoutInflater(), getColor(route.accentResource),
                this::confirmRemoval);
        binding.recruitedUnitList.setAdapter(rosterAdapter);

        FefwApplication app = (FefwApplication) getApplication();
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return modelClass.cast(new UnitRosterViewModel(route, app.routeProgress(),
                        app.progressIo(), app.unitCatalog()));
            }
        }).get(UnitRosterViewModel.class);
        viewModel.catalog().observe(this, ignored -> render());
        viewModel.recruited().observe(this, ignored -> render());
        viewModel.writes().observe(this, ignored -> render());
    }

    private void render() {
        UnitCatalog.LoadResult result = viewModel.catalog().getValue();
        List<String> recruitedIds = viewModel.recruited().getValue();
        UnitRosterViewModel.WriteState writeState = viewModel.writes().getValue();
        if (result == null || recruitedIds == null) {
            showNotice(R.string.loading_units);
            binding.addUnit.setEnabled(false);
            return;
        }

        UnitCatalog catalog = result.catalog();
        boolean writing = writeState == UnitRosterViewModel.WriteState.SAVING;
        binding.addUnit.setEnabled(catalog != null && !writing);
        List<UnitLabel> units = new ArrayList<>();
        for (String id : recruitedIds) {
            units.add(catalog == null ? new UnitLabel(id, id) : catalog.labelFor(id));
        }
        units.sort(UnitLabel.BY_NAME);
        rosterAdapter.show(units, !writing);
        binding.recruitedUnitList.setVisibility(units.isEmpty() ? View.GONE : View.VISIBLE);

        if (writeState == UnitRosterViewModel.WriteState.FAILED) {
            showNotice(R.string.recruit_write_error);
        } else if (catalog == null) {
            showNotice(R.string.unit_catalog_error);
        } else if (units.isEmpty()) {
            showNotice(R.string.no_recruited_units);
        } else {
            binding.unitNotice.setVisibility(View.GONE);
        }
    }

    private void showNotice(int message) {
        binding.unitNotice.setText(message);
        binding.unitNotice.setVisibility(View.VISIBLE);
    }

    private void showAvailableUnits() {
        UnitCatalog catalog = viewModel.catalog().getValue().catalog();
        List<UnitLabel> choices = catalog.availableFor(route, viewModel.recruited().getValue());
        choices.sort(UnitLabel.BY_NAME);
        AlertDialog.Builder picker = new AlertDialog.Builder(this)
                .setTitle(R.string.add_unit)
                .setNegativeButton(R.string.cancel_action, null);
        if (choices.isEmpty()) {
            picker.setMessage(R.string.no_available_units);
        } else {
            UnitListAdapter adapter = new UnitListAdapter(getLayoutInflater(),
                    getColor(route.accentResource), null);
            adapter.show(choices, false);
            picker.setAdapter(adapter, (ignored, position) -> viewModel.recruit(adapter.getItem(position)));
        }
        dialog = picker.show();
    }

    private void confirmRemoval(UnitLabel unit) {
        dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.remove_unit_named, unit.name()))
                .setIcon(UnitPortraits.resourceFor(unit.id()))
                .setMessage(R.string.remove_unit_confirmation)
                .setNegativeButton(R.string.cancel_action, null)
                .setPositiveButton(R.string.remove_unit, (ignored, which) -> viewModel.remove(unit))
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
