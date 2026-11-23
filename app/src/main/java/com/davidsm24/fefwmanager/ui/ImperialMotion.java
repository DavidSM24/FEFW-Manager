package com.davidsm24.fefwmanager.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.view.View;

public final class ImperialMotion {
    private ImperialMotion() {
    }

    public static void reveal(View heading) {
        if (!animationsEnabled(heading.getContext())) {
            return;
        }
        heading.setAlpha(0.86f);
        heading.setTranslationY(5f * heading.getResources().getDisplayMetrics().density);
        heading.animate().alpha(1f).translationY(0f).setDuration(180).start();
    }

    static boolean animationsEnabled(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? ValueAnimator.areAnimatorsEnabled()
                : Settings.Global.getFloat(context.getContentResolver(),
                        Settings.Global.ANIMATOR_DURATION_SCALE, 1f) > 0f;
    }
}
