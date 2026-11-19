package com.davidsm24.fefwmanager;

import android.app.Application;

import androidx.room.Room;

import com.davidsm24.fefwmanager.data.ProgressDatabase;
import com.davidsm24.fefwmanager.data.RouteProgressDao;

public final class FefwApplication extends Application {
    private ProgressDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = Room.databaseBuilder(this, ProgressDatabase.class, "route-progress.db").build();
    }

    public RouteProgressDao routeProgress() {
        return database.routeProgress();
    }
}
