package com.davidsm24.fefwmanager.units;

import java.util.Comparator;

public record UnitLabel(String id, String name) {
    public static final Comparator<UnitLabel> BY_NAME = Comparator
            .comparing(UnitLabel::name, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(UnitLabel::id);

    public String initial() {
        return name.isEmpty() ? "" : name.substring(0, name.offsetByCodePoints(0, 1));
    }
}
