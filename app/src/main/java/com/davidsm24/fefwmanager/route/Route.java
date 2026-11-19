package com.davidsm24.fefwmanager.route;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.davidsm24.fefwmanager.R;

public enum Route {
    CAI("cai", R.string.route_cai, R.color.route_cai),
    THEODORA("theodora", R.string.route_theodora, R.color.route_theodora),
    DIETRICH("dietrich", R.string.route_dietrich, R.color.route_dietrich),
    LEDA("leda", R.string.route_leda, R.color.route_leda);

    public final String id;
    @StringRes public final int nameResource;
    @ColorRes public final int accentResource;

    Route(String id, int nameResource, int accentResource) {
        this.id = id;
        this.nameResource = nameResource;
        this.accentResource = accentResource;
    }

    @Nullable
    public static Route fromId(@Nullable String id) {
        for (Route route : values()) {
            if (route.id.equals(id)) {
                return route;
            }
        }
        return null;
    }
}
