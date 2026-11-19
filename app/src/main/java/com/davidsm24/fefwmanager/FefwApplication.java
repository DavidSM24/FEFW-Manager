package com.davidsm24.fefwmanager;

import android.app.Application;

import androidx.room.Room;

import com.davidsm24.fefwmanager.data.ProgressDatabase;
import com.davidsm24.fefwmanager.data.RouteProgressDao;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class FefwApplication extends Application {
    private final Executor missionWrites = Executors.newSingleThreadExecutor();
    private ProgressDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = Room.databaseBuilder(this, ProgressDatabase.class, "route-progress.db").build();
    }

    public RouteProgressDao routeProgress() {
        return database.routeProgress();
    }

    public Executor missionWrites() {
        return missionWrites;
    }
}
