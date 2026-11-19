package com.davidsm24.fefwmanager.data;

import androidx.annotation.Nullable;

public final class RouteProgress {
    @Nullable public final Integer day;
    @Nullable public final Integer month;
    @Nullable public final String description;
    public final int unitCount;
    public final int supportCount;

    public RouteProgress(@Nullable Integer day, @Nullable Integer month, @Nullable String description,
            int unitCount, int supportCount) {
        this.day = day;
        this.month = month;
        this.description = description;
        this.unitCount = unitCount;
        this.supportCount = supportCount;
    }
}
