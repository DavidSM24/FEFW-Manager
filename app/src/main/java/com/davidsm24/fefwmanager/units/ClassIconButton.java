package com.davidsm24.fefwmanager.units;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageButton;

public final class ClassIconButton extends ImageButton {
    private static final long HOLD_MILLIS = 1_000;

    private final int touchSlop;
    private final Runnable inspectAfterHold = () -> {
        if (isPressed() && isEnabled() && isLongClickable()) {
            cancelHold();
            performLongClick();
        }
    };
    private float downX;
    private float downY;

    public ClassIconButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || !isLongClickable()) {
            cancelHold();
            return super.onTouchEvent(event);
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN -> {
                downX = event.getX();
                downY = event.getY();
                setPressed(true);
                postDelayed(inspectAfterHold, HOLD_MILLIS);
            }
            case MotionEvent.ACTION_MOVE -> {
                if (Math.abs(event.getX() - downX) > touchSlop
                        || Math.abs(event.getY() - downY) > touchSlop) {
                    cancelHold();
                }
            }
            case MotionEvent.ACTION_UP -> {
                boolean tapped = isPressed();
                cancelHold();
                if (tapped) {
                    performClick();
                }
            }
            case MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_POINTER_DOWN -> cancelHold();
            default -> { }
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    void cancelHold() {
        removeCallbacks(inspectAfterHold);
        setPressed(false);
    }

    @Override
    protected void onDetachedFromWindow() {
        cancelHold();
        super.onDetachedFromWindow();
    }
}
