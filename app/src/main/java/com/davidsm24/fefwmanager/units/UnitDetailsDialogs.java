package com.davidsm24.fefwmanager.units;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.davidsm24.fefwmanager.FefwApplication;
import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.data.RecruitedUnit;
import com.davidsm24.fefwmanager.data.UnitClass;
import com.davidsm24.fefwmanager.databinding.DialogClassDetailsBinding;
import com.davidsm24.fefwmanager.databinding.DialogUnitDetailsBinding;
import com.davidsm24.fefwmanager.route.Route;
import com.davidsm24.fefwmanager.ui.FloatingCards;
import com.davidsm24.fefwmanager.ui.ImperialActivity;

public final class UnitDetailsDialogs {
    private final ImperialActivity activity;
    private final FefwApplication app;
    private final UnitClassIcons icons = new UnitClassIcons();
    private AlertDialog unitDialog;
    private AlertDialog classDialog;

    public UnitDetailsDialogs(ImperialActivity activity) {
        this.activity = activity;
        app = (FefwApplication) activity.getApplication();
    }

    public void showUnit(UnitLabel unit, Route route) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        DialogUnitDetailsBinding details = DialogUnitDetailsBinding.inflate(
                LayoutInflater.from(builder.getContext()));
        UnitPortraits.bind(details.detailUnitPortrait, unit, activity.getColor(route.accentResource));
        details.detailUnitName.setText(unit.name());
        details.detailUnitDescription.setText(R.string.unit_details_loading);
        details.detailUnitClassLabel.setText(R.string.unit_details_class);
        details.detailUnitClassName.setText(R.string.unit_details_loading);

        AlertDialog card = builder.setView(details.getRoot()).create();
        LiveData<UnitCatalog.LoadResult> catalog = app.unitCatalog();
        Observer<UnitCatalog.LoadResult> observer = result -> {
            if (!card.isShowing()) {
                return;
            }
            UnitCatalog.Biography biography = result.catalog() == null ? null
                    : result.catalog().biographyFor(unit.id());
            if (biography == null || biography.description().isEmpty()) {
                details.detailUnitDescription.setText(R.string.unit_description_missing);
            } else {
                details.detailUnitDescription.setText(biography.description());
            }
            loadClass(card, details, route, unit.id(),
                    biography == null ? "" : biography.referenceClassId());
        };
        card.setOnDismissListener(ignored -> {
            catalog.removeObserver(observer);
            if (unitDialog == card) {
                unitDialog = null;
            }
        });
        unitDialog = card;
        FloatingCards.show(activity, card);
        catalog.observe(activity, observer);
    }

    private void loadClass(AlertDialog card, DialogUnitDetailsBinding details, Route route,
            String unitId, String referenceClassId) {
        app.progressIo().execute(() -> {
            try {
                RecruitedUnit recruit = app.routeProgress().findRecruit(route, unitId);
                boolean assigned = recruit != null && recruit.classId != null;
                String classId = assigned ? recruit.classId : referenceClassId;
                UnitClass unitClass = classId.isEmpty() ? null : app.unitClasses().findById(classId);
                activity.runOnUiThread(() -> {
                    if (card.isShowing()) {
                        bindUnitClass(details, unitClass, assigned);
                    }
                });
            } catch (RuntimeException failure) {
                Log.e("UnitDetails", "Unable to load the unit class", failure);
                activity.runOnUiThread(() -> {
                    if (card.isShowing()) {
                        details.detailUnitClassName.setText(R.string.unit_details_class_unavailable);
                    }
                });
            }
        });
    }

    private void bindUnitClass(DialogUnitDetailsBinding details, @Nullable UnitClass unitClass,
            boolean assigned) {
        if (unitClass == null) {
            details.detailUnitClassName.setText(R.string.unit_details_class_missing);
            return;
        }
        details.detailUnitClassLabel.setText(assigned ? R.string.unit_details_assigned_class
                : R.string.unit_details_reference_class);
        details.detailUnitClassName.setText(unitClass.name);
        HoldImageButton icon = details.detailUnitClassIcon;
        icons.bind(icon, unitClass);
        icon.setContentDescription(activity.getString(R.string.unit_details_open_class, unitClass.name));
        icon.setOnClickListener(view -> showClass(unitClass));
        icon.setOnLongClickListener(view -> {
            showClass(unitClass);
            return true;
        });
        icon.setVisibility(View.VISIBLE);
    }

    void showClass(UnitClass unitClass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        DialogClassDetailsBinding details = DialogClassDetailsBinding.inflate(
                LayoutInflater.from(builder.getContext()));
        icons.bind(details.detailClassIcon, unitClass);
        details.detailClassName.setText(unitClass.name);
        details.detailClassDescription.setText(ClassDescriptions.forClass(unitClass.id));

        AlertDialog card = builder.setView(details.getRoot()).create();
        card.setOnDismissListener(ignored -> {
            if (classDialog == card) {
                classDialog = null;
            }
        });
        classDialog = card;
        FloatingCards.show(activity, card);
    }

    public void dismiss() {
        if (classDialog != null) {
            classDialog.dismiss();
        }
        if (unitDialog != null) {
            unitDialog.dismiss();
        }
    }
}
