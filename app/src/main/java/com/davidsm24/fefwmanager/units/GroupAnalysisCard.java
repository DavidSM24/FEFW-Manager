package com.davidsm24.fefwmanager.units;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;

import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.databinding.ViewGroupAnalysisBinding;
import com.davidsm24.fefwmanager.databinding.ViewGroupMetricBinding;

import java.util.ArrayList;
import java.util.List;

final class GroupAnalysisCard {
    private static final double MAX_SCORE = 5.0;

    private final ViewGroupAnalysisBinding binding;
    private final Context context;

    GroupAnalysisCard(ViewGroupAnalysisBinding binding, @ColorInt int accent) {
        this.binding = binding;
        context = binding.getRoot().getContext();
        ColorStateList tint = ColorStateList.valueOf(accent);
        for (ViewGroupMetricBinding metric : new ViewGroupMetricBinding[]{binding.groupAttack,
                binding.groupDefense, binding.groupMobility, binding.groupSupport, binding.groupHealing}) {
            metric.metricProgress.setProgressTintList(tint);
        }
    }

    void render(PartyAnalysisResult analysis, int recruitedCount) {
        binding.groupEvaluatedUnits.setText(context.getString(R.string.group_evaluated_units,
                analysis.evaluatedCount(), recruitedCount));
        PartyAnalysisResult.Raw raw = analysis.raw();
        PartyAnalysisResult.Scores scores = analysis.scores();
        setMetric(binding.groupAttack, R.string.group_attack, scores.atk(), raw.atk());
        setMetric(binding.groupDefense, R.string.group_defense, scores.def(), raw.def());
        setMetric(binding.groupMobility, R.string.group_mobility, scores.mov(), raw.mov());
        setMetric(binding.groupSupport, R.string.group_support, scores.sup(), raw.sup());
        setMetric(binding.groupHealing, R.string.group_healing, scores.heal(), raw.heal());

        List<PartyAlert> alerts = analysis.alerts();
        boolean hasAlerts = !alerts.isEmpty();
        binding.groupAlertHeading.setVisibility(hasAlerts ? View.VISIBLE : View.GONE);
        binding.groupAlerts.setVisibility(hasAlerts ? View.VISIBLE : View.GONE);
        binding.groupStatus.setVisibility(hasAlerts ? View.GONE : View.VISIBLE);
        if (hasAlerts) {
            List<String> messages = new ArrayList<>(alerts.size());
            for (PartyAlert alert : alerts) {
                messages.add(context.getString(alertMessage(alert)));
            }
            binding.groupAlerts.setText(TextUtils.join("\n\n", messages));
        } else if (analysis.isEmpty()) {
            binding.groupStatus.setText(R.string.group_no_classes);
            binding.groupStatus.setTextColor(context.getColor(R.color.sand));
        } else {
            binding.groupStatus.setText(R.string.group_balanced);
            binding.groupStatus.setTextColor(context.getColor(R.color.route_leda));
        }
    }

    private void setMetric(ViewGroupMetricBinding metric, @StringRes int label, int score, double raw) {
        metric.metricLabel.setText(label);
        metric.metricScore.setText(context.getString(R.string.group_score, score));
        metric.metricProgress.setProgress((int) Math.round(raw / MAX_SCORE * metric.metricProgress.getMax()));
        metric.getRoot().setContentDescription(
                context.getString(R.string.group_score_accessibility, context.getString(label), score));
    }

    @StringRes
    private static int alertMessage(PartyAlert alert) {
        return switch (alert) {
            case MISSING_MAGIC_DAMAGE -> R.string.group_alert_missing_magic_damage;
            case MISSING_PHYSICAL_DAMAGE -> R.string.group_alert_missing_physical_damage;
            case LOW_HEALING -> R.string.group_alert_low_healing;
            case NO_FRONTLINE -> R.string.group_alert_no_frontline;
            case NO_MOBILE_UNITS -> R.string.group_alert_no_mobile_units;
            case REDUNDANT_LOCKPICKING -> R.string.group_alert_redundant_lockpicking;
        };
    }
}
