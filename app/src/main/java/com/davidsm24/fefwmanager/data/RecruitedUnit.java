package com.davidsm24.fefwmanager.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.davidsm24.fefwmanager.route.Route;

@Entity(tableName = "recruited_units", primaryKeys = {"route_id", "unit_id"})
public final class RecruitedUnit {
    @NonNull @ColumnInfo(name = "route_id") public final Route route;
    @NonNull @ColumnInfo(name = "unit_id") public final String unitId;

    public RecruitedUnit(@NonNull Route route, @NonNull String unitId) {
        this.route = route;
        this.unitId = unitId;
    }
}
