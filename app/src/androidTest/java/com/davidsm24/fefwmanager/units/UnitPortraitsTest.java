package com.davidsm24.fefwmanager.units;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.app.Instrumentation;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.davidsm24.fefwmanager.databinding.ViewUnitPortraitBinding;
import com.davidsm24.fefwmanager.route.Route;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UnitPortraitsTest {
    @Test
    public void rebindsPortraitsAndClearsTheImageForAnUnknownUnit() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        instrumentation.runOnMainSync(() -> {
            Context context = instrumentation.getTargetContext();
            ViewUnitPortraitBinding portrait =
                    ViewUnitPortraitBinding.inflate(LayoutInflater.from(context));

            UnitPortraits.bind(portrait, Route.CAI);
            assertEquals(View.VISIBLE, portrait.unitPortraitImage.getVisibility());
            assertNotNull(portrait.unitPortraitImage.getDrawable());
            assertEquals(View.GONE, portrait.unitPortraitInitial.getVisibility());

            UnitPortraits.bind(portrait, new UnitLabel("legacy-unit", "Legacy"), 0xffc9a45d);
            assertEquals(View.GONE, portrait.unitPortraitImage.getVisibility());
            assertNull(portrait.unitPortraitImage.getDrawable());
            assertEquals(View.VISIBLE, portrait.unitPortraitInitial.getVisibility());
            assertEquals("L", portrait.unitPortraitInitial.getText().toString());

            UnitPortraits.bind(portrait, Route.LEDA);
            assertEquals(View.VISIBLE, portrait.unitPortraitImage.getVisibility());
            assertNotNull(portrait.unitPortraitImage.getDrawable());
            assertEquals(View.GONE, portrait.unitPortraitInitial.getVisibility());
        });
    }
}
