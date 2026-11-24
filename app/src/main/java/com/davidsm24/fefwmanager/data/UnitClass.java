package com.davidsm24.fefwmanager.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "unit_classes")
public final class UnitClass {
    @NonNull @PrimaryKey public final String id;
    @NonNull public final String name;
    @NonNull @ColumnInfo(name = "image_png") public final byte[] imagePng;

    public UnitClass(@NonNull String id, @NonNull String name, @NonNull byte[] imagePng) {
        this.id = id;
        this.name = name;
        this.imagePng = imagePng;
    }
}
