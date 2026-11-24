package com.davidsm24.fefwmanager.units;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.davidsm24.fefwmanager.FefwApplication;
import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.data.RecruitedUnit;
import com.davidsm24.fefwmanager.data.UnitClass;
import com.davidsm24.fefwmanager.databinding.ActivityUnitRosterBinding;
import com.davidsm24.fefwmanager.databinding.ViewGroupAnalysisBinding;
import com.davidsm24.fefwmanager.route.Route;
import com.davidsm24.fefwmanager.ui.FloatingCards;
import com.davidsm24.fefwmanager.ui.ImperialActivity;
import com.davidsm24.fefwmanager.ui.ImperialMotion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class UnitRosterActivity extends ImperialActivity {
    private static final String EXTRA_ROUTE_ID = "route_id";

    private ActivityUnitRosterBinding binding;
    private UnitRosterViewModel viewModel;
    private UnitListAdapter rosterAdapter;
    private GroupAnalysisCard openAnalysis;
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
        UnitPortraits.enableDetails(binding.pageHeader.routePortrait, route, this::showUnitDetails, null);
        if (savedInstanceState == null) {
            ImperialMotion.reveal(binding.pageHeader.getRoot());
        }
        binding.backToRoute.setOnClickListener(view -> finish());
        binding.addUnit.setOnClickListener(view -> showAvailableUnits());
        binding.showGroupAnalysis.setOnClickListener(view -> showGroupAnalysis());
        int accent = getColor(route.accentResource);
        rosterAdapter = new UnitListAdapter(getLayoutInflater(), accent,
                this::showUnitDetails, this::showClassEditor, details()::showClass, this::confirmRemoval);
        binding.recruitedUnitList.setAdapter(rosterAdapter);

        FefwApplication app = (FefwApplication) getApplication();
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return modelClass.cast(new UnitRosterViewModel(route, app.routeProgress(),
                        app.unitClasses(), app.progressIo(), app.unitCatalog()));
            }
        }).get(UnitRosterViewModel.class);
        viewModel.catalog().observe(this, ignored -> render());
        viewModel.recruited().observe(this, recruits -> {
            binding.showGroupAnalysis.setEnabled(true);
            if (openAnalysis != null) {
                openAnalysis.render(viewModel.analyzeGroup(recruits), recruits.size());
            }
            render();
        });
        viewModel.classes().observe(this, ignored -> render());
        viewModel.writes().observe(this, ignored -> render());
    }

    private void render() {
        UnitCatalog.LoadResult result = viewModel.catalog().getValue();
        List<RecruitedUnit> recruits = viewModel.recruited().getValue();
        List<UnitClass> classes = viewModel.classes().getValue();
        UnitRosterViewModel.WriteState writeState = viewModel.writes().getValue();
        if (result == null || recruits == null || classes == null) {
            showNotice(R.string.loading_units);
            binding.addUnit.setEnabled(false);
            return;
        }

        UnitCatalog catalog = result.catalog();
        boolean writing = writeState == UnitRosterViewModel.WriteState.SAVING;
        binding.addUnit.setEnabled(catalog != null && !writing);
        Map<String, UnitClass> classesById = new HashMap<>();
        for (UnitClass unitClass : classes) {
            classesById.put(unitClass.id, unitClass);
        }
        List<UnitLabel> units = new ArrayList<>();
        Map<String, UnitClass> assignedClasses = new HashMap<>();
        for (RecruitedUnit recruit : recruits) {
            String id = recruit.unitId;
            units.add(catalog == null ? new UnitLabel(id, id) : catalog.labelFor(id));
            UnitClass assignedClass = classesById.get(recruit.classId);
            if (assignedClass != null) {
                assignedClasses.put(id, assignedClass);
            }
        }
        units.sort(UnitLabel.BY_NAME);
        rosterAdapter.show(units, assignedClasses, !writing);
        binding.recruitedUnitList.setVisibility(View.VISIBLE);

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

    private void showGroupAnalysis() {
        List<RecruitedUnit> recruits = viewModel.recruited().getValue();
        if (recruits == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ScrollView content = new ScrollView(builder.getContext());
        ViewGroupAnalysisBinding analysis = ViewGroupAnalysisBinding.inflate(
                LayoutInflater.from(builder.getContext()), content, false);
        content.addView(analysis.getRoot());
        GroupAnalysisCard card = new GroupAnalysisCard(analysis);
        card.render(viewModel.analyzeGroup(recruits), recruits.size());

        dialog = builder.setView(content).create();
        dialog.setOnDismissListener(ignored -> {
            if (openAnalysis == card) {
                openAnalysis = null;
            }
        });
        openAnalysis = card;
        FloatingCards.show(this, dialog);
    }

    private void showUnitDetails(UnitLabel unit) {
        details().showUnit(unit, route);
    }

    private void showAvailableUnits() {
        UnitCatalog catalog = viewModel.catalog().getValue().catalog();
        List<String> recruitedIds = new ArrayList<>();
        for (RecruitedUnit recruit : viewModel.recruited().getValue()) {
            recruitedIds.add(recruit.unitId);
        }
        List<UnitLabel> choices = catalog.availableFor(route, recruitedIds);
        choices.sort(UnitLabel.BY_NAME);
        AlertDialog.Builder picker = new AlertDialog.Builder(this)
                .setTitle(R.string.add_unit);
        dialog = UnitPicker.show(picker, getColor(route.accentResource), choices,
                R.string.no_available_units, unit -> showClassPicker(unit, R.string.choose_class_for_unit,
                        unitClass -> viewModel.recruit(unit, unitClass)), this::showUnitDetails);
    }

    private void showClassEditor(UnitLabel unit) {
        showClassPicker(unit, R.string.change_class_for_unit,
                unitClass -> viewModel.changeClass(unit, unitClass));
    }

    private void showClassPicker(UnitLabel unit, @StringRes int title, Consumer<UnitClass> onChoose) {
        List<UnitClass> choices = viewModel.classes().getValue();
        AlertDialog.Builder picker = new AlertDialog.Builder(this)
                .setTitle(getString(title, unit.name()))
                .setIcon(UnitPortraits.resourceFor(unit.id()));
        dialog = UnitPicker.showClasses(picker, choices, onChoose, details()::showClass);
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
