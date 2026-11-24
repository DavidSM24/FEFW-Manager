package com.davidsm24.fefwmanager.units;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.davidsm24.fefwmanager.FefwApplication;
import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.data.RecruitedUnit;
import com.davidsm24.fefwmanager.data.UnitClass;
import com.davidsm24.fefwmanager.databinding.ActivityUnitRosterBinding;
import com.davidsm24.fefwmanager.databinding.ViewGroupAnalysisBinding;
import com.davidsm24.fefwmanager.route.Route;
import com.davidsm24.fefwmanager.ui.ImperialActivity;
import com.davidsm24.fefwmanager.ui.ImperialMotion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UnitRosterActivity extends ImperialActivity {
    private static final String EXTRA_ROUTE_ID = "route_id";

    private ActivityUnitRosterBinding binding;
    private ViewGroupAnalysisBinding analysisBinding;
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
        analysisBinding = ViewGroupAnalysisBinding.inflate(getLayoutInflater(), binding.recruitedUnitList,
                false);
        binding.recruitedUnitList.addHeaderView(analysisBinding.getRoot(), null, false);
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
        viewModel.recruited().observe(this, ignored -> render());
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
        renderAnalysis(viewModel.analyzeGroup(recruits));
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

    private void renderAnalysis(GroupAnalysis analysis) {
        analysisBinding.groupEvaluatedUnits.setText(getString(R.string.group_evaluated_units,
                analysis.evaluatedUnits(), analysis.totalUnits()));
        boolean hasScores = analysis.evaluatedUnits() > 0;
        analysisBinding.groupScores.setVisibility(hasScores ? View.VISIBLE : View.GONE);
        if (hasScores) {
            setGroupScore(analysisBinding.groupAttack, R.string.group_attack, analysis.atk());
            setGroupScore(analysisBinding.groupDefense, R.string.group_defense, analysis.def());
            setGroupScore(analysisBinding.groupMobility, R.string.group_mobility, analysis.mov());
            setGroupScore(analysisBinding.groupSupport, R.string.group_support, analysis.sup());
            setGroupScore(analysisBinding.groupHealing, R.string.group_healing, analysis.heal());
        }
    }

    private void setGroupScore(TextView view, int label, int score) {
        view.setText(getString(R.string.group_score, getString(label), score));
    }

    private void showNotice(int message) {
        binding.unitNotice.setText(message);
        binding.unitNotice.setVisibility(View.VISIBLE);
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
                R.string.no_available_units, this::showAvailableClasses);
    }

    private void showAvailableClasses(UnitLabel unit) {
        List<UnitClass> choices = viewModel.classes().getValue();
        AlertDialog.Builder picker = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.choose_class_for_unit, unit.name()))
                .setIcon(UnitPortraits.resourceFor(unit.id()));
        dialog = UnitPicker.showClasses(picker, choices, unitClass -> viewModel.recruit(unit, unitClass));
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
