package com.davidsm24.fefwmanager.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.davidsm24.fefwmanager.R;

import java.util.function.LongSupplier;

final class ImperialMosaic extends Drawable {
    static final long LOOP_MILLIS = 36_000;
    private static final long FRAME_INTERVAL_MILLIS = 33;
    private static Bitmap texture;

    private final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private final Matrix transform = new Matrix();
    private final BitmapShader shader;
    private final ColorFilter scrim;
    private final LongSupplier clock;
    private final int tileSize;
    private final float scale;
    private final Runnable nextFrame = this::invalidateSelf;
    private boolean animating;

    @MainThread
    ImperialMosaic(Resources resources, LongSupplier clock) {
        if (texture == null) {
            texture = BitmapFactory.decodeResource(resources, R.drawable.imperial_mosaic);
        }
        this.clock = clock;
        tileSize = resources.getDimensionPixelSize(R.dimen.mosaic_tile_size);
        scale = (float) tileSize / texture.getWidth();
        shader = new BitmapShader(texture, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        transform.setScale(scale, scale);
        shader.setLocalMatrix(transform);
        scrim = new PorterDuffColorFilter(resources.getColor(R.color.mosaic_scrim, null),
                PorterDuff.Mode.SRC_ATOP);
        paint.setShader(shader);
        paint.setColorFilter(scrim);
    }

    void setAnimating(boolean animating) {
        this.animating = animating;
        if (animating) {
            invalidateSelf();
        } else {
            unscheduleSelf(nextFrame);
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (animating) {
            long now = clock.getAsLong();
            float offset = (float) (now % LOOP_MILLIS) / LOOP_MILLIS * tileSize;
            transform.setScale(scale, scale);
            transform.postTranslate(0f, offset);
            shader.setLocalMatrix(transform);
            unscheduleSelf(nextFrame);
            scheduleSelf(nextFrame, now + FRAME_INTERVAL_MILLIS);
        }
        canvas.drawRect(getBounds(), paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter == null ? scrim : colorFilter);
        invalidateSelf();
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getOpacity() {
        return paint.getAlpha() == 255 ? PixelFormat.OPAQUE : PixelFormat.TRANSLUCENT;
    }
}
