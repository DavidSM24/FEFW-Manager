package com.davidsm24.fefwmanager.data;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {MainMission.class, RecruitedUnit.class, SupportPair.class, UnitClass.class},
        version = 3)
@TypeConverters(RouteConverter.class)
public abstract class ProgressDatabase extends RoomDatabase {
    private static final Migration ADD_RECRUITED_CLASS = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("""
                    ALTER TABLE `recruited_units` ADD COLUMN `class_id` TEXT
                    REFERENCES `unit_classes` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION
                    """);
            database.execSQL("CREATE INDEX `index_recruited_units_class_id` ON `recruited_units` (`class_id`)");
        }
    };

    public static ProgressDatabase open(Context context, String name) {
        Context application = context.getApplicationContext();
        AssetManager assets = application.getAssets();
        return Room.databaseBuilder(application, ProgressDatabase.class, name)
                .addMigrations(new Migration(1, 2) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        database.execSQL("""
                                CREATE TABLE IF NOT EXISTS `unit_classes` (
                                    `id` TEXT NOT NULL PRIMARY KEY,
                                    `name` TEXT NOT NULL,
                                    `image_png` BLOB NOT NULL
                                )
                                """);
                        UnitClassCatalogSeeder.seed(database, assets);
                    }
                }, ADD_RECRUITED_CLASS)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase database) {
                        UnitClassCatalogSeeder.seed(database, assets);
                    }
                })
                .build();
    }

    public abstract RouteProgressDao routeProgress();

    public abstract UnitClassDao unitClasses();
}
