package com.davidsm24.fefwmanager.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;

import com.davidsm24.fefwmanager.R;

public final class FloatingCards {
    private FloatingCards() {}

    public static void show(Activity activity, AlertDialog card) {
        card.setCanceledOnTouchOutside(true);
        card.show();
        View content = activity.findViewById(android.R.id.content);
        int gutter = activity.getResources().getDimensionPixelSize(R.dimen.screen_gutter);
        int maximum = activity.getResources().getDimensionPixelSize(R.dimen.floating_dialog_max_width);
        int width = Math.min(content.getWidth() - 2 * gutter, maximum);
        card.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}
