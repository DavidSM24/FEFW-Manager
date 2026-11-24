package com.davidsm24.fefwmanager.units;

import static org.junit.Assert.assertEquals;

import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.davidsm24.fefwmanager.databinding.ViewGroupAnalysisBinding;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
public final class GroupAnalysisCardTest {
    private record LevelCase(int input, int activeSegments, int color) {}

    @Test
    public void displaysRoundedLevelAndBalancedStatus() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        var context = instrumentation.getTargetContext();
        PartyAnalysisResult analysis = PartyAnalyzer.analyze(Arrays.asList(
                ClassRoleCatalog.forClass("armored-knight"),
                ClassRoleCatalog.forClass("ovate"),
                ClassRoleCatalog.forClass("wing-soldier")));
        AtomicReference<ViewGroupAnalysisBinding> result = new AtomicReference<>();
        instrumentation.runOnMainSync(() -> {
            ViewGroupAnalysisBinding binding = ViewGroupAnalysisBinding.inflate(LayoutInflater.from(context));
            new GroupAnalysisCard(binding).render(analysis, 4);
            result.set(binding);
        });

        ViewGroupAnalysisBinding binding = result.get();
        assertEquals("Unidades evaluadas · 3/4", binding.groupEvaluatedUnits.getText().toString());
        assertEquals("4 / 5", binding.groupHealing.metricScore.getText().toString());
        assertEquals(4, binding.groupHealing.metricLevel.getLevel());
        assertEquals(View.GONE, binding.groupAlerts.getVisibility());
        assertEquals(View.VISIBLE, binding.groupStatus.getVisibility());
        assertEquals("Grupo equilibrado", binding.groupStatus.getText().toString());
    }

    @Test
    public void paintsAllFiveSegmentsWithTheSameLevelColorAndClampsInvalidScores() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        LevelCase[] cases = {
                new LevelCase(-1, 1, 0xFFE53935),
                new LevelCase(5, 5, 0xFF9C27B0),
                new LevelCase(1, 1, 0xFFE53935),
                new LevelCase(2, 2, 0xFFFB8C00),
                new LevelCase(3, 3, 0xFF43A047),
                new LevelCase(4, 4, 0xFF1E88E5),
                new LevelCase(6, 5, 0xFF9C27B0)
        };
        int[][] pixels = new int[cases.length][5];
        int[] gaps = new int[cases.length];
        instrumentation.runOnMainSync(() -> {
            StatLevelBar bar = new StatLevelBar(instrumentation.getTargetContext(), null);
            bar.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            bar.layout(0, 0, 100, 12);
            Bitmap bitmap = Bitmap.createBitmap(100, 12, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            for (int index = 0; index < cases.length; index++) {
                bar.setLevel(cases[index].input());
                bitmap.eraseColor(Color.TRANSPARENT);
                bar.draw(canvas);
                for (int segment = 0; segment < 5; segment++) {
                    pixels[index][segment] = bitmap.getPixel(segment * 20 + 10, 6);
                }
                gaps[index] = bitmap.getPixel(40, 6);
            }
            bitmap.recycle();
        });

        for (int index = 0; index < cases.length; index++) {
            LevelCase level = cases[index];
            assertEquals(Color.TRANSPARENT, gaps[index]);
            for (int segment = 0; segment < 5; segment++) {
                int pixel = pixels[index][segment];
                if (segment < level.activeSegments()) {
                    assertEquals(level.color(), pixel);
                } else {
                    assertEquals(46, Color.alpha(pixel));
                    // Premultiplicar el alfa de 8 bits redondea los canales RGB.
                    assertEquals(Color.red(level.color()), Color.red(pixel), 3.0);
                    assertEquals(Color.green(level.color()), Color.green(pixel), 3.0);
                    assertEquals(Color.blue(level.color()), Color.blue(pixel), 3.0);
                }
            }
        }
    }
}
