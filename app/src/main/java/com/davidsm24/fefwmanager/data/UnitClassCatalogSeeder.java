package com.davidsm24.fefwmanager.data;

import android.content.res.AssetManager;

import androidx.sqlite.db.SupportSQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

final class UnitClassCatalogSeeder {
    private static final String DIRECTORY = "unit-classes/";

    private UnitClassCatalogSeeder() {
    }

    static void seed(SupportSQLiteDatabase database, AssetManager assets) {
        try {
            JSONArray classes = new JSONArray(new String(readAsset(assets, DIRECTORY + "catalog.json"),
                    StandardCharsets.UTF_8));
            for (int i = 0; i < classes.length(); i++) {
                JSONObject unitClass = classes.getJSONObject(i);
                String id = unitClass.getString("id");
                byte[] image = readAsset(assets, DIRECTORY + id + ".png");
                database.execSQL("INSERT INTO unit_classes (id, name, image_png) VALUES (?, ?, ?)",
                        new Object[]{id, unitClass.getString("name"), image});
            }
        } catch (IOException | JSONException exception) {
            throw new IllegalStateException("Unable to import the bundled unit classes", exception);
        }
    }

    private static byte[] readAsset(AssetManager assets, String path) throws IOException {
        try (InputStream input = assets.open(path)) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int count;
            while ((count = input.read(buffer)) != -1) {
                bytes.write(buffer, 0, count);
            }
            return bytes.toByteArray();
        }
    }
}
