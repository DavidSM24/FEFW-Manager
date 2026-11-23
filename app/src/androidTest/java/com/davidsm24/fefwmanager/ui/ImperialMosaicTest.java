package com.davidsm24.fefwmanager.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicLong;

@RunWith(AndroidJUnit4.class)
public class ImperialMosaicTest {
    @Test
    public void movesRepeatsAndFreezesWhenStopped() {
        var instrumentation = InstrumentationRegistry.getInstrumentation();
        instrumentation.runOnMainSync(() -> {
            Resources resources = instrumentation.getTargetContext().getResources();
            AtomicLong time = new AtomicLong();
            ImperialMosaic mosaic = new ImperialMosaic(resources, time::get);
            mosaic.setBounds(0, 0, 128, 128);
            mosaic.setAnimating(true);

            Bitmap start = frame(mosaic);
            time.set(ImperialMosaic.LOOP_MILLIS / 4);
            Bitmap moving = frame(mosaic);
            assertFalse(start.sameAs(moving));

            time.set(ImperialMosaic.LOOP_MILLIS);
            assertTrue(start.sameAs(frame(mosaic)));

            mosaic.setAnimating(false);
            time.addAndGet(ImperialMosaic.LOOP_MILLIS / 4);
            assertTrue(start.sameAs(frame(mosaic)));
        });
    }

    private static Bitmap frame(ImperialMosaic mosaic) {
        Bitmap output = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888);
        mosaic.draw(new Canvas(output));
        return output;
    }
}
