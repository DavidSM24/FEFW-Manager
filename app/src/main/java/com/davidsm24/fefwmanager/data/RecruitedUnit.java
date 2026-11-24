package com.davidsm24.fefwmanager.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import com.davidsm24.fefwmanager.route.Route;

@Entity(tableName = "recruited_units", primaryKeys = {"route_id", "unit_id"},
        foreignKeys = @ForeignKey(entity = UnitClass.class, parentColumns = "id", childColumns = "class_id"),
        indices = @Index("class_id"))
public final class RecruitedUnit {
    @NonNull @ColumnInfo(name = "route_id") public final Route route;
    @NonNull @ColumnInfo(name = "unit_id") public final String unitId;
    @Nullable @ColumnInfo(name = "class_id") public final String classId;

    @Ignore
    public RecruitedUnit(@NonNull Route route, @NonNull String unitId) {
        this(route, unitId, null);
    }

    public RecruitedUnit(@NonNull Route route, @NonNull String unitId, @Nullable String classId) {
        this.route = route;
        this.unitId = unitId;
        this.classId = classId;
    }
}
