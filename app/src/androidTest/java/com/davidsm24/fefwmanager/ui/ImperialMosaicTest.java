package com.davidsm24.fefwmanager.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.davidsm24.fefwmanager.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicLong;

@RunWith(AndroidJUnit4.class)
public class ImperialMosaicTest {
    @Test
    public void movesDownRepeatsAndFreezesWhenStopped() {
        var instrumentation = InstrumentationRegistry.getInstrumentation();
        instrumentation.runOnMainSync(() -> {
            Resources resources = instrumentation.getTargetContext().getResources();
            float quarterTile = resources.getDimensionPixelSize(R.dimen.mosaic_tile_size) / 4f;
            AtomicLong time = new AtomicLong();
            ImperialMosaic mosaic = new ImperialMosaic(resources, time::get);
            mosaic.setBounds(0, 0, 128, 128 + (int) Math.ceil(quarterTile));
            mosaic.setAnimating(true);

            Bitmap start = frame(mosaic, 0f);
            time.set(ImperialMosaic.LOOP_MILLIS / 4);
            Bitmap moving = frame(mosaic, 0f);
            assertFalse(start.sameAs(moving));
            assertTrue(start.sameAs(frame(mosaic, quarterTile)));

            time.set(ImperialMosaic.LOOP_MILLIS);
            assertTrue(start.sameAs(frame(mosaic, 0f)));

            mosaic.setAnimating(false);
            time.addAndGet(ImperialMosaic.LOOP_MILLIS / 4);
            assertTrue(start.sameAs(frame(mosaic, 0f)));
        });
    }

    private static Bitmap frame(ImperialMosaic mosaic, float top) {
        Bitmap output = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.translate(0f, -top);
        mosaic.draw(canvas);
        return output;
    }
}
