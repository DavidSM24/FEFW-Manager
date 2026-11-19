package com.davidsm24.fefwmanager.route;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.ComponentActivity;

import com.davidsm24.fefwmanager.FefwApplication;
import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.data.RouteProgress;
import com.davidsm24.fefwmanager.databinding.ActivityRouteBinding;

public final class RouteActivity extends ComponentActivity {
    private static final String EXTRA_ROUTE_ID = "com.davidsm24.fefwmanager.ROUTE_ID";

    private ActivityRouteBinding binding;
    private AlertDialog dialog;

    public static Intent intent(Context context, Route route) {
        return new Intent(context, RouteActivity.class).putExtra(EXTRA_ROUTE_ID, route.id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Route route = Route.fromId(getIntent().getStringExtra(EXTRA_ROUTE_ID));
        if (route == null) {
            finish();
            return;
        }

        binding = ActivityRouteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.routeTitle.setText(getString(R.string.route_title, getString(route.nameResource)));
        binding.backToRoutes.setOnClickListener(view -> finish());

        ((FefwApplication) getApplication()).routeProgress().observeProgress(route)
                .observe(this, this::render);
    }

    private void render(RouteProgress progress) {
        String deadline = progress.day == null
                ? getString(R.string.no_deadline)
                : getString(R.string.deadline_format, progress.day, progress.month);
        binding.missionSummary.setText(getString(R.string.next_mission_summary, deadline));

        String missionDescription = progress.description == null || progress.description.isEmpty()
                ? getString(R.string.no_mission_description)
                : progress.description;
        binding.manageMission.setOnClickListener(view -> showSummary(R.string.main_mission,
                getString(R.string.mission_preview, deadline, missionDescription)));

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

    private void showSummary(int title, String message) {
        dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.close_summary, null)
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
