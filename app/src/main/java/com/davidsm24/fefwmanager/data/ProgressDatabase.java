package com.davidsm24.fefwmanager.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {MainMission.class, RecruitedUnit.class, SupportPair.class}, version = 1)
@TypeConverters(RouteConverter.class)
public abstract class ProgressDatabase extends RoomDatabase {
    public abstract RouteProgressDao routeProgress();
}
