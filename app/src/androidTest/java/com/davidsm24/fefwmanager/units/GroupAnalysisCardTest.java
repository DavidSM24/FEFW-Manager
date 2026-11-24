package com.davidsm24.fefwmanager.units;

import static org.junit.Assert.assertEquals;

import android.app.Instrumentation;
import android.view.LayoutInflater;
import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.davidsm24.fefwmanager.R;
import com.davidsm24.fefwmanager.databinding.ViewGroupAnalysisBinding;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
public final class GroupAnalysisCardTest {
    @Test
    public void displaysFractionalProgressAndBalancedStatus() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        var context = instrumentation.getTargetContext();
        PartyAnalysisResult analysis = PartyAnalyzer.analyze(Arrays.asList(
                ClassRoleCatalog.forClass("armored-knight"),
                ClassRoleCatalog.forClass("ovate"),
                ClassRoleCatalog.forClass("wing-soldier")));
        AtomicReference<ViewGroupAnalysisBinding> result = new AtomicReference<>();
        instrumentation.runOnMainSync(() -> {
            ViewGroupAnalysisBinding binding = ViewGroupAnalysisBinding.inflate(LayoutInflater.from(context));
            new GroupAnalysisCard(binding, context.getColor(R.color.route_cai)).render(analysis, 4);
            result.set(binding);
        });

        ViewGroupAnalysisBinding binding = result.get();
        assertEquals("Unidades evaluadas · 3/4", binding.groupEvaluatedUnits.getText().toString());
        assertEquals("4 / 5", binding.groupHealing.metricScore.getText().toString());
        assertEquals(3824, binding.groupHealing.metricProgress.getProgress());
        assertEquals(View.GONE, binding.groupAlerts.getVisibility());
        assertEquals(View.VISIBLE, binding.groupStatus.getVisibility());
        assertEquals("Grupo equilibrado", binding.groupStatus.getText().toString());
    }
}
