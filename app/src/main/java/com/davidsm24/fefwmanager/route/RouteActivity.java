package com.davidsm24.fefwmanager.route;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.davidsm24.fefwmanager.FefwApplication;
import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.data.MainMission;
import com.davidsm24.fefwmanager.data.RouteProgress;
import com.davidsm24.fefwmanager.databinding.ActivityRouteBinding;
import com.davidsm24.fefwmanager.databinding.DialogMissionEditorBinding;

public final class RouteActivity extends ComponentActivity {
    private static final String EXTRA_ROUTE_ID = "com.davidsm24.fefwmanager.ROUTE_ID";
    private static final String MISSION_DRAFT = "mission_draft";
    private static final String DRAFT_DAY = "day";
    private static final String DRAFT_MONTH = "month";
    private static final String DRAFT_DESCRIPTION = "description";

    private Route route;
    private ActivityRouteBinding binding;
    private AlertDialog dialog;
    private DialogMissionEditorBinding missionForm;
    private MissionEditorViewModel missionEditor;

    public static Intent intent(Context context, Route route) {
        return new Intent(context, RouteActivity.class).putExtra(EXTRA_ROUTE_ID, route.id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        route = Route.fromId(getIntent().getStringExtra(EXTRA_ROUTE_ID));
        if (route == null) {
            finish();
            return;
        }

        binding = ActivityRouteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.routeTitle.setText(getString(R.string.route_title, getString(route.nameResource)));
        binding.backToRoutes.setOnClickListener(view -> finish());

        FefwApplication app = (FefwApplication) getApplication();
        missionEditor = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return modelClass.cast(
                        new MissionEditorViewModel(app.routeProgress(), app.missionWrites()));
            }
        }).get(MissionEditorViewModel.class);

        Bundle draft = savedInstanceState == null
                ? null : savedInstanceState.getBundle(MISSION_DRAFT);
        if (draft != null) {
            showMissionEditor(draft.getString(DRAFT_DAY), draft.getString(DRAFT_MONTH),
                    draft.getString(DRAFT_DESCRIPTION));
        }

        missionEditor.saveState().observe(this, this::renderSaveState);
        app.routeProgress().observeProgress(route).observe(this, this::render);
    }

    private void render(RouteProgress progress) {
        String deadline = progress.day == null
                ? getString(R.string.no_deadline)
                : getString(R.string.deadline_format, progress.day, progress.month);
        binding.missionSummary.setText(getString(R.string.next_mission_summary, deadline));

        binding.manageMission.setOnClickListener(view -> {
            missionEditor.reset();
            showMissionEditor(progress.day == null ? "" : progress.day.toString(),
                    progress.month == null ? "" : progress.month.toString(), progress.description);
        });

        binding.manageUnits.setOnClickListener(view -> showSummary(R.string.manage_units,
                getResources().getQuantityString(R.plurals.recruited_units,
                        progress.unitCount, progress.unitCount)));
        binding.manageSupports.setOnClickListener(view -> showSummary(R.string.manage_supports,
                getResources().getQuantityString(R.plurals.saved_supports,
                        progress.supportCount, progress.supportCount)));

        binding.manageMission.setEnabled(true);
        binding.manageUnits.setEnabled(true);
        binding.manageSupports.setEnabled(true);
    }

    private void showMissionEditor(String day, String month, String description) {
        DialogMissionEditorBinding form = DialogMissionEditorBinding.inflate(getLayoutInflater());
        form.missionDay.setText(day);
        form.missionMonth.setText(month);
        form.missionDescription.setText(description);

        AlertDialog editor = new AlertDialog.Builder(this)
                .setTitle(R.string.edit_main_mission)
                .setView(form.getRoot())
                .setNegativeButton(R.string.cancel_mission_edit, null)
                .setPositiveButton(R.string.save_mission_edit, null)
                .create();
        editor.setCanceledOnTouchOutside(false);
        editor.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        editor.setOnDismissListener(ignored -> {
            if (dialog == editor) {
                missionForm = null;
                dialog = null;
            }
        });

        missionForm = form;
        dialog = editor;
        editor.show();
        editor.getButton(DialogInterface.BUTTON_NEGATIVE).setAllCaps(false);
        editor.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(false);
        editor.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(view -> saveMission(form));
        renderSaveState(missionEditor.saveState().getValue());
    }

    private void saveMission(DialogMissionEditorBinding form) {
        form.missionDay.setError(null);
        form.missionMonth.setError(null);
        int month = datePart(form.missionMonth);
        if (month < 1 || month > 12) {
            form.missionMonth.requestFocus();
            form.missionMonth.setError(getString(R.string.invalid_mission_month));
            return;
        }
        int day = datePart(form.missionDay);
        if (!MainMission.isValidDate(day, month)) {
            form.missionDay.requestFocus();
            form.missionDay.setError(getString(R.string.invalid_mission_day));
            return;
        }
        missionEditor.save(new MainMission(route, day, month,
                form.missionDescription.getText().toString()));
    }

    private static int datePart(EditText field) {
        try {
            return Integer.parseInt(field.getText().toString());
        } catch (NumberFormatException invalidNumber) {
            return 0;
        }
    }

    private void renderSaveState(MissionEditorViewModel.SaveState state) {
        if (missionForm == null) {
            return;
        }
        if (state == MissionEditorViewModel.SaveState.SAVED) {
            dialog.dismiss();
            return;
        }

        boolean editable = state != MissionEditorViewModel.SaveState.SAVING;
        dialog.setCancelable(editable);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(editable);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(editable);
        missionForm.missionDay.setEnabled(editable);
        missionForm.missionMonth.setEnabled(editable);
        missionForm.missionDescription.setEnabled(editable);
        missionForm.missionSaveError.setVisibility(state == MissionEditorViewModel.SaveState.FAILED
                ? View.VISIBLE : View.GONE);
    }

    private void showSummary(int title, String message) {
        dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.close_summary, null)
                .show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (missionForm != null && dialog.isShowing()) {
            Bundle draft = new Bundle();
            draft.putString(DRAFT_DAY, missionForm.missionDay.getText().toString());
            draft.putString(DRAFT_MONTH, missionForm.missionMonth.getText().toString());
            draft.putString(DRAFT_DESCRIPTION, missionForm.missionDescription.getText().toString());
            outState.putBundle(MISSION_DRAFT, draft);
        }
    }

    @Override
    protected void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
        }
        super.onDestroy();
    }
}
