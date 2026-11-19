package com.davidsm24.fefwmanager.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.davidsm24.fefwmanager.route.Route;

@Entity(tableName = "support_pairs", primaryKeys = {"route_id", "first_unit_id", "second_unit_id"})
public final class SupportPair {
    @NonNull @ColumnInfo(name = "route_id") public final Route route;
    @NonNull @ColumnInfo(name = "first_unit_id") public final String firstUnitId;
    @NonNull @ColumnInfo(name = "second_unit_id") public final String secondUnitId;

    public SupportPair(@NonNull Route route, @NonNull String firstUnitId,
            @NonNull String secondUnitId) {
        int order = firstUnitId.compareTo(secondUnitId);
        if (order == 0) {
            throw new IllegalArgumentException("A support requires two different units");
        }
        this.route = route;
        this.firstUnitId = order < 0 ? firstUnitId : secondUnitId;
        this.secondUnitId = order < 0 ? secondUnitId : firstUnitId;
    }
}
