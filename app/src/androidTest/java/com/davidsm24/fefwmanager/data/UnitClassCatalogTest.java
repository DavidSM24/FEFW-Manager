package com.davidsm24.fefwmanager.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public final class UnitClassCatalogTest {
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private final String name = "class-catalog-test-" + UUID.randomUUID() + ".db";

    @Test
    public void createsAndReopensTheCatalogWithItsImages() {
        try {
            assertStoredCatalog();
            assertStoredCatalog();
        } finally {
            context.deleteDatabase(name);
        }
    }

    @Test
    public void migratesVersionOneWithoutLosingSavedProgress() {
        ProgressDatabase database = ProgressDatabase.open(context, name);
        try {
            createVersionOneDatabase();
            assertCatalog(database.unitClasses().getAll());
            SupportSQLiteDatabase migrated = database.getOpenHelper().getReadableDatabase();
            assertEquals(3, migrated.getVersion());
            assertRowCount(migrated, 1, """
                    SELECT * FROM main_missions WHERE route_id = 'cai'
                    AND day = 10 AND month = 5 AND description = 'Guard the gate'
                    """);
            assertRowCount(migrated, 2, """
                    SELECT * FROM recruited_units WHERE route_id = 'cai'
                    AND unit_id IN ('peter', 'tialla') AND class_id IS NULL
                    """);
            assertRowCount(migrated, 1, """
                    SELECT * FROM support_pairs WHERE route_id = 'cai'
                    AND first_unit_id = 'peter' AND second_unit_id = 'tialla'
                    """);
        } finally {
            database.close();
            context.deleteDatabase(name);
        }
    }

    private void assertStoredCatalog() {
        ProgressDatabase database = ProgressDatabase.open(context, name);
        try {
            assertCatalog(database.unitClasses().getAll());
        } finally {
            database.close();
        }
    }

    private void createVersionOneDatabase() {
        try (SQLiteDatabase database = context.openOrCreateDatabase(name, Context.MODE_PRIVATE, null)) {
            database.execSQL("""
                    CREATE TABLE main_missions (
                        route_id TEXT NOT NULL PRIMARY KEY, day INTEGER NOT NULL,
                        month INTEGER NOT NULL, description TEXT NOT NULL
                    )
                    """);
            database.execSQL("""
                    CREATE TABLE recruited_units (
                        route_id TEXT NOT NULL, unit_id TEXT NOT NULL, PRIMARY KEY (route_id, unit_id)
                    )
                    """);
            database.execSQL("""
                    CREATE TABLE support_pairs (
                        route_id TEXT NOT NULL, first_unit_id TEXT NOT NULL, second_unit_id TEXT NOT NULL,
                        PRIMARY KEY (route_id, first_unit_id, second_unit_id)
                    )
                    """);
            database.execSQL("CREATE TABLE room_master_table (id INTEGER PRIMARY KEY, identity_hash TEXT)");
            database.execSQL("""
                    INSERT INTO room_master_table VALUES (42, '3a7e239360c180e8c7f1005018592a9b')
                    """);
            database.execSQL("INSERT INTO main_missions VALUES ('cai', 10, 5, 'Guard the gate')");
            database.execSQL("INSERT INTO recruited_units VALUES ('cai', 'peter'), ('cai', 'tialla')");
            database.execSQL("INSERT INTO support_pairs VALUES ('cai', 'peter', 'tialla')");
            database.setVersion(1);
        }
    }

    private static void assertCatalog(List<UnitClass> classes) {
        assertEquals(60, classes.size());
        assertEquals("Archer", classes.get(0).name);
        Set<String> ids = new HashSet<>();
        for (UnitClass unitClass : classes) {
            assertTrue(ids.add(unitClass.id));
            BitmapFactory.Options image = new BitmapFactory.Options();
            image.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(unitClass.imagePng, 0, unitClass.imagePng.length, image);
            assertEquals(unitClass.id, "image/png", image.outMimeType);
            assertTrue(unitClass.id, image.outWidth > 0 && image.outHeight > 0);
        }
        assertTrue(ids.contains("celestial-trooper"));
        assertTrue(ids.contains("wing-soldier"));
    }

    private static void assertRowCount(SupportSQLiteDatabase database, int expected, String query) {
        try (Cursor cursor = database.query(query)) {
            assertEquals(expected, cursor.getCount());
        }
    }
}
