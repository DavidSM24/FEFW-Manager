package com.davidsm24.fefwmanager.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.davidsm24.fefwmanager.route.Route;

@Entity(tableName = "main_missions")
public final class MainMission {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "route_id")
    public final Route route;
    public final int day;
    public final int month;
    @NonNull public final String description;

    public MainMission(@NonNull Route route, int day, int month, @NonNull String description) {
        int daysInMonth = switch (month) {
            case 2 -> 29;
            case 4, 6, 9, 11 -> 30;
            default -> 31;
        };
        if (month < 1 || month > 12 || day < 1 || day > daysInMonth) {
            throw new IllegalArgumentException("Invalid mission date");
        }
        this.route = route;
        this.day = day;
        this.month = month;
        this.description = description;
    }
}
