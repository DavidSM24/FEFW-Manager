package com.davidsm24.fefwmanager.units;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;

import com.davidsm24.fefwmanager.R;

public final class StatLevelBar extends View {
    private static final int MIN_LEVEL = 1;
    private static final int MAX_LEVEL = 5;
    private static final int INACTIVE_ALPHA = Math.round(0.18f * 255);

    private final Paint segmentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float segmentGap;
    private int level = MIN_LEVEL;
    @ColorInt private int levelColor;

    public StatLevelBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        segmentGap = getResources().getDimension(R.dimen.stat_segment_gap);
        levelColor = getStatLevelColor(level);
    }

    void setLevel(int score) {
        int boundedLevel = Math.max(MIN_LEVEL, Math.min(MAX_LEVEL, score));
        if (boundedLevel != level) {
            level = boundedLevel;
            levelColor = getStatLevelColor(level);
            invalidate();
        }
    }

    int getLevel() {
        return level;
    }

    @ColorInt
    private int getStatLevelColor(int level) {
        return getContext().getColor(switch (level) {
            case 1 -> R.color.stat_level_1;
            case 2 -> R.color.stat_level_2;
            case 3 -> R.color.stat_level_3;
            case 4 -> R.color.stat_level_4;
            default -> R.color.stat_level_5;
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth() - getPaddingLeft() - getPaddingRight();
        float top = getPaddingTop();
        float bottom = getHeight() - getPaddingBottom();
        if (width <= 0 || bottom <= top) {
            return;
        }

        float gap = Math.min(segmentGap, width / (2f * MAX_LEVEL));
        float segmentWidth = (width - (MAX_LEVEL - 1) * gap) / MAX_LEVEL;
        float radius = Math.min(segmentWidth, bottom - top) / 2f;
        boolean rtl = getLayoutDirection() == LAYOUT_DIRECTION_RTL;
        segmentPaint.setColor(levelColor);
        for (int index = 0; index < MAX_LEVEL; index++) {
            boolean active = rtl ? index >= MAX_LEVEL - level : index < level;
            segmentPaint.setAlpha(active ? 255 : INACTIVE_ALPHA);
            float left = getPaddingLeft() + index * (segmentWidth + gap);
            canvas.drawRoundRect(left, top, left + segmentWidth, bottom, radius, radius, segmentPaint);
        }
    }
}
