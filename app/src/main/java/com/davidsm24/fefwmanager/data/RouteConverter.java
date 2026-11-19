package com.davidsm24.fefwmanager.data;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

import com.davidsm24.fefwmanager.route.Route;

public final class RouteConverter {
    @TypeConverter
    @NonNull
    public static String toId(@NonNull Route route) {
        return route.id;
    }

    @TypeConverter
    @NonNull
    public static Route fromId(@NonNull String id) {
        Route route = Route.fromId(id);
        if (route == null) {
            throw new IllegalArgumentException("Unknown route: " + id);
        }
        return route;
    }
}
